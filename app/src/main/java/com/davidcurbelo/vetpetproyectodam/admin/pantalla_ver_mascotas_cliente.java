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

public class pantalla_ver_mascotas_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private Button agregar_mascota;
    private ConstraintLayout constraintLayout;

    private List<List<String>> mascotas;
    private List<String> mascota;
    private RecyclerView rvmascotas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String id_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_mascotas_cliente);

        toolbar = this.findViewById(R.id.toolbar_ver_mascotas_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constraintLayout = this.findViewById(R.id.layout_ver_mascotas_cliente_admin);

        agregar_mascota = this.findViewById(R.id.button_agregar_mascota);
        agregar_mascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_agregar_mascota.class);
                id_cliente = getIntent().getExtras().getString("id_cliente");
                intent.putExtra("id_cliente", id_cliente);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvmascotas = this.findViewById(R.id.recyclerview_ver_mascotas_cliente);
        rvmascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvmascotas.setLayoutManager(layoutManager);
        mascotas = new ArrayList<>();
        mascota = new ArrayList<>();
        adapter = new MyAdapterVerMascotas(mascotas, mascota, getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        id_cliente = getIntent().getExtras().getString("id_cliente");
        // Referencia a los datos del cliente
        final DatabaseReference ref_cliente = mDatabase.child("usuarios").child(id_cliente);
        ref_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Acceder a los datos de un cliente
                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String nombre_cliente = data.get("nombre").toString();
                String apellidos_cliente = data.get("apellidos").toString();
                // Poner título de la toolbar
                getSupportActionBar().setTitle("Mascotas de " + nombre_cliente + " " + apellidos_cliente);
                // Referencia a las mascotas del cliente
                ref_cliente.child("mascotas").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int num_mascotas = (int) dataSnapshot.getChildrenCount();
                        if(num_mascotas == 0){
                            constraintLayout.setBackgroundResource(R.drawable.fondo_sin_mascotas);
                        }
                        for (int i = 0; i < num_mascotas; i++) {
                            DataSnapshot ds = dataSnapshot.child(String.valueOf(i));
                            final Map<String, Object> data = (Map<String, Object>) ds.getValue();
                            mascota.add(0, data.get("nombre").toString());
                            mascota.add(1, data.get("foto").toString());
                            mascota.add(2, String.valueOf(i));
                            mascota.add(3, id_cliente);
                            mascotas.add(i, mascota);
                            mascota = new ArrayList<>(mascota);
                            rvmascotas.setAdapter(adapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR0", "onCancelled: "+databaseError);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }

    // Evitar que los elementos del RecyclerView se repitan al volver hacia atrás con el botón "volver" del propio móvil (No a través de la Toolbar)
    @Override
    public void onStop() {
        super.onStop();
        mascotas.clear();
    }
}
