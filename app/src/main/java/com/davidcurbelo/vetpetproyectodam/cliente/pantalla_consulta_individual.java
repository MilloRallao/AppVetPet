package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Date;
import java.util.Map;

public class pantalla_consulta_individual extends AppCompatActivity {
    private CardView cardView_usuario;
    private CardView cardView_veterinario;
    private Toolbar toolbar;
    private TextView fecha;
    private TextView hora;
    private TextView nombre;
    private TextView asunto;
    private TextView mensaje;
    private ImageView imagen;
    private ImageView adjunto1;
    private ImageView adjunto2;
    private ImageView adjunto3;
    private ImageView zoom1;
    private ImageView zoom2;
    private ImageView zoom3;
    private String aux_adjunto1;
    private String aux_adjunto2;
    private String aux_adjunto3;
    private String aux_id;
    private String aux_num_adjuntos;
    private String aux_estado;
    private String aux_respuesta;

    private TextView fecha_respuesta;
    private TextView hora_respuesta;
    private TextView mensaje_respuesta;
    private Button pedir_cita;
    private ImageView llamar;
    private String id_clinica;
    private String telefono2_clinica;

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_consulta_individual);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cardView_usuario = this.findViewById(R.id.cardview_individual_respuesta_usuario);
        cardView_veterinario = this.findViewById(R.id.cardview_individual_respuesta_veterinario);

        toolbar = this.findViewById(R.id.toolbar_consulta_individual);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Ir hacia atras con el boton de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fecha = this.findViewById(R.id.textView_fecha_consulta_individual);
        hora = this.findViewById(R.id.textView_hora_consulta_individual);
        nombre = this.findViewById(R.id.textView_nombre_mascota_consulta_individual);
        asunto = this.findViewById(R.id.textView_asunto_consulta_individual);
        mensaje = this.findViewById(R.id.textView_mensaje_consulta_individual);
        imagen = this.findViewById(R.id.imageView_mascota_consulta_individual);

        // Accion al pinchar sobre la imagen adjunta 1, se amplia la imagen ocupando toda la pantalla
        adjunto1 = this.findViewById(R.id.imageView_adjunto_1_consulta_individual);
        adjunto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto1, aux_adjunto1, null);
            }
        });

        // Accion al pinchar sobre la imagen adjunta 2, se amplia la imagen ocupando toda la pantalla
        adjunto2 = this.findViewById(R.id.imageView_adjunto_2_consulta_individual);
        adjunto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto2, aux_adjunto2, null);
            }
        });

        // Accion al pinchar sobre la imagen adjunta 3, se amplia la imagen ocupando toda la pantalla
        adjunto3 = this.findViewById(R.id.imageView_adjunto_3_consulta_individual);
        adjunto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto3, aux_adjunto3, null);
            }
        });

        zoom1 = this.findViewById(R.id.imageView_zoom_1);
        zoom2 = this.findViewById(R.id.imageView_zoom_2);
        zoom3 = this.findViewById(R.id.imageView_zoom_3);

        fecha_respuesta = this.findViewById(R.id.textView_fecha_respuesta_veterinario_consulta_individual);
        hora_respuesta = this.findViewById(R.id.textView_hora_respuesta_veterinario_consulta_individual);
        mensaje_respuesta = this.findViewById(R.id.textView_respuesta_veterinario_consulta_individual);

        // Accion al pulsar boton para pedir cita, lleva al activity de confirmar cita
        pedir_cita = this.findViewById(R.id.button_pedir_cita_consulta_individual);
        pedir_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_confirmar_cita.class);
                startActivity(intent);
            }
        });

        // Accion al pulsar sobre la imagen del telefono para llamar a la clinica
        llamar = this.findViewById(R.id.imageview_llamar_consulta_individual);
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tlfn = Integer.parseInt(telefono2_clinica.trim().replace(" ", ""));
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + tlfn)));
            }
        });

        aux_id = getIntent().getExtras().getString("id");
        String aux_fecha = getIntent().getExtras().getString("fecha");
        String aux_hora = getIntent().getExtras().getString("hora");
        String aux_nombre = getIntent().getExtras().getString("nombre");
        String aux_asunto = getIntent().getExtras().getString("asunto");
        String aux_mensaje = getIntent().getExtras().getString("mensaje");
        String aux_imagen = getIntent().getExtras().getString("imagen");

        fecha.setText(aux_fecha);
        hora.setText(aux_hora);
        nombre.setText(aux_nombre);
        asunto.setText(aux_asunto);
        mensaje.setText(aux_mensaje);
        Glide.with(this).load(aux_imagen).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imagen);
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id);
    }

    // Obtener el id de la clinica del usuario
    public void getIdClinicaFromFirebase(final idCallback myCallback) {
        // Referencia a los datos del usuario en la BD
        mDatabase.child("usuarios").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                id_clinica = data.get("clinica").toString();
                myCallback.onCallback(id_clinica);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Obtener el telefono de la clinica del usuario para asociarlo al boton de llamada
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id) {
                id_clinica = id;
                // Referencia a la clinica
                DatabaseReference ref_data_clinica = mDatabase.child("clinicas").child(id_clinica);
                ref_data_clinica.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Obtener el telefono de la clinica
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        telefono2_clinica = data.get("telefono").toString();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR1", "onCancelled: "+databaseError);
                    }
                });
            }
        });

        // Mostrar la respuesta del veterinario solo en caso de haya respondido a la consulta
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id) {
                id_clinica = id;
                // Consulta respondida o no por el veterinario
                aux_respuesta = getIntent().getExtras().getString("respuesta");
                // Si está respondida, cargar los datos desde la BD y mostrar el cardview de la respuesta del veterinario
                if(aux_respuesta.equalsIgnoreCase("Respondido")){
                    // Referencia a los datos de la respuesta del veterinario a la consulta
                    DatabaseReference ref_data_respuesta = mDatabase.child("consultas").child(id_clinica).child(aux_id).child("respuesta");
                    ref_data_respuesta.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            fecha_respuesta.setText(data.get("fecha").toString());
                            hora_respuesta.setText(data.get("hora").toString());
                            mensaje_respuesta.setText(data.get("mensaje").toString());
                            cardView_veterinario.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR2", "onCancelled: "+databaseError);
                        }
                    });
                }else {
                    cardView_veterinario.setVisibility(View.INVISIBLE);
                }
            }
        });
        // Consulta cerrada o abierta
        aux_estado = getIntent().getExtras().getString("estado");
        // Cambiar el titulo de la toolbar y el color de ambos cardview segun el estado de la consulta
        if(aux_estado.equalsIgnoreCase("Cerrada")){
            getSupportActionBar().setTitle("Consulta Cerrada #"+aux_id);
            cardView_usuario.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorNegative)));
            cardView_veterinario.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorNegative)));
        }else if(aux_estado.equalsIgnoreCase("Abierta")){
            getSupportActionBar().setTitle("Consulta Abierta #"+aux_id);
        }
        // Numero de imagenes adjuntas
        aux_num_adjuntos = getIntent().getExtras().getString("num_adjuntos");
        // Controlar el numero de imagenes adjuntas a mostrar
        switch (aux_num_adjuntos){
            case "1":
                aux_adjunto1 = getIntent().getExtras().getString("adjunto1");
                Glide.with(this).load(aux_adjunto1).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto1);
                zoom1.setVisibility(View.VISIBLE);
                break;
            case "2":
                aux_adjunto1 = getIntent().getExtras().getString("adjunto1");
                aux_adjunto2 = getIntent().getExtras().getString("adjunto2");
                Glide.with(this).load(aux_adjunto1).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto1);
                Glide.with(this).load(aux_adjunto2).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto2);
                zoom1.setVisibility(View.VISIBLE);
                zoom2.setVisibility(View.VISIBLE);
                break;
            case "3":
                aux_adjunto1 = getIntent().getExtras().getString("adjunto1");
                aux_adjunto2 = getIntent().getExtras().getString("adjunto2");
                aux_adjunto3 = getIntent().getExtras().getString("adjunto3");
                Glide.with(this).load(aux_adjunto1).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto1);
                Glide.with(this).load(aux_adjunto2).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto2);
                Glide.with(this).load(aux_adjunto3).apply(new RequestOptions().transform(new RoundedCorners(15)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(adjunto3);
                zoom1.setVisibility(View.VISIBLE);
                zoom2.setVisibility(View.VISIBLE);
                zoom3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
