package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class pantalla_consultas_abiertas extends AppCompatActivity {
    private List<List<Object>> consultas;
    private List<Object> consulta;
    private RecyclerView rvConsultas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Toolbar toolbar;
    private String clinica_id;
    private String foto;
    private long num_consultas;
    private List<String> id_consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_consultas_abiertas);

        toolbar = this.findViewById(R.id.toolbar_consultas_abiertas);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvConsultas = this.findViewById(R.id.recyclerview_consultas_abiertas);
        rvConsultas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvConsultas.setLayoutManager(layoutManager);
        id_consultas = new ArrayList<>();
        consultas = new ArrayList<>();
        consulta = new ArrayList<>();
        adapter = new MyAdapterConsultasAbiertas(consultas, consulta, getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        datosUsuario();
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
                Map<String, Object> data1 = (Map<String, Object>) dataSnapshot.getValue();
                clinica_id = data1.get("clinica").toString();
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
                foto = data.get("foto").toString();
                myCallback.onCallback(foto);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    // Obtener las consultas del usuario
    public void datosUsuario(){
        // Referencia a los datos del usuario en la BD
        DatabaseReference ref_usuario = mDatabase.child("usuarios").child(user.getUid());
        ref_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener todas las consultas del usuario
                DataSnapshot a = dataSnapshot.child("consultas");
                // Obtener la cantidad de consultas del usuario
                num_consultas = a.getChildrenCount();
                Map<String, Object> data2 = (Map<String, Object>) a.getValue();
                // Guardar cada ID de consulta en la lista
                id_consultas.add(0, "");
                for (int i= 1; i <= num_consultas; i++){
                    id_consultas.add(i, data2.get("id_consulta_"+i).toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }

    // Llamar al adaptador del RecyclerView con los datos de las consultas del usuario
    public void loadAdapter(){
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id) {
                final int[] aux_contador_lista = {0};
                // Obtener todos los datos de las consultas del usuario
                final DatabaseReference ref_consultas = mDatabase.child("consultas").child(id);
                ref_consultas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        // Recorrer todas las consultas del usuario en "consultas"
                        for(int i = 1; i <= num_consultas; i++){
                            // Referencia a solo las consultas que concuerden con los ID guardados en la lista de ID de las consultas del usuario
                            final int finalI = i;
                            ref_consultas.child(id_consultas.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Datos de una consulta específica
                                    final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                                    // Obtener el estado de la consulta para mostrar solamente las consultas abiertas
                                    String estado = data.get("estado").toString();
                                    if(estado.equalsIgnoreCase("Abierta")){
                                        // Obtener la imagen de la mascota de la consulta
                                        String id_mascota = data.get("id_mascota").toString();

                                        getImagenMascotaFromFirebase(new idCallback() {
                                            @Override
                                            public void onCallback(String id) {
                                                consulta.add(0, id_consultas.get(finalI));
                                                consulta.add(1, foto);
                                                consulta.add(2, data.get("fecha"));
                                                consulta.add(3, data.get("mascota"));
                                                consulta.add(4, data.get("asunto").toString());
                                                consulta.add(5, data.get("numero_adjuntos"));

                                                // Mostrar mensaje e icono dependiendo de si la consulta ha sido respondida o no por el veterinario
                                                String respuesta = data.get("respondido").toString();
                                                if(respuesta.equalsIgnoreCase("Si")){
                                                    consulta.add(6, "Respondido");
                                                    consulta.add(7, R.drawable.alerta_icon);
                                                }else if(respuesta.equalsIgnoreCase("No")){
                                                    consulta.add(6, "Sin respuesta");
                                                    consulta.add(7, R.drawable.espera_icon);
                                                }
                                                consulta.add(8, data.get("hora"));
                                                consulta.add(9, data.get("mensaje"));
                                                consulta.add(10, data.get("estado"));

                                                // Numero de imagenes adjuntas
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
                                                rvConsultas.setAdapter(adapter);
                                            }
                                        }, id_mascota);
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
                        Log.d("ERROR1", "onCancelled: "+databaseError);
                    }
                });
            }
        });
    }

    // Prevenir la repetición de elementos en el RecyclerView al volver hacia atrás con los botones del propio móvil (No con el botón de la Toolbar)
    @Override
    protected void onStop() {
        super.onStop();
        consultas.clear();
    }
}