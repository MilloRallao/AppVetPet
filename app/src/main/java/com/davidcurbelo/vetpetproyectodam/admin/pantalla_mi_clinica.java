package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;

public class pantalla_mi_clinica extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView logo;
    private TextView nombre;
    private EditText web;
    private EditText telefono1;
    private EditText telefono2;
    private EditText direccion;
    private EditText codigoPostal;
    private EditText localidad;
    private EditText horario;
    private EditText hora_apertura;
    private EditText hora_cierre;
    private EditText correo;
    private Button editar;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private String mMinuteFinal;
    private String id_clinica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_clinica);

        toolbar = this.findViewById(R.id.toolbar_mi_clinica_admin);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        logo = this.findViewById(R.id.imageView_logo_clinica_admin);
        nombre = this.findViewById(R.id.textView_nombre_clinica_admin);
        web = this.findViewById(R.id.editText_web_clinica_admin);
        telefono1 = this.findViewById(R.id.editText_telefono1_clinica_admin);
        telefono2 = this.findViewById(R.id.editText_telefono2_clinica_admin);
        direccion = this.findViewById(R.id.editText_direccion_clinica_admin);
        codigoPostal = this.findViewById(R.id.editText_codigo_postal_clinica_admin);
        localidad = this.findViewById(R.id.editText_localidad_clinica_admin);
        horario = this.findViewById(R.id.editText_horario_dias_clinica_admin);
        correo = this.findViewById(R.id.editText_email_clinica_admin);

        hora_apertura = this.findViewById(R.id.editText_horario_hora_inicio_clinica_admin);
        // Accion al presionar el EditText para elegir el día de la cita
        hora_apertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(hora_apertura);
            }
        });

        hora_cierre = this.findViewById(R.id.editText_horario_hora_fin_clinica_admin);
        // Accion al presionar el EditText para elegir la hora de la cita
        hora_cierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(hora_cierre);
            }
        });

        // Accion al presionar el botón "Editar" para editar la información
        editar = this.findViewById(R.id.button_editar_informacion_mi_clinica);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando el botón se muestre como "Editar", habilitar todos los campos para poder modificarlos
                if(editar.getText().toString().contains("Editar")){
                    editar.setText("Actualizar información");
                    web.setEnabled(true);
                    telefono1.setEnabled(true);
                    telefono2.setEnabled(true);
                    direccion.setEnabled(true);
                    codigoPostal.setEnabled(true);
                    localidad.setEnabled(true);
                    horario.setEnabled(true);
                    hora_apertura.setEnabled(true);
                    hora_cierre.setEnabled(true);
                    correo.setEnabled(true);
                }else if(editar.getText().toString().contains("Actualizar")){ // Cuando el botón se muestre como "Actualizar", actualizar cada campo según corresponda
                    // Firebase permite actualizar valores individuales y también conjuntos, pero en este segundo caso, si no pasan todos los demás datos que no se vayan a actualizar, serán borrados.
                    // Habría 2 maneras de proceder: 1) sería la que sigue y 2) sería guardar todos los datos del nodo en un Hashmap e introducir los valores que serán actualizados y luego hacer un setValue() pasándole ese Hashmap
                    final DatabaseReference ref_clinica = mDatabase.child("clinicas").child(id_clinica);
                    ref_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ref_clinica.child("web").setValue(web.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("telefono").setValue(telefono1.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("movil").setValue(telefono2.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("direccion").setValue(direccion.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("cp").setValue(codigoPostal.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("localidad").setValue(localidad.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("horario_dias").setValue(horario.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("horario_horas").setValue(hora_apertura.getText().toString() + "-" + hora_cierre.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_clinica.child("email").setValue(correo.getText().toString());
                                    Toast.makeText(getApplicationContext(), "¡Datos actualizados!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), pantalla_mi_clinica.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR0", "onCancelled: "+databaseError);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        rellenarDatos();
    }

    // Rellenar los campos con los datos de la clinica
    public void rellenarDatos(){
        // Referencia a los datos del usuario
        DatabaseReference ref_data_user = mDatabase.child("usuarios").child(currentUser.getUid());
        ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                id_clinica = data.get("clinica").toString();
                ponerLogo(id_clinica);
                // Referencia a los datos de la clinica
                DatabaseReference ref_data_clinica = mDatabase.child("clinicas").child(id_clinica);
                ref_data_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        nombre.setText(data.get("nombre").toString());
                        web.setText(data.get("web").toString());
                        telefono1.setText(data.get("telefono").toString());
                        telefono2.setText(data.get("movil").toString());
                        direccion.setText(data.get("direccion").toString());
                        codigoPostal.setText(data.get("cp").toString());
                        localidad.setText(data.get("localidad").toString());
                        horario.setText(data.get("horario_dias").toString());
                        hora_apertura.setText(data.get("horario_horas").toString().substring(0, 5));
                        hora_cierre.setText(data.get("horario_horas").toString().substring(6));
                        correo.setText(data.get("email").toString());
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
                Glide.with(getApplicationContext()).load(finalLocalFile).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(logo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "ALGO HA FALLADO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostrar el Dialog para escoger la hora de apertura y cierre de la clínica
    private void showTimePickerDialog(final EditText editText){
        // Hora del momento actual
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Crear TimePicker Dialog que se mostrará
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Formatear la fecha para que aparezca en formato HH:MM
                mMinuteFinal = String.format("%02d:%02d", hourOfDay, minute);
                editText.setText(mMinuteFinal);
            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
    }
}
