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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class pantalla_ver_citas_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;

    private List<List<Object>> citas;
    private List<Object> cita;
    private RecyclerView rvCitas;
    private RecyclerView.Adapter adapterCitas;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_citas_cliente);

        toolbar = this.findViewById(R.id.toolbar_ver_citas_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constraintLayout = this.findViewById(R.id.layout_ver_citas_cliente);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvCitas = this.findViewById(R.id.recyclerview_ver_citas_cliente);
        rvCitas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCitas.setLayoutManager(layoutManager);
        citas = new ArrayList<>();
        cita = new ArrayList<>();
        adapterCitas = new MyAdapterVerCitasClientes(citas, cita, getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAdapter();
        setTitleToolbar();
    }

    // Poner el título a la toolbar
    public void setTitleToolbar(){
        // ID cliente
        final String id_cliente = getIntent().getExtras().getString("id_cliente");
        // Referencia al cliente
        DatabaseReference ref_cliente = mDatabase.child("usuarios").child(id_cliente);
        ref_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String nombre = data.get("nombre").toString();
                String apellidos = data.get("apellidos").toString();
                String nombre_completo = nombre + " " + apellidos;
                getSupportActionBar().setTitle("Citas de "+ nombre_completo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR3", "onCancelled: "+databaseError);
            }
        });
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
        // ID cliente
        final String id_cliente = getIntent().getExtras().getString("id_cliente");
        // Fecha actual
        final Date date = new Date();
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                // Referencia a las citas de la clínica
                DatabaseReference ref_citas = mDatabase.child("citas").child(id_clinica);
                // Obtener las citas ordenadas por fecha a partir de la fecha actual
                ref_citas.orderByValue().startAt(date.getTime()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final int[] aux_contador = {0};
                        // Recorrer cada cita
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // Datos de una cita específica
                            final Map<String, Object> data = (Map<String, Object>) postSnapshot.getValue();
                            // ID cliente cita específica
                            String id_usuario = data.get("id_cliente").toString();
                            // Mostrar solo las citas del cliente especificado
                            if(id_cliente.equals(id_usuario)){
                                // Cambiar el fondo predeterminado por un color si el cliente tiene citas
                                constraintLayout.setBackgroundResource(R.color.color_fondo);
                                getImagenMascotaFromFirebase(new idCallback() {
                                    @Override
                                    public void onCallback(String foto) {
                                        Long date_ts = date.getTime();
                                        Long cita_ts = (Long) data.get("timestamp");
                                        // Sólo coger las citas cuyo tiempo sea mayor al tiempo actual, a través de comparar sus TimeStamps, para establecer el adaptador de citas abiertas
                                        if (date_ts < cita_ts) {
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
                                            rvCitas.setAdapter(adapterCitas);
                                        }else if(date_ts > cita_ts){ // En caso contrario, las citas serán de un tiempo menor, por lo tanto se establecerá el adaptador de citas cerradas
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
                                            rvCitas.setAdapter(adapterCitas);
                                        }
                                    }
                                }, data.get("id_cliente").toString(), data.get("id_mascota").toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR2", "onCancelled: "+databaseError);
                    }
                });
            }
        });
    }
}
