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

public class pantalla_mi_mascota_consultas extends AppCompatActivity {
    private Toolbar toolbar;

    private List<List<Object>> consultasMascota;
    private List<Object> consultaMascota;
    private RecyclerView rvConsultasMascotas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String aux_id;
    private String aux_imagen;
    private String aux_nombre;
    private String id_clinica;
    private long num_consultas;
    private List<String> id_consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_mascota_consultas_online);

        toolbar = this.findViewById(R.id.toolbar_mi_mascota_consultas_online);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvConsultasMascotas = this.findViewById(R.id.recyclerview_mi_mascota_consultas_online);
        rvConsultasMascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvConsultasMascotas.setLayoutManager(layoutManager);
        consultasMascota = new ArrayList<>();
        consultaMascota = new ArrayList<>();
        adapter = new MyAdapterConsultasMiMascota(consultasMascota, consultaMascota, getApplicationContext());

        id_consultas = new ArrayList<>();
        aux_id = getIntent().getExtras().getString("id");
        aux_imagen = getIntent().getExtras().getString("imagen");
        aux_nombre = getIntent().getExtras().getString("nombre");

        // Titulo de la toolbar
        getSupportActionBar().setTitle("Consultas Online de "+ aux_nombre);

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
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                id_clinica = data.get("clinica").toString();
                myCallback.onCallback(id_clinica);
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

    public void loadAdapter() {
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id) {
                final int[] aux_contador_lista = {0};
                final int[] aux_contador_consulta = {1};
                // Referencia a todas las consultas de la clinica
                final DatabaseReference ref_consultas_usuario = mDatabase.child("consultas").child(id);
                ref_consultas_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int num_consultas_total = (int) dataSnapshot.getChildrenCount();
                        // Recorrer todas las consultas de la clinica
                        for(int i = 1; i <= num_consultas_total; i++){
                            // Referencia a una consulta
                            final int finalI = i;
                            ref_consultas_usuario.child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    // Si el ID de la mascota coincide con el ID de la mascota de la consulta, se guardan los datos
                                    if(data.get("id_mascota").toString().equals(aux_id)){
                                        consultaMascota.add(0, finalI);
                                        consultaMascota.add(1, aux_imagen);
                                        consultaMascota.add(2, data.get("fecha"));
                                        consultaMascota.add(3, data.get("mascota"));
                                        consultaMascota.add(4, data.get("asunto").toString());
                                        consultaMascota.add(5, data.get("numero_adjuntos"));

                                        // Mostrar mensaje e icono dependiendo de si la consulta ha sido respondida o no por el veterinario
                                        String respuesta = data.get("respondido").toString();
                                        if(respuesta.equalsIgnoreCase("Si")){
                                            consultaMascota.add(6, "Respondido");
                                            consultaMascota.add(7, R.drawable.alerta_icon);
                                        }else if(respuesta.equalsIgnoreCase("No")){
                                            consultaMascota.add(6, "Sin respuesta");
                                            consultaMascota.add(7, R.drawable.espera_icon);
                                        }
                                        consultaMascota.add(8, data.get("hora"));
                                        consultaMascota.add(9, data.get("mensaje"));
                                        consultaMascota.add(10, data.get("estado"));

                                        // Numero de imagenes adjuntas
                                        int num_adjuntos = Integer.parseInt(data.get("numero_adjuntos").toString());
                                        // Controlar el numero de imagenes adjuntas a insertar en la lista
                                        switch (num_adjuntos){
                                            case 1:
                                                consultaMascota.add(11, data.get("imagen_adjunta0"));
                                                break;
                                            case 2:
                                                consultaMascota.add(11, data.get("imagen_adjunta0"));
                                                consultaMascota.add(12, data.get("imagen_adjunta1"));
                                                break;
                                            case 3:
                                                consultaMascota.add(11, data.get("imagen_adjunta0"));
                                                consultaMascota.add(12, data.get("imagen_adjunta1"));
                                                consultaMascota.add(13, data.get("imagen_adjunta2"));
                                                break;
                                        }
                                        consultasMascota.add(aux_contador_lista[0], consultaMascota);
                                        aux_contador_lista[0]++;
                                        aux_contador_consulta[0]++;
                                        consultaMascota = new ArrayList<>(consultaMascota);
                                        rvConsultasMascotas.setAdapter(adapter);
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
        consultasMascota.clear();
    }
}
