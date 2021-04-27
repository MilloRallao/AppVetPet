package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class pantalla_agregar_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText nombre;
    private EditText apellidos;
    private EditText dni;
    private EditText telefono;
    private EditText direccion;
    private EditText localidad;
    private EditText email;
    private EditText password;
    private Button agregar_cliente;
    private RadioGroup radioGroup;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth mAuth2;

    private String genero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_agregar_cliente);

        toolbar = this.findViewById(R.id.toolbar_agregar_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Opciones de la base de datos de Firebase
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[https://vetpet-proyecto-dam.firebaseio.com/]")
                .setApiKey("AIzaSyBzVh8oqdVJvBW2BaKU-SHYPTzkIxj-lQs")
                .setApplicationId("vetpet-proyecto-dam").build();

        // Crear un segundo FirebaseAuth concurrente al primero para no perder la conexión del admin al crear un nuevo usuario
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "AnyAppName");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }

        nombre = this.findViewById(R.id.editText_nombre_agregar_cliente);
        apellidos = this.findViewById(R.id.editText_apellidos_agregar_cliente);
        dni = this.findViewById(R.id.editText_dni_agregar_cliente);
        telefono = this.findViewById(R.id.editText_telefono_agregar_cliente);
        direccion = this.findViewById(R.id.editText_direccion_agregar_cliente);
        localidad = this.findViewById(R.id.editText_localidad_agregar_cliente);
        email = this.findViewById(R.id.editText_email_agregar_cliente);
        password = this.findViewById(R.id.editText_contrasena_agregar_cliente);
        radioGroup = this.findViewById(R.id.radiogroup_genero);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton_hombre){
                    genero = "Hombre";
                }else if(checkedId == R.id.radioButton_mujer){
                    genero = "Mujer";
                }
            }
        });

        // Accion del boton "Dar de alta" para agregar un cliente a la base de datos
        agregar_cliente = this.findViewById(R.id.button_agregar_cliente);
        agregar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar_cliente();
            }
        });
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id_clinica);
    }

    // Obtener ID de la clinica del veterinario
    public void getIdClinica(final idCallback myCallback){
        mDatabase.child("usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String clinica_id = data.get("clinica").toString();
                myCallback.onCallback(clinica_id);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR3", "onCancelled: "+databaseError);
            }
        });
    }

    // Agregar al cliente a la base de datos introduciendo todos sus datos
    public void agregar_cliente(){
        final String nombre = this.nombre.getText().toString().trim();
        final String apellidos = this.apellidos.getText().toString().trim();
        final String dni = this.dni.getText().toString().trim();
        final String telefono = this.telefono.getText().toString().trim();
        final String direccion = this.direccion.getText().toString().trim();
        final String localidad = this.localidad.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        // Control de campos vacíos
        if(nombre.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba el nombre del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba la contraseña del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba el correo del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(apellidos.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba los apellidos del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(dni.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba el DNI/NIE del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(telefono.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba el teléfono del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(direccion.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba la dirección del cliente.", Toast.LENGTH_SHORT).show();
        }
        else if(localidad.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba la localidad del cliente.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                mAuth2.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    String ex = task.getException().toString();
                                    Toast.makeText(getApplicationContext(), "Registrado fallido"+ex, Toast.LENGTH_LONG).show();
                                } else {
                                    // Obtener el usuario recién creado
                                    final FirebaseUser cliente = mAuth2.getCurrentUser();;
                                    getIdClinica(new idCallback() {
                                        @Override
                                        public void onCallback(String id_clinica) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("nombre",nombre);
                                            data.put("apellidos", apellidos);
                                            data.put("dni", dni);
                                            data.put("direccion", direccion);
                                            data.put("localidad", localidad);
                                            data.put("telefono", telefono);
                                            data.put("email", email);
                                            data.put("password", password);
                                            data.put("clinica", id_clinica);
                                            data.put("tipo_usuario", "Cliente");
                                            data.put("genero", genero);
                                            // Insertar los datos del usuario creado en Firebase DB
                                            mDatabase.child("usuarios").child(cliente.getUid()).setValue(data);
                                            // Referencia a los clientes de la clinica
                                            final DatabaseReference ref_clinica = mDatabase.child("clinicas").child(id_clinica).child("clientes");
                                            ref_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    // Control de la primera vez que se inserte un cliente en la clinica
                                                    if(dataSnapshot.getChildrenCount() == 0){
                                                        ref_clinica.child("id_usuario_"+1).setValue(cliente.getUid());
                                                    }else{ // Control de si hay mas de un cliente insertado en la clinica
                                                        ref_clinica.child("id_usuario_"+(dataSnapshot.getChildrenCount()+1)).setValue(cliente.getUid());
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Cliente registrado correctamente.", Toast.LENGTH_SHORT).show();
                                                    // Desconectar al usuario creado
                                                    mAuth2.signOut();
                                                    // Mostrar Dialog
                                                    showConfirmDialog();
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.d("ERROR", "onCancelled: "+databaseError);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ocurrió algún fallo.", Toast.LENGTH_SHORT).show();
            }finally {
            }
        }
    }

    // Cargar Dialog personalizado de éxito al agregar nuevo cliente
    private void showConfirmDialog() {
        Dialog dialog = new Dialog(this);
        // Asignar diseño de layout al Dialog
        dialog.setContentView(R.layout.confirm_dialog_agregar_cliente);
        // Referencias a los botones del Dialog
        Button volver = dialog.findViewById(R.id.button_volver_agregar_cliente);
        Button agregar_mascota = dialog.findViewById(R.id.button_agregar_mascota);
        // Accion del boton volver (Pasar a la activity correspondiente)
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_principal_admin.class);
                startActivity(intent);
            }
        });
        // Accion del boton ver consulta (Pasar a la activity correspondiente)
        agregar_mascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_agregar_mascota.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
}
