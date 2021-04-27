package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class pantalla_historial_citas_admin extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;

    private List<List<Object>> citas;
    private List<Object> cita;
    private RecyclerView rvCitas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_historial_citas_admin);

        toolbar = this.findViewById(R.id.toolbar_historial_citas_admin);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constraintLayout = this.findViewById(R.id.layout_historial_citas_admin);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvCitas = this.findViewById(R.id.recyclerview_historial_citas_admin);
        rvCitas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCitas.setLayoutManager(layoutManager);
        citas = new ArrayList<>();
        cita = new ArrayList<>();
        adapter = new MyAdapterCitasAdmin(citas, cita, getApplicationContext(), 2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAdapter();
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id);
    }

    // Obtener el ID de la clinica del veterinario
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

    // Obtener ruta de imagen de la mascota
    public void getImagenMascotaFromFirebase(final idCallback myCallback, String id_usuario, String id_mascota) {
        // Referencia a una mascota especifica en la BD
        mDatabase.child("usuarios").child(id_usuario).child("mascotas").child(id_mascota).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String foto = data.get("foto").toString();
                myCallback.onCallback(foto);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }

    // Llamar al adaptador del RecyclerView con los datos de las consultas de la clinica
    public void loadAdapter() {
        // Fecha actual
        final Date date = new Date();
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                // Referencia a las citas de la clínica
                final DatabaseReference ref_citas = mDatabase.child("citas").child(id_clinica);
                ref_citas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Obtener las citas ordenadas por fecha menores que la fecha actual
                        Query query = ref_citas.orderByChild("timestamp").endAt(date.getTime());
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final int[] aux_contador = {0};
                                // Recorrer cada cita
                                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                    final Map<String, Object> data = (Map<String, Object>) ds.getValue();
                                    getImagenMascotaFromFirebase(new idCallback() {
                                        @Override
                                        public void onCallback(String foto) {
                                            cita.add(0, foto);
                                            cita.add(1, data.get("mascota"));
                                            cita.add(2, data.get("nombre_cliente"));
                                            cita.add(3, data.get("motivo"));
                                            cita.add(4, data.get("dia"));
                                            cita.add(5, data.get("hora"));
                                            cita.add(6, data.get("estado"));
                                            citas.add(aux_contador[0], cita);
                                            aux_contador[0]++;
                                            cita = new ArrayList<>(cita);
                                            rvCitas.setAdapter(adapter);
                                            // Si hay citas cerradas, se cambia el fondo predeterminado por un color
                                            constraintLayout.setBackgroundColor(getResources().getColor(R.color.color_fondo));
                                        }
                                    }, data.get("id_cliente").toString(), data.get("id_mascota").toString());
                                }
                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){
                                Log.d("ERROR2", "onCancelled: " + databaseError);
                            }
                        };
                        query.addListenerForSingleValueEvent(valueEventListener);
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
