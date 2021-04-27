package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidcurbelo.vetpetproyectodam.R;
import com.davidcurbelo.vetpetproyectodam.pantalla_mapa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class pantalla_ver_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView icono_cliente;
    private TextView nombre_cliente;
    private ImageButton llamar_cliente;
    private TextView id_cliente;
    private TextView dni_cliente;
    private TextView direccion_cliente;
    private TextView localidad_cliente;
    private TextView telefono_cliente;
    private TextView email_cliente;
    private TextView num_mmascotas_cliente;
    private TextView num_citas_cliente;
    private TextView num_consultas_cliente;
    private Button ver_mapa;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_cliente);

        toolbar = this.findViewById(R.id.toolbar_ver_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        icono_cliente = this.findViewById(R.id.imageView_icono_cliente_ver_cliente);
        nombre_cliente = this.findViewById(R.id.textView_nombre_cliente_ver_cliente);
        id_cliente = this.findViewById(R.id.textView_id_cliente_ver_cliente);
        dni_cliente = this.findViewById(R.id.textView_dni_cliente_ver_cliente);
        direccion_cliente = this.findViewById(R.id.textView_direccion_cliente_ver_cliente);
        localidad_cliente = this.findViewById(R.id.textView_localidad_cliente_ver_cliente);
        telefono_cliente = this.findViewById(R.id.textView_telefono_cliente_ver_cliente);
        email_cliente = this.findViewById(R.id.textView_email_cliente_ver_cliente);
        num_mmascotas_cliente = this.findViewById(R.id.textView_numero_mascotas_cliente_ver_cliente);
        num_citas_cliente = this.findViewById(R.id.textView_numero_citas_cliente_ver_cliente);
        num_consultas_cliente = this.findViewById(R.id.textView_numero_consultas_cliente_ver_cliente);

        // Accion de pulsar en el botón de la llamada, abre el marcador de llamadas del móvil
        llamar_cliente = this.findViewById(R.id.imageButton_llamar_cliente_ver_cliente);
        llamar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + telefono_cliente.getText().toString())));
            }
        });

        // Acción al pulsar en el botón de ver en mapa, dirige a la activity de un Google Maps para ver la localización del cliente
        ver_mapa = this.findViewById(R.id.button_ver_mapa_cliente_ver_cliente);
        ver_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_mapa.class);
                intent.putExtra("titulo", nombre_cliente.getText().toString());
                intent.putExtra("direccion", direccion_cliente.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String id_cliente_Intent = getIntent().getExtras().getString("id_cliente");
        DatabaseReference ref_cliente = mDatabase.child("usuarios").child(id_cliente_Intent);
        ref_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String genero = data.get("genero").toString();
                if(genero.equalsIgnoreCase("Mujer")){
                    icono_cliente.setImageResource(R.drawable.genero_mujer);
                }else if(genero.equalsIgnoreCase("Hombre")){
                    icono_cliente.setImageResource(R.drawable.genero_hombre);
                }
                String nombre = data.get("nombre").toString();
                String apellidos = data.get("apellidos").toString();
                nombre_cliente.setText(nombre.concat(" ").concat(apellidos));
                getSupportActionBar().setTitle("Datos de " + nombre + " " + apellidos);
                String telefono = data.get("telefono").toString();
                telefono_cliente.setText(telefono);
                id_cliente.setText(id_cliente_Intent);
                String dni = data.get("dni").toString();
                dni_cliente.setText(dni);
                String direccion = data.get("direccion").toString();
                direccion_cliente.setText(direccion);
                String localidad = data.get("localidad").toString();
                localidad_cliente.setText(localidad);
                String email = data.get("email").toString();
                email_cliente.setText(email);
                String num_mascotas = String.valueOf(dataSnapshot.child("mascotas").getChildrenCount());
                num_mmascotas_cliente.setText(num_mascotas);
                String num_citas = String.valueOf(dataSnapshot.child("citas").getChildrenCount());
                num_citas_cliente.setText(num_citas);
                String num_consultas = String.valueOf(dataSnapshot.child("consultas").getChildrenCount());
                num_consultas_cliente.setText(num_consultas);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }
}
