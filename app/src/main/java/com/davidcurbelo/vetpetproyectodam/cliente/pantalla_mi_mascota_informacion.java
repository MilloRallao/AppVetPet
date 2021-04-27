package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Map;

public class pantalla_mi_mascota_informacion extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imagen;
    private TextView nombre;
    private TextView especie;
    private TextView raza;
    private TextView sexo;
    private TextView fecha_nacimiento;
    private TextView edad;
    private TextView edad_humana;
    private TextView peso;
    private TextView num_vacunas;
    private TextView num_diagnosticos;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_mascota_informacion);

        toolbar = this.findViewById(R.id.toolbar_informacion_mi_mascota);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imagen = this.findViewById(R.id.imageView_mascota_informacion_mi_mascota);
        nombre = this.findViewById(R.id.textView_nombre_mascota_informacion_mi_mascota);
        especie = this.findViewById(R.id.textView_especie_mascota_informacion_mi_mascota);
        raza = this.findViewById(R.id.textView_raza_mascota_informacion_mi_mascota);
        sexo = this.findViewById(R.id.textView_sexo_mascota_informacion_mi_mascota);
        fecha_nacimiento = this.findViewById(R.id.textView_fecha_nacimiento_mascota_informacion_mi_mascota);
        edad = this.findViewById(R.id.textView_edad_mascota_informacion_mi_mascota);
        edad_humana = this.findViewById(R.id.textView_edad_humana_mascota_informacion_mi_mascota);
        peso = this.findViewById(R.id.textView_peso_mascota_informacion_mi_mascota);
        num_vacunas = this.findViewById(R.id.textView_vacunas_mascota__informacion_mi_mascota);
        num_diagnosticos = this.findViewById(R.id.textView_diagnosticos_mascota_informacion_mi_mascota);

    }

    @Override
    protected void onStart() {
        super.onStart();
        rellenarDatos();
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(int num_vacunas_especie);
    }

    // Obtener el numero de vacunas de una especie
    public void getNumeroVacunasEspecie(final idCallback myCallback, String especie) {
        String especie_final = "";
        if(especie.equalsIgnoreCase("perro")){
            especie_final = "perros";
        }else if(especie.equalsIgnoreCase("gato")){
            especie_final = "gatos";
        }
        mDatabase.child("vacunas").child(especie_final).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num_vacunas_especie = (int) dataSnapshot.getChildrenCount();
                myCallback.onCallback(num_vacunas_especie);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }

    // Rellenar los campos con los datos de la mascota
    public void rellenarDatos(){
        String id_mascota = getIntent().getExtras().getString("id");
        // Referencia a los datos del cliente
        final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(user.getUid()).child("mascotas").child(id_mascota);
        ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                Glide.with(getApplicationContext()).load(data.get("foto")).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imagen);
                nombre.setText(data.get("nombre").toString());
                getSupportActionBar().setTitle("Información de " + data.get("nombre").toString());
                especie.setText(data.get("especie").toString());
                raza.setText(data.get("raza").toString());
                sexo.setText(data.get("sexo").toString());
                fecha_nacimiento.setText(data.get("fecha_nacimiento").toString());
                edad.setText(data.get("edad").toString());
                String[] edad_mascota = data.get("edad").toString().split(" ");
                int edad_num = Integer.parseInt(edad_mascota[0]);
                int edad_humana_mascota = edad_num*7;
                edad_humana.setText(edad_humana_mascota + " años");
                peso.setText(data.get("peso").toString());
                getNumeroVacunasEspecie(new idCallback() {
                    @Override
                    public void onCallback(int num_vacunas_especie) {
                        int num_vacunas_mascota = (int) dataSnapshot.child("vacunas").getChildrenCount();
                        num_vacunas.setText(num_vacunas_mascota +"/" + num_vacunas_especie);
                        int num_diagnosticos_mascota = (int) dataSnapshot.child("diagnosticos").getChildrenCount();
                        num_diagnosticos.setText(String.valueOf(num_diagnosticos_mascota));
                    }
                }, data.get("especie").toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }
}
