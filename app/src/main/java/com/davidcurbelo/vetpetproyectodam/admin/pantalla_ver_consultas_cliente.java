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
import java.util.List;
import java.util.Map;

public class pantalla_ver_consultas_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;

    private List<List<Object>> consultas;
    private List<Object> consulta;
    private RecyclerView rvConsultas;
    private RecyclerView.Adapter adapterConsultas;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_consultas_cliente);

        toolbar = this.findViewById(R.id.toolbar_ver_consultas_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constraintLayout = this.findViewById(R.id.layout_ver_consultas_cliente_admin);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvConsultas = this.findViewById(R.id.recyclerview_consultas_cliente_admin);
        rvConsultas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvConsultas.setLayoutManager(layoutManager);
        consultas = new ArrayList<>();
        consulta = new ArrayList<>();
        adapterConsultas = new MyAdapterVerConsultasCliente(consultas, consulta, getApplicationContext());
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
                getSupportActionBar().setTitle("Consultas de "+ nombre_completo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR4", "onCancelled: "+databaseError);
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
    public void loadAdapter(){
        // ID cliente
        final String id_cliente = getIntent().getExtras().getString("id_cliente");
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                final int[] aux_contador_lista = {0};
                // Obtener todos los datos de las consultas del usuario
                final DatabaseReference ref_consultas = mDatabase.child("consultas").child(id_clinica);
                ref_consultas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        // Numero de consultas existentes en la clínica
                        int num_consultas = (int) dataSnapshot.getChildrenCount();
                        // Recorrer todas las consultas
                        for(int i = 1; i <= num_consultas; i++){
                            final int finalI = i;
                            // Referencia a cada consulta por separado
                            ref_consultas.child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Datos de una consulta específica
                                    final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    // ID cliente consulta específica
                                    String id_usuario = data.get("id_usuario").toString();
                                    // Mostrar solo las consultas del cliente especificado
                                    if(id_cliente.equals(id_usuario)){
                                        // Obtener el estado de la consulta
                                        String estado = data.get("estado").toString();
                                        // Coger las consultas cerradas para establecer el adaptador de consultas cerradas
                                        if(estado.equalsIgnoreCase("Cerrada")){
                                            // Obtener la imagen de la mascota de la consulta
                                            String id_mascota = data.get("id_mascota").toString();
                                            getImagenMascotaFromFirebase(new idCallback() {
                                                @Override
                                                public void onCallback(String id) {
                                                    consulta.add(0, finalI);
                                                    consulta.add(1, id);
                                                    consulta.add(2, data.get("fecha"));
                                                    consulta.add(3, data.get("mascota"));
                                                    consulta.add(4, data.get("asunto").toString());
                                                    consulta.add(5, data.get("numero_adjuntos"));
                                                    consulta.add(6, "Respondido");
                                                    consulta.add(7, R.drawable.alerta_icon_v2);
                                                    consulta.add(8, data.get("hora"));
                                                    consulta.add(9, data.get("mensaje"));
                                                    consulta.add(10, data.get("estado"));

                                                    // Modificar mensaje de adjuntos dependiendo del numero de imagenes adjuntas
                                                    int num_adjuntos = Integer.parseInt(data.get("numero_adjuntos").toString());
                                                    // Controlar el numero de imagenes adjuntas a insertar en la lista
                                                    switch (num_adjuntos){
                                                        case 1:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            break;
                                                        case 2:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            consulta.add(12, data.get("imagen_adjunta1"));
                                                            break;
                                                        case 3:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            consulta.add(12, data.get("imagen_adjunta1"));
                                                            consulta.add(13, data.get("imagen_adjunta2"));
                                                            break;
                                                    }
                                                    consultas.add(aux_contador_lista[0], consulta);
                                                    aux_contador_lista[0]++;
                                                    consulta = new ArrayList<>(consulta);
                                                    rvConsultas.setAdapter(adapterConsultas);
                                                }
                                            }, id_usuario, id_mascota);
                                        }else if (estado.equalsIgnoreCase("Abierta")) { // Coger las consultas abiertas para establecer el adaptador de consultas abiertas
                                            // Obtener la imagen de la mascota de la consulta
                                            String id_mascota = data.get("id_mascota").toString();
                                            getImagenMascotaFromFirebase(new idCallback() {
                                                @Override
                                                public void onCallback(String id) {
                                                    consulta.add(0, finalI);
                                                    consulta.add(1, id);
                                                    consulta.add(2, data.get("fecha"));
                                                    consulta.add(3, data.get("mascota"));
                                                    consulta.add(4, data.get("asunto").toString());
                                                    consulta.add(5, data.get("numero_adjuntos"));

                                                    // Mostrar mensaje e icono dependiendo de si la consulta ha sido respondida o no por el veterinario
                                                    String respuesta = data.get("respondido").toString();
                                                    if (respuesta.equalsIgnoreCase("Si")) {
                                                        consulta.add(6, "Respondido");
                                                        consulta.add(7, R.drawable.alerta_icon);
                                                    } else if (respuesta.equalsIgnoreCase("No")) {
                                                        consulta.add(6, "Sin respuesta");
                                                        consulta.add(7, R.drawable.espera_icon);
                                                    }
                                                    consulta.add(8, data.get("hora"));
                                                    consulta.add(9, data.get("mensaje"));
                                                    consulta.add(10, data.get("estado"));

                                                    // Numero de imagenes adjuntas
                                                    int num_adjuntos = Integer.parseInt(data.get("numero_adjuntos").toString());
                                                    // Controlar el numero de imagenes adjuntas a insertar en la lista
                                                    switch (num_adjuntos) {
                                                        case 1:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            break;
                                                        case 2:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            consulta.add(12, data.get("imagen_adjunta1"));
                                                            break;
                                                        case 3:
                                                            consulta.add(11, data.get("imagen_adjunta0"));
                                                            consulta.add(12, data.get("imagen_adjunta1"));
                                                            consulta.add(13, data.get("imagen_adjunta2"));
                                                            break;
                                                    }
                                                    consultas.add(aux_contador_lista[0], consulta);
                                                    aux_contador_lista[0]++;
                                                    consulta = new ArrayList<>(consulta);
                                                    rvConsultas.setAdapter(adapterConsultas);
                                                }
                                            }, id_usuario, id_mascota);
                                        }
                                        // Cambiar el fondo predeterminado por un color si el cliente tiene consultas
                                        constraintLayout.setBackgroundResource(R.color.color_fondo);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERROR3", "onCancelled: "+databaseError);
                                }
                            });
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

    // Evitar que los elementos del RecyclerView se repitan al volver hacia atrás con el botón "volver" del propio móvil (No a través de la Toolbar)
    @Override
    public void onStop() {
        super.onStop();
        consultas.clear();
    }
}
