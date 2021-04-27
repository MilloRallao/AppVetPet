package com.davidcurbelo.vetpetproyectodam.cliente;

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

public class pantalla_principal_usuario extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton mi_mascota;
    private ImageButton mi_veterinario;
    private ImageButton pedir_cita;
    private ImageButton consulta_online;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_usuario);

        toolbar = this.findViewById(R.id.toolbar_principal_usuario);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mi_mascota = this.findViewById(R.id.imageButton_mi_mascota_principal);
        mi_veterinario = this.findViewById(R.id.imageButton_mi_veterinario_principal_user);
        pedir_cita = this.findViewById(R.id.imageButton_pedir_cita_principal_user);
        consulta_online = this.findViewById(R.id.imageButton_consulta_online_principal_user);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Accion al pulsar en el boton "Mi Mascota" que enlaza con su actividad
        mi_mascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), pantalla_mi_mascota.class);
                startActivity(intent);
            }
        });

        // Accion al pulsar en el boton "Mi Veterinario" que enlaza con su actividad
        mi_veterinario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), pantalla_mi_veterinario.class);
                startActivity(intent);
            }
        });

        // Accion al pulsar en el boton "Pedir Cita" que enlaza con su actividad
        pedir_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), pantalla_pedir_cita.class);
                startActivity(intent);
            }
        });

        // Accion al pulsar en el boton "Consulta Online" que enlaza con su actividad
        consulta_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), pantalla_consulta_online.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String uid = currentUser.getUid();
        // Referencia al nodo de la BD del usuario actual
        DatabaseReference ref_msj = mDatabase.child("usuarios").child(uid);
        ref_msj.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String genero = (String) data.get("genero");
                String nombre = (String) data.get("nombre");
                //Modifica el mensaje de la Toolbar  segun el genero del usuario y muestra su nombre
                if(genero.equalsIgnoreCase("Hombre")){
                    getSupportActionBar().setTitle("Bienvenido, "+nombre);
                }else if(genero.equalsIgnoreCase("Mujer")){
                    getSupportActionBar().setTitle(" Bienvenida, "+nombre);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }
}
