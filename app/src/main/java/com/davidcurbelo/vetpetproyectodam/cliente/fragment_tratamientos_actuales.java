package com.davidcurbelo.vetpetproyectodam.cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class fragment_tratamientos_actuales extends Fragment {
    private View myView;
    private String id_mascota;
    private TabLayout tabLayout;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<List<String>> tratamientosMascota;
    private List<String> tratamientoMascota;
    private RecyclerView rvTratamientosMascota;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public fragment_tratamientos_actuales(View view, String id){
        myView = view;
        id_mascota = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_tratamientos_actuales, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tabLayout = myView.findViewById(R.id.tablayout_tratamientos);

        rvTratamientosMascota = view.findViewById(R.id.recyclerview_tratamientos_actuales);
        rvTratamientosMascota.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvTratamientosMascota.setLayoutManager(layoutManager);
        tratamientosMascota = new ArrayList<>();
        tratamientoMascota = new ArrayList<>();
        adapter = new MyAdapterTramientosActuales(tratamientosMascota, tratamientoMascota, getActivity());

        final DatabaseReference ref_usuario = mDatabase.child("usuarios").child(user.getUid());
        ref_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                final String id_clinica = data.get("clinica").toString();
                DatabaseReference ref_mascota = ref_usuario.child("mascotas").child(id_mascota);
                ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        final String nombre = data.get("nombre").toString();
                        final String imagen = data.get("foto").toString();
                        final DatabaseReference ref_diagnosticos = mDatabase.child("diagnosticos").child(id_clinica);
                        ref_diagnosticos.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int num_diagnosticos = (int) dataSnapshot.getChildrenCount();
                                final int[] contador_aux = {0};
                                for (int i = 1; i <= num_diagnosticos; i++) {
                                    final DatabaseReference ref_diagnostico = ref_diagnosticos.child(String.valueOf(i));
                                    ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                            String id_mascota_BD = data.get("id_mascota").toString();
                                            if(id_mascota_BD.equals(id_mascota)){
                                                DatabaseReference ref_tratamientos = ref_diagnostico.child("tratamiento");
                                                ref_tratamientos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                                        Calendar now = Calendar.getInstance();
                                                        int mYear = now.get(Calendar.YEAR);
                                                        int mMonth = now.get(Calendar.MONTH);
                                                        int mDay = now.get(Calendar.DAY_OF_MONTH);
                                                        String fecha = mDay + "/" + (mMonth+1) + "/" + mYear;
                                                        DateFormat format = new SimpleDateFormat("dd/MM/yyyyy");
                                                        if(!data.get("fecha_fin").toString().equalsIgnoreCase("Crónico") && !data.get("fecha_fin").toString().equalsIgnoreCase("Sin fecha")){
                                                            try {
                                                                // Fecha de finalizacion del tratamiento
                                                                Date date1 = format.parse(data.get("fecha_fin").toString());
                                                                // Fecha actual
                                                                Date date2 = format.parse(fecha);
                                                                // Solo guardar los tratamientos que sean actuales, es decir, donde la fecha actual este antes que la fecha de finalizacion del tratamiento
                                                                if(date2.before(date1)){
                                                                    tratamientoMascota.add(0, data.get("fecha_inicio").toString());
                                                                    tratamientoMascota.add(1, data.get("fecha_fin").toString());
                                                                    tratamientoMascota.add(2, data.get("farmaco").toString());
                                                                    tratamientoMascota.add(3, data.get("dosis").toString());
                                                                    tratamientoMascota.add(4, data.get("administracion").toString());
                                                                    tratamientoMascota.add(5, nombre);
                                                                    tratamientoMascota.add(6, id_mascota);
                                                                    tratamientoMascota.add(7, imagen);
                                                                    tratamientosMascota.add(contador_aux[0], tratamientoMascota);
                                                                    tratamientoMascota = new ArrayList<>(tratamientoMascota);
                                                                    rvTratamientosMascota.setAdapter(adapter);
                                                                    contador_aux[0]++;
                                                                }
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else {
                                                            tratamientoMascota.add(0, data.get("fecha_inicio").toString());
                                                            tratamientoMascota.add(1, data.get("fecha_fin").toString());
                                                            tratamientoMascota.add(2, data.get("farmaco").toString());
                                                            tratamientoMascota.add(3, data.get("dosis").toString());
                                                            tratamientoMascota.add(4, data.get("administracion").toString());
                                                            tratamientoMascota.add(5, nombre);
                                                            tratamientoMascota.add(6, id_mascota);
                                                            tratamientoMascota.add(7, imagen);
                                                            tratamientosMascota.add(contador_aux[0], tratamientoMascota);
                                                            tratamientoMascota = new ArrayList<>(tratamientoMascota);
                                                            rvTratamientosMascota.setAdapter(adapter);
                                                            contador_aux[0]++;
                                                        }
                                                        // Crear el Badge que mostrara el numero de tratamientos actuales
                                                        BadgeDrawable badge = tabLayout.getTabAt(0).getOrCreateBadge();
                                                        badge.setVisible(true);
                                                        badge.setNumber(contador_aux[0]);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("ERROR0", "onCancelled: "+databaseError);
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("ERROR1", "onCancelled: "+databaseError);
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR0", "onCancelled: "+databaseError);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR0", "onCancelled: "+databaseError);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });

        return view;
    }
}
