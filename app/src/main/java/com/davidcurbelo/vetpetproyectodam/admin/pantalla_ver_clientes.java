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

public class pantalla_ver_clientes extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;

    private List<List<String>> clientes;
    private List<String> cliente;
    private RecyclerView rvclientes;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private int num_clientes;
    private List<String> id_clientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_clientes);

        toolbar = this.findViewById(R.id.toolbar_ver_clientes);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constraintLayout = this.findViewById(R.id.layout_ver_clientes_admin);

        id_clientes = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvclientes = this.findViewById(R.id.recyclerview_ver_clientes);
        rvclientes.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvclientes.setLayoutManager(layoutManager);
        clientes = new ArrayList<>();
        cliente = new ArrayList<>();
        adapter = new MyAdapterVerClientes(clientes, cliente, getApplicationContext());

    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback1 {
        void onCallback(String id, int num_mascotas);
    }
    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback2 {
        void onCallback(String id);
    }

    // Obtener datos del usuario
    public void getDatosUsuarioFromFirebase(final idCallback1 myCallback, String id_usuario) {
        // Referencia a los datos del usuario en la BD
        mDatabase.child("usuarios").child(id_usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String nombre_cliente = data.get("nombre").toString();
                String apellidos_cliente = data.get("apellidos").toString();
                String nombre_completo_cliente = nombre_cliente + " " + apellidos_cliente;
                int num_mascotas = (int) dataSnapshot.child("mascotas").getChildrenCount();
                myCallback.onCallback(nombre_completo_cliente, num_mascotas);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR2", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener ID de la clinica del veterinario
    public void getIdClinica(final idCallback2 myCallback){
        mDatabase.child("usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String clinica_id = data.get("clinica").toString();
                myCallback.onCallback(clinica_id);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR3", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener los ID's de los clientes de la clinica
    public void getIdClientesFromFirebase(String clinica_id){
        // Referencia a los clientes de la clinica
        DatabaseReference ref_clientes = mDatabase.child("clinicas").child(clinica_id).child("clientes");
        ref_clientes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Numero de clientes que tiene la clinica
                num_clientes = (int) dataSnapshot.getChildrenCount();
                if(num_clientes == 0){
                    constraintLayout.setBackgroundResource(R.drawable.fondo_sin_clientes);
                }
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Guardar cada ID de cliente en la lista
                id_clientes.add(0, "");
                for (int i= 1; i <= num_clientes; i++){
                    id_clientes.add(i, data.get("id_usuario_"+i).toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        getIdClinica(new idCallback2() {
            @Override
            public void onCallback(String id_clinica) {
                getIdClientesFromFirebase(id_clinica);
                // Referencia a los usuarios
                DatabaseReference ref_usuarios = mDatabase.child("usuarios");
                ref_usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                        final int[] aux_contador = {0};
                        // Recorrer todos los usuarios
                        for(int i = 1; i <= num_clientes; i++){
                            // Acceder a los datos de un cliente
                            DataSnapshot a = dataSnapshot.child(id_clientes.get(i));
                            final Map<String, Object> data = (Map<String, Object>) a.getValue();
                            final int finalI = i;
                            getDatosUsuarioFromFirebase(new idCallback1() {
                                @Override
                                public void onCallback(String nombre, int num_mascotas) {
                                    cliente.add(0, data.get("dni").toString());
                                    cliente.add(1, nombre);
                                    cliente.add(2, String.valueOf(num_mascotas));
                                    cliente.add(3, data.get("genero").toString());
                                    cliente.add(4, data.get("telefono").toString());
                                    cliente.add(5, id_clientes.get(finalI));
                                    clientes.add(aux_contador[0], cliente);
                                    aux_contador[0]++;
                                    cliente = new ArrayList<>(cliente);
                                    rvclientes.setAdapter(adapter);
                                }
                            }, id_clientes.get(i));
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

    // Evitar que los elementos del RecyclerView se repitan al volver hacia atrás con el botón "volver" del propio móvil (No a través de la Toolbar)
    @Override
    public void onStop() {
        super.onStop();
        clientes.clear();
    }
}
