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

public class pantalla_proximas_citas extends AppCompatActivity {
    private Toolbar toolbar;

    private List<List<Object>> citas;
    private List<Object> cita;
    private RecyclerView rvCitas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    private String clinica_id;
    private String foto;
    private long num_citas;
    private List<String> id_citas;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_proximas_citas);

        toolbar = this.findViewById(R.id.toolbar_proximas_citas);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_citas = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvCitas = this.findViewById(R.id.recyclerview_proximas_citas);
        rvCitas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCitas.setLayoutManager(layoutManager);
        citas = new ArrayList<>();
        cita = new ArrayList<>();
        adapter = new MyAdapterProximasCitas(citas, cita, getApplicationContext());

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

    // Obtener el id de la cita especifica
    public void getIdCitaFromFirebase(final idCallback myCallback) {
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
                foto = data.get("foto").toString();
                myCallback.onCallback(foto);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    // Llamar al adaptador del RecyclerView con los datos de las citas del usuario
    public void loadAdapter(){
        getIdCitaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id) {
                clinica_id = id;
                final int[] aux_contador = {0};
                // Obtener todos los datos de las citas del usuario
                DatabaseReference ref_citas = mDatabase.child("citas").child(clinica_id);
                ref_citas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        // Recorrer todas las citas del usuario en el nodo "citas" y establecer el Adapter para el RecyclerView para ir mostrandolas
                        for(int i = 1; i <= num_citas; i++){
                            // Datos de una cita específica
                            DataSnapshot a = dataSnapshot.child(id_citas.get(i));
                            final Map<String, Object> data = (Map<String, Object>) a.getValue();
                            // Obtener el estado de la cita para mostrar solamente las citas abiertas
                            String estado = data.get("estado").toString();
                            if(estado.equalsIgnoreCase("Abierta")){
                                // Obtener la imagen de la mascota de la cita
                                String id_mascota = data.get("id_mascota").toString();

                                final int finalI = i;
                                getImagenMascotaFromFirebase(new idCallback() {
                                    @Override
                                    public void onCallback(String id) {
                                        cita.add(0, id_citas.get(finalI));
                                        cita.add(1, foto);
                                        cita.add(2, data.get("dia"));
                                        cita.add(3, data.get("hora"));
                                        cita.add(4, data.get("mascota"));
                                        cita.add(5, data.get("motivo"));

                                        cita.add(6, clinica_id);
                                        cita.add(7, user.getUid());

                                        citas.add(aux_contador[0], cita);
                                        aux_contador[0]++;
                                        cita = new ArrayList<>(cita);
                                        rvCitas.setAdapter(adapter);
                                    }
                                }, id_mascota);
                            }
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

    // Obtener las citas del usuario
    public void datosUsuario(){
        // Referencia a los datos del usuario en la BD
        DatabaseReference ref_usuario = mDatabase.child("usuarios").child(user.getUid());
        ref_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener todas las citas del usuario
                DataSnapshot a = dataSnapshot.child("citas");
                // Obtener la cantidad de citas del usuario
                num_citas = a.getChildrenCount();
                Map<String, Object> data = (Map<String, Object>) a.getValue();
                // Guardar cada ID de cita en la lista
                id_citas.add(0, "");
                for (int i= 1; i <= num_citas; i++){
                    id_citas.add(i, data.get("id_cita_"+i).toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR2", "onCancelled: "+databaseError);
            }
        });
    }
}
