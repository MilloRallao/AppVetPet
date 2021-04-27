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

public class pantalla_mi_mascota extends AppCompatActivity {
    private Toolbar toolbar;

    private List<List<String>> mascotas;
    private List<String> mascota;
    private RecyclerView rvMascotas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_mascota);

        toolbar = this.findViewById(R.id.toolbar_mi_mascota);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvMascotas = this.findViewById(R.id.reciclerview_mi_mascota);
        rvMascotas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvMascotas.setLayoutManager(layoutManager);
        mascotas = new ArrayList<>();
        mascota = new ArrayList<>();
        adapter = new MyAdapterMiMascota(mascotas, mascota, getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Referencia a las mascotas del usuario
        DatabaseReference ref_mascotas = mDatabase.child("usuarios").child(user.getUid()).child("mascotas");
        ref_mascotas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot){
                // Numero de mascotas que tiene el usuario
                int num_mascotas = (int) dataSnapshot.getChildrenCount();
                // Modificar el texto de la Toolbar dependiendo del número de mascotas del cliente
                if(num_mascotas > 1){
                    toolbar.setTitle("Mis Mascotas");
                }else if(num_mascotas <= 1){
                    toolbar.setTitle("Mi Mascota");
                }
                // Recorrer todas las mascotas del usuario y establecer el Adapter para el RecyclerView para ir mostrandolas
                for(int i = 0; i < num_mascotas; i++){
                    // Acceder a los datos de una mascota
                    DataSnapshot a = dataSnapshot.child(String.valueOf(i));
                    Map<String, Object> data = (Map<String, Object>) a.getValue();
                    mascota.add(0, data.get("nombre").toString());
                    mascota.add(1, data.get("sexo").toString());
                    mascota.add(2, data.get("especie").toString());
                    mascota.add(3, data.get("edad").toString());
                    mascota.add(4, data.get("peso").toString());
                    mascota.add(5, data.get("foto").toString());
                    mascota.add(6, String.valueOf(i));
                    mascotas.add(i, mascota);
                    mascota = new ArrayList<>(mascota);
                    rvMascotas.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", "onCancelled: "+databaseError);
            }
        });
    }

    // Prevenir la repetición de elementos en el RecyclerView al volver hacia atrás con los botones del propio móvil (No con el botón de la Toolbar)
    @Override
    protected void onStop() {
        super.onStop();
        mascotas.clear();
    }
}
