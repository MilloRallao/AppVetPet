package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.davidcurbelo.vetpetproyectodam.R;
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

public class pantalla_diagnosticos_mascota_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;
    private Button agregar_diagnostico;

    private List<List<String>> diagnosticos;
    private List<String> diagnostico;
    private RecyclerView rvDiagnostico;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String id_mascota;
    private String id_cliente;
    private int num_diagnosticos;
    private List<String> id_diagnosticos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_diagnosticos_mascota_cliente);

        constraintLayout = this.findViewById(R.id.layout_diagnosticos_mascota_cliente);

        toolbar = this.findViewById(R.id.toolbar_diagnosticos_mascota_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Ir hacia atras con el boton de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id_mascota = getIntent().getExtras().getString("id_mascota");
        id_cliente = getIntent().getExtras().getString("id_cliente");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvDiagnostico = this.findViewById(R.id.recyclerview_diagnosticos_mascota_cliente);
        rvDiagnostico.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvDiagnostico.setLayoutManager(layoutManager);
        diagnosticos = new ArrayList<>();
        diagnostico = new ArrayList<>();
        adapter = new MyAdapterDiagnosticosMascotaCliente(diagnosticos, diagnostico, getApplicationContext());

        agregar_diagnostico = this.findViewById(R.id.button_agregar_diagnostico_mascota_cliente);
        agregar_diagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_agregar_diagnostico_tratamiento.class);
                intent.putExtra("id_mascota", id_mascota);
                intent.putExtra("id_cliente", id_cliente);
                intent.putExtra("agregar_actualizar", 0);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        diagnosticosMascota();
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
                String clinica_id = data.get("clinica").toString();
                myCallback.onCallback(clinica_id);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }

    // Cargar adaptador para el Recyclerview de los datos de los tratamientos de la mascota
    public void loadAdapter() {
        final DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
        ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Poner título de la Toolbar
                String nombre_mascota = data.get("nombre").toString();
                getSupportActionBar().setTitle("Diagnósticos de " + nombre_mascota);
                if(num_diagnosticos != 0){
                    constraintLayout.setBackgroundResource(R.color.color_fondo);
                    getIdClinicaFromFirebase(new idCallback() {
                        @Override
                        public void onCallback(final String id_clinica) {
                            // Referencia a los diagnosticas de la clinica
                            final DatabaseReference ref_diagnosticos = mDatabase.child("diagnosticos").child(id_clinica);
                            ref_diagnosticos.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final int[] aux_contador_lista = {0};
                                    int num_diagnosticos_total = (int) dataSnapshot.getChildrenCount();
                                    // Recorrer todas los diagnosticos de la clinica
                                    for(int i= 1; i <= num_diagnosticos_total; i++){
                                        ref_diagnosticos.child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                                // Si el ID de la mascota coincide con el ID de la mascota de la consulta, se guardan los datos
                                                String id_mascota_BD = data.get("id_mascota").toString();
                                                if(id_mascota_BD.equals(id_mascota)){
                                                    diagnostico.add(0, data.get("fecha").toString());
                                                    diagnostico.add(1, data.get("diagnostico").toString());
                                                    diagnostico.add(2, data.get("anamnesis").toString());
                                                    diagnostico.add(3, data.get("pruebas").toString());
                                                    diagnostico.add(4, dataSnapshot.child("tratamiento").child("farmaco").getValue().toString());
                                                    diagnostico.add(5, id_mascota);
                                                    diagnostico.add(6, id_cliente);
                                                    diagnostico.add(7, dataSnapshot.getKey());
                                                    diagnostico.add(8, id_clinica);
                                                    diagnosticos.add(aux_contador_lista[0], diagnostico);
                                                    aux_contador_lista[0]++;
                                                    diagnostico = new ArrayList<>(diagnostico);
                                                    rvDiagnostico.setAdapter(adapter);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d("ERROR2", "onCancelled: "+databaseError);
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERROR3", "onCancelled: "+databaseError);
                                }
                            });
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR4", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener todos los diagnosticos de la mascota del cliente
    private void diagnosticosMascota(){
        DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("diagnosticos");
        ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_diagnosticos = new ArrayList<>();
                // Obtener todos los diagnosticos de la mascota
                Map<String, Object> data2 = (Map<String, Object>) dataSnapshot.getValue();
                // Obtener la cantidad de diagnosticos de la mascota
                num_diagnosticos = (int) dataSnapshot.getChildrenCount();
                // Guardar cada ID de consulta en la lista
                id_diagnosticos.add(0, "");
                for (int i= 1; i <= num_diagnosticos; i++){
                    id_diagnosticos.add(i, data2.get("diagnostico_"+i).toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR5", "onCancelled: "+databaseError);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        diagnosticos.clear();
    }
}
