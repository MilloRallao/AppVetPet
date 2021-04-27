package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pantalla_agregar_diagnostico_tratamiento extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText diagnostico;
    private EditText anamnesis;
    private EditText pruebas;
    private EditText farmaco;
    private EditText dosis;
    private EditText formaAdministracion;
    private EditText fechaInicio;
    private EditText fechaFin;
    private Button agregarActualizar;
    private Spinner fechaFinSpinner;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String id_mascota;
    private String id_cliente;
    private int agregar_actualizar;
    private String id_diagnostico;
    private String tipo_fechaFin_seleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_agregar_diagnostico_tratamiento);

        toolbar = this.findViewById(R.id.toolbar_agregar_diagnostico_tratamiento);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Ir hacia atras con el boton de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        diagnostico = this.findViewById(R.id.editText_diagnostico_agregar_diagnostico_tratamiento);
        anamnesis = this.findViewById(R.id.editText_anamnesis_agregar_diagnostico_tratamiento);
        pruebas = this.findViewById(R.id.editText_pruebas_agregar_diagnostico_tratamiento);
        farmaco = this.findViewById(R.id.editText_farmaco_agregar_diagnostico_tratamiento);
        dosis = this.findViewById(R.id.editText_dosis_agregar_diagnostico_tratamiento);
        formaAdministracion = this.findViewById(R.id.editText_forma_administracion_agregar_diagnostico_tratamiento);
        fechaFinSpinner = this.findViewById(R.id.spinner_fecha_fin_agregar_diagnostico_tratamiento);

        // Acción al pulsar en el Editext para elegir la fecha de inicio del tratamiento
        fechaInicio = this.findViewById(R.id.editText_fecha_inicio_agregar_diagnostico_tratamiento);
        fechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fechaInicio);
            }
        });

        // Acción al pulsar en el Editext para elegir la fecha de finalización del tratamiento (Si procede)
        fechaFin = this.findViewById(R.id.editText_fecha_fin_agregar_diagnostico_tratamiento);
        fechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fechaFin);
            }
        });

        // Accion al pulsar en el botón de agregar diagnóstico (O actualizar diagnóstico)
        agregarActualizar = this.findViewById(R.id.button_agregar_diagnostico_tratamiento);
        agregarActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String diagnostico_input = diagnostico.getText().toString();
                final String anamnesis_input = anamnesis.getText().toString();
                final String pruebas_input = pruebas.getText().toString();
                final String farmaco_input = farmaco.getText().toString();
                final String dosis_input = dosis.getText().toString();
                final String administracion_input= formaAdministracion.getText().toString();
                final String fechaInicio_input = fechaInicio.getText().toString();
                final String fechaFin_input = fechaFin.getText().toString();
                final String tipo_fechaFin_input = tipo_fechaFin_seleccionado;
                // Control de campos vacíos
                if(diagnostico_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba un diagnóstico", Toast.LENGTH_SHORT).show();
                }else if(anamnesis_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba una anamnesis", Toast.LENGTH_SHORT).show();
                }else if(pruebas_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba las pruebas realizadas", Toast.LENGTH_SHORT).show();
                }else if(farmaco_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba el fármaco del tratamiento", Toast.LENGTH_SHORT).show();
                }else if(dosis_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba la dosis del fármaco", Toast.LENGTH_SHORT).show();
                }else if(administracion_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba la forma de administración del fármaco", Toast.LENGTH_SHORT).show();
                }else if(fechaInicio_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba la fecha de inicio del tratamiento", Toast.LENGTH_SHORT).show();
                }else if(tipo_fechaFin_input.equalsIgnoreCase("No Procede") && fechaFin_input.isEmpty()){
                    Toast.makeText(pantalla_agregar_diagnostico_tratamiento.this, "Escriba la fecha de finalización del tratamiento", Toast.LENGTH_SHORT).show();
                }else{
                    getIdClinicaFromFirebase(new idCallback() {
                        @Override
                        public void onCallback(String id_clinica) {
                            // Agregar diagnóstico y tratamiento
                            if(agregar_actualizar == 0){
                                final DatabaseReference ref_diagnostico = mDatabase.child("diagnosticos").child(id_clinica);
                                ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final int num_diagnosticos = (int) dataSnapshot.getChildrenCount();
                                        // Datos Diagnóstico
                                        final Map<String, Object> diagnosticoNuevo = new HashMap<>();
                                        diagnosticoNuevo.put("anamnesis", anamnesis_input);
                                        diagnosticoNuevo.put("diagnostico", diagnostico_input);
                                        // Fecha actual
                                        final Calendar c = Calendar.getInstance();
                                        int mYear = c.get(Calendar.YEAR);
                                        int mMonth = c.get(Calendar.MONTH);
                                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                                        String fecha_actual = mDay + "/" + mMonth + "/" + mYear;
                                        diagnosticoNuevo.put("fecha", fecha_actual);
                                        diagnosticoNuevo.put("id_cliente", id_cliente);
                                        diagnosticoNuevo.put("id_mascota", id_mascota);
                                        diagnosticoNuevo.put("pruebas", pruebas_input);
                                        // Datos tratamiento
                                        final Map<String, Object> tratamientoNuevo = new HashMap<>();
                                        tratamientoNuevo.put("administracion", administracion_input);
                                        tratamientoNuevo.put("dosis", dosis_input);
                                        tratamientoNuevo.put("farmaco", farmaco_input);
                                        if(tipo_fechaFin_input.equalsIgnoreCase("No Procede")){
                                            tratamientoNuevo.put("fecha_fin", fechaFin_input);
                                        }else{
                                            tratamientoNuevo.put("fecha_fin", tipo_fechaFin_input);
                                        }
                                        tratamientoNuevo.put("fecha_inicio", fechaInicio_input);


                                        // Referencia a la mascota del cliente
                                        final DatabaseReference ref_tratamientos = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
                                        ref_tratamientos.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                                diagnosticoNuevo.put("mascota", data.get("nombre"));
                                                diagnosticoNuevo.put("tratamiento", tratamientoNuevo);
                                                ref_diagnostico.child(String.valueOf(num_diagnosticos+1)).setValue(diagnosticoNuevo);
                                                // Referencia a los diagnósticos de la mascota
                                                ref_tratamientos.child("diagnosticos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        int num_diagnosticos_cliente = (int) dataSnapshot.getChildrenCount();
                                                        // Insertar nuevo diagnóstico en el cliente
                                                        mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("diagnosticos").child("diagnostico_"+ (num_diagnosticos_cliente + 1)).setValue(num_diagnosticos+1);
                                                        finish();
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("ERROR5", "onCancelled: "+databaseError);
                                                    }
                                                });
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d("ERROR6", "onCancelled: "+databaseError);
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR7", "onCancelled: "+databaseError);
                                    }
                                });
                            }else if(agregar_actualizar == 1){ // Actualizar diagnóstico y tratamiento
                                final DatabaseReference ref_diagnostico = mDatabase.child("diagnosticos").child(id_clinica).child(id_diagnostico);
                                ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Map<String, Object> diagnosticoActualizado = new HashMap<>();
                                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                        diagnosticoActualizado.put("anamnesis", anamnesis_input);
                                        diagnosticoActualizado.put("diagnostico", diagnostico_input);
                                        diagnosticoActualizado.put("fecha", data.get("fecha"));
                                        diagnosticoActualizado.put("id_cliente", data.get("id_cliente"));
                                        diagnosticoActualizado.put("id_mascota", data.get("id_mascota"));
                                        diagnosticoActualizado.put("mascota", data.get("mascota"));
                                        diagnosticoActualizado.put("pruebas", pruebas_input);

                                        Map<String, Object> tratamientoActualizado = new HashMap<>();
                                        tratamientoActualizado.put("administracion", administracion_input);
                                        tratamientoActualizado.put("dosis", dosis_input);
                                        tratamientoActualizado.put("farmaco", farmaco_input);
                                        if(tipo_fechaFin_input.equalsIgnoreCase("No Procede")){
                                            tratamientoActualizado.put("fecha_fin", fechaFin_input);
                                        }else{
                                            tratamientoActualizado.put("fecha_fin", tipo_fechaFin_input);
                                        }
                                        tratamientoActualizado.put("fecha_inicio", fechaInicio_input);
                                        diagnosticoActualizado.put("tratamiento", tratamientoActualizado);
                                        ref_diagnostico.setValue(diagnosticoActualizado);
                                        finish();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR8", "onCancelled: "+databaseError);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        id_mascota = getIntent().getExtras().getString("id_mascota");
        id_cliente = getIntent().getExtras().getString("id_cliente");
        agregar_actualizar = getIntent().getExtras().getInt("agregar_actualizar");
        id_diagnostico = getIntent().getExtras().getString("id_diagnostico");
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id);
    }

    // Obtener el id de la clinica del usuario
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

    @Override
    protected void onStart() {
        super.onStart();
        cargarTiposFechaFin();
        // Agregar un nuevo diagnóstico
        if(agregar_actualizar == 0){
            final DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
            ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    // Poner título de la Toolbar
                    String nombre_mascota = data.get("nombre").toString();
                    getSupportActionBar().setTitle("Nuevo Diagnóstico de " + nombre_mascota);
                    agregarActualizar.setText("Agregar Diagnóstico");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("ERROR1", "onCancelled: "+databaseError);
                }
            });
        }else if(agregar_actualizar == 1){ // Editar un diagnóstico ya existente
            agregarActualizar.setText("Actualizar Diagnóstico");
            final DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
            ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    // Poner título de la Toolbar
                    String nombre_mascota = data.get("nombre").toString();
                    getSupportActionBar().setTitle("Editar Diagnóstico de " + nombre_mascota);
                    getIdClinicaFromFirebase(new idCallback() {
                        @Override
                        public void onCallback(String id_clinica) {
                            final DatabaseReference ref_diagnostico = mDatabase.child("diagnosticos").child(id_clinica).child(id_diagnostico);
                            ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    diagnostico.setText(data.get("diagnostico").toString());
                                    anamnesis.setText(data.get("anamnesis").toString());
                                    pruebas.setText(data.get("pruebas").toString());
                                    DatabaseReference ref_tratamiento = ref_diagnostico.child("tratamiento");
                                    ref_tratamiento.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                            farmaco.setText(data.get("farmaco").toString());
                                            dosis.setText(data.get("dosis").toString());
                                            formaAdministracion.setText(data.get("administracion").toString());
                                            fechaInicio.setText(data.get("fecha_inicio").toString());
                                            // Si la fecha de finalización es una fecha
                                            if(!data.get("fecha_fin").toString().equalsIgnoreCase("Crónico") && !data.get("fecha_fin").toString().equalsIgnoreCase("Sin fecha")){
                                                fechaFin.setText(data.get("fecha_fin").toString());
                                            }else { // En caso contrario, se pone la opción cargada en el Spinner y el EditText para seleccionar fecha de finalización estaría inhabilitado
                                                fechaFin.setEnabled(false);
                                                if(data.get("fecha_fin").toString().equalsIgnoreCase("Crónico")){
                                                    fechaFinSpinner.setSelection(1);
                                                }else if(data.get("fecha_fin").toString().equalsIgnoreCase("Sin fecha")){
                                                    fechaFinSpinner.setSelection(2);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("ERROR2", "onCancelled: "+databaseError);
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERROR3", "onCancelled: "+databaseError);
                                }
                            });
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("ERROR4", "onCancelled: "+databaseError);
                }
            });
        }
    }

    // Cargar los tipos de la fecha de fin del tratamiento dentro del Spinner
    private void cargarTiposFechaFin(){
        List<String> tipos = new ArrayList<>();
        tipos.add(0, "No Procede");
        tipos.add(1, "Crónico");
        tipos.add(2, "Sin fecha");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, tipos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        fechaFinSpinner.setAdapter(adapter);
        fechaFinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id != 0){
                    tipo_fechaFin_seleccionado = parent.getItemAtPosition(position).toString();
                    fechaFin.setEnabled(false);
                }else{
                    tipo_fechaFin_seleccionado = parent.getItemAtPosition(position).toString();
                    fechaFin.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Mostrar el Dialog para escoger la fecha de la cita
    private void showDatePickerDialog(final EditText editText) {
        // Fecha del momento actual
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //Crear DatePicker Dialog que se mostrará
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}