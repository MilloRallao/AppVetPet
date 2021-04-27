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

public class fragment_diagnosticos extends Fragment {
    private View myView;
    private String id_mascota;
    private TabLayout tabLayout;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<List<String>> diagnosticosMascota;
    private List<String> diagnosticoMascota;
    private RecyclerView rvDiagnosticosMascota;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private long num_diagnosticos;
    private String clinica_id;
    private String imagen;

    public fragment_diagnosticos(View view, String id) {
        myView = view;
        id_mascota = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagnosticos, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tabLayout = myView.findViewById(R.id.tablayout_vacunas);

        rvDiagnosticosMascota = view.findViewById(R.id.recyclerview_diagnosticos_hechos);
        rvDiagnosticosMascota.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvDiagnosticosMascota.setLayoutManager(layoutManager);
        diagnosticosMascota = new ArrayList<>();
        diagnosticoMascota = new ArrayList<>();
        adapter = new MyAdapterDiagnosticosHechos(diagnosticosMascota, diagnosticoMascota, getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAdapter();
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id);
    }

    // Obtener el id de la clinica del usuario
    public void getIdClinicaFromFirebase(final idCallback myCallback) {
        // Referencia a los datos del usuario en la BD
        mDatabase.child("usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                clinica_id = data.get("clinica").toString();
                myCallback.onCallback(clinica_id);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    // Obtener ruta de imagen de la mascota
    public void getImagenMascotaFromFirebase(final idCallback myCallback, String id) {
        // Referencia a una mascota especifica en la BD
        mDatabase.child("usuarios").child(user.getUid()).child("mascotas").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                imagen = data.get("foto").toString();
                myCallback.onCallback(imagen);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public void loadAdapter(){
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                // Referencia a los diagnosticas de la clinica del usuario
                final DatabaseReference ref_diagnosticos = mDatabase.child("diagnosticos").child(id_clinica);
                ref_diagnosticos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final int[] aux_contador_lista = {0};
                        int num_diagnosticos_total = (int) dataSnapshot.getChildrenCount();
                        // Recorrer todas los diagnosticos de la clinica
                        for(int i= 1; i <= num_diagnosticos_total; i++){
                            final DatabaseReference ref_diagnostico = ref_diagnosticos.child(String.valueOf(i));
                            ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    // Si el ID de la mascota coincide con el ID de la mascota de la consulta, se guardan los datos
                                    if(data.get("id_mascota").toString().equals(id_mascota)){
                                        getImagenMascotaFromFirebase(new idCallback() {
                                            @Override
                                            public void onCallback(final String imagen_mascota) {
                                                ref_diagnostico.child("tratamiento").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        final Map<String, Object> dataTratamiento = (Map<String, Object>) dataSnapshot.getValue();
                                                        diagnosticoMascota.add(0, data.get("fecha").toString());
                                                        diagnosticoMascota.add(1, data.get("diagnostico").toString());
                                                        diagnosticoMascota.add(2, data.get("anamnesis").toString());
                                                        diagnosticoMascota.add(3, data.get("pruebas").toString());
                                                        diagnosticoMascota.add(4, dataTratamiento.get("farmaco").toString());
                                                        diagnosticoMascota.add(5, data.get("mascota").toString());
                                                        diagnosticoMascota.add(6, data.get("id_mascota").toString());
                                                        diagnosticoMascota.add(7, imagen_mascota);
                                                        diagnosticosMascota.add(aux_contador_lista[0], diagnosticoMascota);
                                                        aux_contador_lista[0]++;
                                                        diagnosticoMascota = new ArrayList<>(diagnosticoMascota);
                                                        rvDiagnosticosMascota.setAdapter(adapter);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("ERROR0", "onCancelled: "+databaseError);
                                                    }
                                                });

                                            }
                                        }, id_mascota);
                                    }
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
        });
    }
}
