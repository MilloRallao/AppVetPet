package com.davidcurbelo.vetpetproyectodam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.admin.pantalla_principal_admin;
import com.davidcurbelo.vetpetproyectodam.cliente.pantalla_principal_usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class pantalla_login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private EditText user;
    private EditText password;
    private Button login;
    private Button registro;
    private ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Referencia a la BD en Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

        user = this.findViewById(R.id.editText_usuario);
        password = this.findViewById(R.id.editText_password);
        login = this.findViewById(R.id.button_login);
        registro = this.findViewById(R.id.button_registro);
        progreso = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarUsuario();
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), pantalla_registro.class);
                startActivity(intent);
            }
        });
    }

    private void comprobarUsuario(){
        String usuario = user.getText().toString().trim();
        String contrasena = password.getText().toString().trim();
        final Intent intent_cliente = new Intent(this, pantalla_principal_usuario.class);
        final Intent intent_admin = new Intent(this, pantalla_principal_admin.class);
        // Control de campos vacíos
        if(usuario.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba su nombre de usuario.", Toast.LENGTH_SHORT).show();
        }
        else if(contrasena.isEmpty()){
            Toast.makeText(getApplicationContext(), "Escriba su contraseña.", Toast.LENGTH_SHORT).show();
        }
        else{ // Mostrar Dialog de progreso
            progreso.setMessage("Logueando");
            progreso.show();
            // Comprobar credenciales de usuario
            mAuth.signInWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // Éxito
                    if (task.isSuccessful()) {
                        // Referencia al usuario en la BD
                        mDatabase.child(task.getResult().getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Datos del usuario
                                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                // Distinguir tipos de usuario y enviar a su respectiva pantalla de la aplicación
                                if(data.get("tipo_usuario").toString().equalsIgnoreCase("Administrador")){
                                    Toast.makeText(getApplicationContext(), "Autenticación correcta.", Toast.LENGTH_SHORT).show();
                                    startActivity(intent_admin);
                                }
                                else if(data.get("tipo_usuario").toString().equalsIgnoreCase("Cliente")){
                                    Toast.makeText(getApplicationContext(), "Autenticación correcta.", Toast.LENGTH_SHORT).show();
                                    startActivity(intent_cliente);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR1", "onCancelled: "+databaseError);
                            }
                        });
                    } else { // Fracaso
                        Toast.makeText(getApplicationContext(), "Email o contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                    }
                    progreso.dismiss();
                }
            });
        }
    }
}
