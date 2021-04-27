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

import java.util.Date;
import java.util.Map;

public class pantalla_ver_citas extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton proximas_citas;
    private ImageButton historial_citas;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_citas);

        toolbar = this.findViewById(R.id.toolbar_ver_citas_admin);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        proximas_citas = this.findViewById(R.id.imageButton_proximas_citas_admin);
        proximas_citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_proximas_citas_admin.class);
                startActivity(intent);
            }
        });

        historial_citas = this.findViewById(R.id.imageButton_historial_citas_admin);
        historial_citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_historial_citas_admin.class);
                startActivity(intent);
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

    // Actualizar el estado de las citas cada vez que se entre a esta actividad
    @Override
    protected void onStart() {
        super.onStart();
        // Fecha actual
        final Date date = new Date();
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                // Referencia a las citas de la clínica
                final DatabaseReference ref_citas = mDatabase.child("citas").child(id_clinica);
                ref_citas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long date_ts = date.getTime();
                        int num_citas = (int) dataSnapshot.getChildrenCount();
                        // Recorrer cada cita
                        for (int i = 1; i <= num_citas; i++) {
                            DataSnapshot ds = dataSnapshot.child(String.valueOf(i));
                            Map<String, Object> data = (Map<String, Object>) ds.getValue();
                            Long cita_ts = (Long) data.get("timestamp");
                            // Sólo coger las citas cuyo tiempo sea menor al tiempo actual, a través de comparar sus TimeStamps
                            if (date_ts > cita_ts) {
                                ref_citas.child(String.valueOf(i)).child("estado").setValue("Realizada");
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
}
