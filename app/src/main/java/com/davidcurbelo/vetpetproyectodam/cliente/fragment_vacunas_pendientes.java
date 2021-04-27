package com.davidcurbelo.vetpetproyectodam.cliente;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fragment_vacunas_pendientes extends Fragment {
    private View myView;
    private String id_mascota;
    private String especie;
    private TabLayout tabLayout;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<List<String>> vacunasMascota;
    private List<String> vacunaMascota;
    private RecyclerView rvVacunasMascota;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public fragment_vacunas_pendientes(View view, String id, String especie) {
        myView = view;
        this.especie = especie;
        id_mascota = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vacunas_pendientes, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tabLayout = myView.findViewById(R.id.tablayout_vacunas);

        rvVacunasMascota = view.findViewById(R.id.recyclerview_vacunas_pendientes);
        rvVacunasMascota.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvVacunasMascota.setLayoutManager(layoutManager);
        vacunasMascota = new ArrayList<>();
        vacunaMascota = new ArrayList<>();
        adapter = new MyAdapterVacunasPendientes(vacunasMascota, vacunaMascota, getActivity());

        DatabaseReference ref_vacunas = mDatabase.child("usuarios").child(user.getUid()).child("mascotas").child(id_mascota).child("vacunas");
        ref_vacunas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                // Numero de vacunas de la mascota
                int num_vacunas = (int) dataSnapshot.getChildrenCount();
                // ID de la ultima vacuna
                int id_ultima_vacuna = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    id_ultima_vacuna = Integer.parseInt(child.getKey());
                }
                final int[] contador_aux = {0};
                final int[] contador_badge = {0};
                // Recorrer todas las vacunas
                for (int i = 0; i <= id_ultima_vacuna; i++){
                    final int finalI = i;
                    getNumeroDosisVacuna(new idCallback() {
                        @Override
                        public void onCallback(int num_dosis_vacuna, String tipo_ultima_dosis) {
                            // Acceder a una vacuna individual
                            DataSnapshot a = dataSnapshot.child(String.valueOf(finalI));
                            // Controlar que algun ID de vacuna no existe (Pueden no estar en orden los ID de las vacunas puestas a la mascota)
                            if(a.exists()){
                                // Numero de dosis de esa vacuna individual
                                int num_dosis = (int) a.getChildrenCount();
                                // Acceder a la ultima dosis de esa vacuna individual
                                DataSnapshot b = a.child("dosis_"+num_dosis);
                                // Obtener los datos de la última dosis de esa vacuna individual
                                Map<String, Object> data = (Map<String, Object>) b.getValue();
                                vacunaMascota.add(0, data.get("nombre").toString());
                                vacunaMascota.add(1, data.get("fecha").toString());
                                vacunaMascota.add(2, String.valueOf(num_dosis));
                                vacunaMascota.add(3, String.valueOf(num_dosis_vacuna));
                                vacunaMascota.add(4, tipo_ultima_dosis);
                                vacunasMascota.add(contador_aux[0], vacunaMascota);
                                vacunaMascota = new ArrayList<>(vacunaMascota);
                                rvVacunasMascota.setAdapter(adapter);
                                contador_aux[0]++;
                                if(num_dosis == num_dosis_vacuna){
                                    if(tipo_ultima_dosis.contains("Anual")){
                                        contador_badge[0]++;
                                    }
                                }else{
                                    contador_badge[0]++;
                                }
                                // Crear el Badge que mostrara el numero de tratamientos actuales
                                BadgeDrawable badge = tabLayout.getTabAt(1).getOrCreateBadge();
                                badge.setVisible(true);
                                badge.setNumber(contador_badge[0]);
                            }
                        }
                    }, String.valueOf(i));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });

        return view;
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(int id, String tipo_ultima_dosis);
    }

    // Obtener el numero de dosis necesarias de una vacuna
    public void getNumeroDosisVacuna(final idCallback myCallback, String id_vacuna) {
        String especie_final = "";
        if(especie.equalsIgnoreCase("perro")){
            especie_final = "perros";
        }else if(especie.equalsIgnoreCase("gato")){
            especie_final = "gatos";
        }
        mDatabase.child("vacunas").child(especie_final).child(id_vacuna).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num_dosis = (int) dataSnapshot.getChildrenCount() -1;
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Obtener el texto de la ultima dosis para saber que tipo de dosis es (Anual o no)
                String tipo_ultima_dosis = data.get("dosis_"+(num_dosis)).toString();
                myCallback.onCallback(num_dosis, tipo_ultima_dosis);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}
