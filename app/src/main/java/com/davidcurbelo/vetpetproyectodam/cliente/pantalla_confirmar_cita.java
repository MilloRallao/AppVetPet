package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pantalla_confirmar_cita extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private ImageButton llamada;
    private Spinner spinner_mascotas;
    private Spinner spinner_motivos;
    private EditText dia;
    private EditText hora;
    private Button confirmar_cita;

    private String mascota_seleccionada = "";
    private String motivo_seleccionado;
    private String tlfno;
    private String clinica_id;
    private long id_mascota;
    private String nombre_usuario;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_confirmar_cita);

        toolbar = this.findViewById(R.id.toolbar_confirmar_cita);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        llamada = this.findViewById(R.id.imageButton_llamar_confirmar_cita);
        spinner_mascotas = this.findViewById(R.id.spinner_mascotas_confirmar_cita);
        spinner_motivos = this.findViewById(R.id.spinner_motivos_confirmar_cita);
        dia = this.findViewById(R.id.editText_dia);
        hora = this.findViewById(R.id.editText_hora);
        confirmar_cita = this.findViewById(R.id.button_confirmar_cita);

        // Accion al presionar la imagen para llamar a la clinica
        llamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tlfn = Integer.parseInt(tlfno);
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tlfn)));
            }
        });

        // Accion al presionar el EditText para elegir el día de la cita
        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Accion al presionar el EditText para elegir la hora de la cita
        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Accion al presionar el boton de confirmar cita
        confirmar_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();
                String dia_seleccionado = dia.getText().toString();
                String hora_seleccionada = hora.getText().toString();
                // Control de campos vacíos
                if(dia_seleccionado.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Elija una fecha para la cita", Toast.LENGTH_SHORT).show();
                }else if( hora_seleccionada.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Elija una hora para la cita", Toast.LENGTH_SHORT).show();
                }else if(mascota_seleccionada.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Elija una mascota la cita", Toast.LENGTH_SHORT).show();
                }else{
                    // Mapa de datos de la cita que será insertada en la BD
                    final HashMap<String, Object> dataCita = new HashMap<>();
                    dataCita.put("id_cliente", user.getUid());
                    dataCita.put("nombre_cliente", nombre_usuario);
                    dataCita.put("id_mascota", id_mascota);
                    dataCita.put("mascota", mascota_seleccionada);
                    dataCita.put("motivo", motivo_seleccionado);
                    dataCita.put("dia", dia_seleccionado);
                    dataCita.put("hora", hora_seleccionada);
                    dataCita.put("estado", "Abierta");
                    // Guardar el timestamp(ms) de la fecha de la cita
                    String fecha = dia_seleccionado + " " + hora_seleccionada;
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fecha);
                        Timestamp timestamp = new Timestamp(date.getTime());
                        dataCita.put("timestamp", timestamp.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Referencia a las citas de la clinica del usuario
                    DatabaseReference ref_citas = mDatabase.child("citas").child(clinica_id);
                    ref_citas.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Control de la primera vez que se inserten citas en la clinica del usuario
                            if(dataSnapshot.getChildrenCount() == 0){
                                // Inserción de datos de la cita en "citas" en la BD
                                mDatabase.child("citas").child(clinica_id).child("1").setValue(dataCita);
                                // Referencia a las citas del usuario
                                final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(user.getUid()).child("citas");
                                ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Control de la primera vez que se inserten citas en el usuario
                                        if(dataSnapshot.getChildrenCount() == 0){
                                            ref_data_user.child("id_cita_"+1).setValue(1);
                                        // Control de si hay mas de una cita ya insertada en el usuario
                                        }else{
                                            ref_data_user.child("id_cita_"+(dataSnapshot.getChildrenCount()+1)).setValue(1);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR4", "onCancelled: "+databaseError);
                                    }
                                });
                            // Control de si hay más de una cita ya creada para la clinica del usuario
                            }else{
                                final Long id_cita = dataSnapshot.getChildrenCount()+1;
                                // Inserción de datos de la cita en "citas" en la BD
                                mDatabase.child("citas").child(clinica_id).child(String.valueOf(id_cita)).setValue(dataCita);
                                // Referencia a las citas del usuario para insertar sus referencias
                                final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(user.getUid()).child("citas");
                                ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Control de la primera vez que se inserten citas en el usuario
                                        if(dataSnapshot.getChildrenCount() == 0){
                                            ref_data_user.child("id_cita_"+1).setValue(id_cita);
                                        // Control de si hay mas de una cita ya insertada en el usuario
                                        }else{
                                            ref_data_user.child("id_cita_"+(dataSnapshot.getChildrenCount()+1)).setValue(id_cita);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR5", "onCancelled: "+databaseError);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR3", "onCancelled: "+databaseError);
                        }
                    });
                    // Mostrar Dialog de confirmacion de creacion exitosa de cita
                    showConfirmDialog();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarMascotas();
        cargarMotivos();
        // UID usuario
        String uid = currentUser.getUid();
        // Referencia a los datos del usuario
        DatabaseReference ref_data_user = mDatabase.child("usuarios").child(uid);
        ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                clinica_id = data.get("clinica").toString();
                nombre_usuario = data.get("nombre").toString() + " " + data.get("apellidos");
                DatabaseReference ref_clinica = mDatabase.child("clinicas").child(clinica_id);
                ref_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        tlfno = data.get("telefono").toString().trim().replace(" ", "");
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

    // Cargar Dialog personalizado de confirmación de consulta
    private void showConfirmDialog() {
        Dialog dialog = new Dialog(this);
        // Asignar diseño de layout al Dialog
        dialog.setContentView(R.layout.confirm_dialog_cita);
        // Referencias a los botones del Dialog
        Button volver = dialog.findViewById(R.id.button_volver_cita);
        Button ver_consulta = dialog.findViewById(R.id.button_ver_cita);
        // Accion del boton volver (Pasar a la activity correspondiente)
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_pedir_cita.class);
                startActivity(intent);
            }
        });
        // Accion del boton ver consulta (Pasar a la activity correspondiente)
        ver_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_proximas_citas.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    // Cargar los motivos de la cita dentro del Spinner
    private void cargarMotivos(){
        List<String> motivos = new ArrayList<>();
        motivos.add(0, "Consulta");
        motivos.add(1, "Vacuna");
        motivos.add(2, "Revisión");
        motivos.add(3, "Peluquería");
        motivos.add(4, "Tienda");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, motivos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_motivos.setAdapter(adapter);
        spinner_motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                motivo_seleccionado = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Cargar las mascotas del usuario dentro del Spinner
    private void cargarMascotas(){
        // Lista de los nombres de las mascotas
        final List<String> mascotas = new ArrayList<>();
        // Referencia al usuario logueado
        FirebaseUser user = mAuth.getCurrentUser();
        // Referencia al nodo de las mascotas del usuario logueado de la BD
        DatabaseReference ref_mascotas = mDatabase.child("usuarios").child(user.getUid()).child("mascotas");
        // Rellenar la lista de nombres de mascota
        ref_mascotas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Acceder a todos los nodos hijo del nodo "mascotas"
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    // Nombre de una mascota
                    String nombre_mascota = ds.child("nombre").getValue().toString();
                    // Agregar el nombre de una mascota a la lista de nombres de mascota
                    mascotas.add(nombre_mascota);
                }
                // Crear el Adapter para rellenar de datos el Spinner con los nombres de las mascotas del usuario logueado
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, mascotas);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                // Asignar el Adapter al Spinner
                spinner_mascotas.setAdapter(arrayAdapter);
                // Establecer la opcion seleccionada del Spinner y mostrarla
                spinner_mascotas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mascota_seleccionada = parent.getItemAtPosition(position).toString();
                        id_mascota = id;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "ALGO HA FALLADO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostrar el Dialog para escoger la fecha de la cita
    private void showDatePickerDialog() {
        // Fecha del momento actual
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //Crear DatePicker Dialog que se mostrará
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dia.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
            }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // Mostrar el Dialog para escoger la hora de la cita
    private void showTimePickerDialog(){
        // Hora del momento actual
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Crear TimePicker Dialog que se mostrará
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Formatear la fecha para que aparezca en formato HH:MM
                final String mMinuteFinal = String.format("%02d:%02d", hourOfDay, minute);
                hora.setText(mMinuteFinal);
            }
            }, mHour, mMinute, true);
        timePickerDialog.show();
    }
}
