package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class pantalla_principal_admin extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton agregar_cliente;
    private ImageButton ver_clientes;
    private ImageButton consultas_online;
    private ImageButton ver_citas;
    private ImageButton mi_clinica;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_admin);

        toolbar = this.findViewById(R.id.toolbar_principal_admin);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Accion del boton para ir a la pantalla de agregar cliente
        agregar_cliente = this.findViewById(R.id.imagebutton_agregar_cliente_principal_admin);
        agregar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_agregar_cliente.class);
                startActivity(intent);
            }
        });

        // Accion del boton para ir a la pantalla de ver clientes
        ver_clientes = this.findViewById(R.id.imagebutton_ver_clientes_principal_admin);
        ver_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_ver_clientes.class);
                startActivity(intent);
            }
        });

        // Accion del boton para ir a la pantalla de ver consultas online
        consultas_online = this.findViewById(R.id.imagebutton_ver_consultas_online_principal_admin);
        consultas_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_ver_consultas_online.class);
                startActivity(intent);
            }
        });

        // Accion del boton para ir a la pantalla de mi clinica
        ver_citas = this.findViewById(R.id.imageButton_ver_citas_principal_admin);
        ver_citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_ver_citas.class);
                startActivity(intent);
            }
        });

        // Accion del boton para ir a la pantalla de mi clinica
        mi_clinica = this.findViewById(R.id.imagebutton_mi_clinica_principal_admin);
        mi_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_mi_clinica.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Referencia al nodo de la BD del usuario actual
        DatabaseReference ref_user = mDatabase.child("usuarios").child(user.getUid());
        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String clinica_id = data.get("clinica").toString();
                DatabaseReference ref_clinica = mDatabase.child("clinicas").child(clinica_id);
                ref_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String nombre_clinica = (String) data.get("nombre");
                        getSupportActionBar().setTitle(nombre_clinica);
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
}
