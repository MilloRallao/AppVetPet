package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.davidcurbelo.vetpetproyectodam.R;
import com.davidcurbelo.vetpetproyectodam.pantalla_mapa;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class pantalla_mi_veterinario extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private Toolbar toolbar;
    private ConstraintLayout fondo;
    private ImageView logo;
    private ImageView llamada_clinica;
    private TextView web_clinica;
    private TextView nombre_clinica;
    private TextView telefono1_clinica;
    private TextView telefono2_clinica;
    private TextView direccion_clinica;
    private TextView codigo_postal_clinica;
    private TextView localidad_clinica;
    private TextView horario_dias_clinica;
    private TextView horario_horas_clinica;
    private TextView abierto_cerrado_horario_clinica;
    private TextView email_clinica;
    private ImageButton facebook_clinica;
    private Button ver_mapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_veterinario);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = this.findViewById(R.id.toolbar_mi_veterinario);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fondo = this.findViewById(R.id.fondo_mi_veterinario);
        logo = this.findViewById(R.id.imageView_logo_clinica);
        llamada_clinica = this.findViewById(R.id.imageView_llamada_clinica);
        web_clinica = this.findViewById(R.id.textView_web_clinica);
        nombre_clinica = this.findViewById(R.id.textView_nombre_clinica);
        telefono1_clinica = this.findViewById(R.id.textView_telefono1_clinica);
        telefono2_clinica = this.findViewById(R.id.textView_telefono2_clinica);
        direccion_clinica = this.findViewById(R.id.textView_direccion_clinica);
        codigo_postal_clinica = this.findViewById(R.id.textView_codigo_postal_clinica);
        localidad_clinica = this.findViewById(R.id.textView_localidad_clinica);
        horario_dias_clinica = this.findViewById(R.id.textView_horario_dias_clinica);
        horario_horas_clinica = this.findViewById(R.id.textView_horario_horas_clinica);
        abierto_cerrado_horario_clinica = this.findViewById(R.id.textView_abierto_cerrado_horario_clinica);
        email_clinica = this.findViewById(R.id.textView_email_clinica);
        facebook_clinica = this.findViewById(R.id.imageButton_facebook_clinica);
        ver_mapa = this.findViewById(R.id.button_ver_mapa);

        web_clinica.setPaintFlags(telefono1_clinica.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        telefono1_clinica.setPaintFlags(telefono1_clinica.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        telefono2_clinica.setPaintFlags(telefono2_clinica.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        email_clinica.setPaintFlags(email_clinica.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        llamada_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tlfn = Integer.parseInt(telefono2_clinica.getText().toString().trim().replace(" ", ""));
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tlfn)));
            }
        });

        // Accion al presionar sobre la direccion web. Abre el navegador por defecto con la direccion de la web de la clinica
        web_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+web_clinica.getText().toString().trim()));
                startActivity(intent);
            }
        });

        // Accion al presionar sobre el telefono 1. Abre el marcador telefónico para hacer la llamada
        telefono1_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tlfn = Integer.parseInt(telefono1_clinica.getText().toString().trim().replace(" ", ""));
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tlfn)));
            }
        });

        // Accion al presionar sobre el telefono 2. Abre el marcador telefónico para hacer la llamada
        telefono2_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tlfn = Integer.parseInt(telefono2_clinica.getText().toString().trim().replace(" ", ""));
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tlfn)));
            }
        });

        // Accion al presionar el boton "Ver en Mapa". Abre la Activity del Google Map
        ver_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_mapa.class);
                intent.putExtra("titulo", nombre_clinica.getText().toString());
                intent.putExtra("direccion", direccion_clinica.getText().toString());
                startActivity(intent);
            }
        });

        // Accion al presionar sobre el correo de la clinica.
        email_clinica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        rellenarDatos();
    }

    // Rellenar los campos de la actividad con los datos de la clinica asociada al usuario
    public void rellenarDatos(){
        // UID usuario
        String uid = currentUser.getUid();
        // Referencia a los datos del usuario
        DatabaseReference ref_data_user = mDatabase.child("usuarios").child(uid);
        ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String id_clinica = data.get("clinica").toString();
                ponerFondo(id_clinica);
                ponerLogo(id_clinica);
                // Referencia a los datos de la clinica asociada al usuario
                DatabaseReference ref_data_clinica = mDatabase.child("clinicas").child(id_clinica);
                ref_data_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        String nombre = (String) data.get("nombre");
                        nombre_clinica.setText(nombre);
                        String web = (String) data.get("web");
                        web_clinica.setText(web);
                        String tlfno = (String) data.get("telefono");
                        telefono1_clinica.setText(tlfno);
                        String movil = (String) data.get("movil");
                        telefono2_clinica.setText(movil);
                        String direccion = (String) data.get("direccion");
                        direccion_clinica.setText(direccion);
                        String cp = data.get("cp").toString();
                        codigo_postal_clinica.setText(cp);
                        String localidad = (String) data.get("localidad");
                        localidad_clinica.setText(localidad);
                        String horario_dias = (String) data.get("horario_dias");
                        horario_dias_clinica.setText(horario_dias);
                        String horario_horas = (String) data.get("horario_horas");
                        horario_horas_clinica.setText(horario_horas);
                        comprobarHorario(horario_horas);
                        String email = (String) data.get("email");
                        email_clinica.setText(email);
                        String facebook = (String) data.get("facebook");
                        facebook_clinica.setEnabled(true);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR1", "onCancelled: "+databaseError);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR2", "onCancelled: "+databaseError);
            }
        });
    }

    // Comprobar si la clinica está abierta en el momento actual y se modifica el texto en consecuencia
    public void comprobarHorario(String horario_horas){
        int currentTime = Calendar.getInstance().HOUR_OF_DAY;
        String[] horas = horario_horas.split("-");
        int hora_inicial = Integer.parseInt(horas[0].trim().substring(0,2));
        int hora_final = Integer.parseInt(horas[1].trim().substring(0,2));
        if(hora_inicial < currentTime  && currentTime < hora_final){
            abierto_cerrado_horario_clinica.setText("Abierto");
        }else{
            abierto_cerrado_horario_clinica.setText("Cerrado");
        }
    }

    // Poner logo correspondiente a la clínica asociada al cliente
    public void ponerLogo(String id_clinica_aux) {
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Referencia al logo de la clinica
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("logos/clinica"+ id_clinica_aux +".jpg");
        // Guardar imagen en local y asociarla al imageview
        final File finalLocalFile = localFile;
        storageReference.getFile(finalLocalFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Glide.with(getApplicationContext()).load(finalLocalFile).into(logo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "ALGO HA FALLADO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Poner fondo correspondiente a la clinica asociada al cliente
    public void ponerFondo(String id_clinica_aux){
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Referencia al fondo de la clinica
        StorageReference storageReference2 = FirebaseStorage.getInstance().getReference("fondos/fondo_mi_veterinario_clinica"+ id_clinica_aux +".png");
        // Guardar imagen en local y asociarla al imageview
        final File finalLocalFile = localFile;
        // Guardar imagen en local y asociarla como fondo del layout
        storageReference2.getFile(finalLocalFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                fondo.setBackground(Drawable.createFromPath(finalLocalFile.getPath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "ALGO HA FALLADO", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
