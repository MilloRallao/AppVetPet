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

public class pantalla_mi_mascota_historial_citas extends AppCompatActivity {

    private Toolbar toolbar;

    private List<List<Object>> citasMascota;
    private List<Object> citaMascota;
    private RecyclerView rvCitasMascotas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<String> idCitas;
    private String aux_id;
    private String aux_imagen;
    private String aux_nombre;
    private String id_clinica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_mascota_historial_citas);

        toolbar = this.findViewById(R.id.toolbar_mi_mascota_historial_citas);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvCitasMascotas = this.findViewById(R.id.recyclerview_mi_mascota_historial_citas);
        rvCitasMascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCitasMascotas.setLayoutManager(layoutManager);
        citasMascota = new ArrayList<>();
        citaMascota = new ArrayList<>();
        adapter = new MyAdapterHistorialCitasMiMascota(citasMascota, citaMascota, getApplicationContext());

        idCitas = new ArrayList<>();
        aux_id = getIntent().getExtras().getString("id");
        aux_imagen = getIntent().getExtras().getString("imagen");
        aux_nombre = getIntent().getExtras().getString("nombre");
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

    @Override
    protected void onStart() {
        super.onStart();
        // Titulo de la toolbar
        getSupportActionBar().setTitle("Historial de citas de "+ aux_nombre);
        // Referencia a las citas del usuario
        DatabaseReference ref_citas_usuario = mDatabase.child("usuarios").child(user.getUid()).child("citas");
        ref_citas_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Numero de citas que tiene el usuario
                int num_citas = (int) dataSnapshot.getChildrenCount();
                // Guardar todos los ID de las citas del usuario
                for(int i = 1; i <= num_citas; i++){
                    idCitas.add(i-1, data.get("id_cita_"+i).toString());
                }
                getIdClinicaFromFirebase(new idCallback() {
                    @Override
                    public void onCallback(String id) {
                        final int[] contador_aux = {0};
                        // Recorrer solamente las citas del usuario en el nodo "citas" de la BD
                        for(int i = 1; i <= idCitas.size(); i++){
                            final int finalI = i;
                            // Referencia a la consulta en "citas"
                            DatabaseReference ref_citas = mDatabase.child("citas").child(id).child(idCitas.get(finalI-1));
                            ref_citas.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    // Si el ID de la mascota de la cita analizada concuerda con el ID de la mascota seleccionada, se establece el adaptador con la lista de citas rellena
                                    if(data.get("id_mascota").toString().equals(aux_id)){
                                        citaMascota.add(0, idCitas.get(finalI-1));
                                        citaMascota.add(1, aux_imagen);
                                        citaMascota.add(2, data.get("dia"));
                                        citaMascota.add(3, data.get("hora"));
                                        citaMascota.add(4, aux_nombre);
                                        citaMascota.add(5, data.get("motivo"));
                                        citaMascota.add(6, data.get("estado"));
                                        citasMascota.add(contador_aux[0], citaMascota);
                                        citaMascota = new ArrayList<>(citaMascota);
                                        rvCitasMascotas.setAdapter(adapter);
                                        contador_aux[0]++;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERROR2", "onCancelled: "+databaseError);
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }

    // Prevenir la repetición de elementos en el RecyclerView al volver hacia atrás con los botones del propio móvil (No con el botón de la Toolbar)
    @Override
    protected void onStop() {
        super.onStop();
        citasMascota.clear();
    }
}
