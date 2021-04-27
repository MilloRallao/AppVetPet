package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.davidcurbelo.vetpetproyectodam.cliente.PhotoFullPopupWindow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class pantalla_consultas_individual_admin extends AppCompatActivity {
    private Toolbar toolbar;
    private CardView cardView_usuario;
    private CardView cardView_veterinario;
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
    private String aux_respuesta;

    private Button responder;
    private EditText respuesta;
    private Button enviar_respuesta;

    private TextView fecha_respuesta;
    private TextView hora_respuesta;
    private TextView mensaje_respuesta;
    private Button cerrar_consulta;

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_consultas_individual_admin);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cardView_usuario = this.findViewById(R.id.cardview_individual_usuario_admin);
        cardView_veterinario = this.findViewById(R.id.cardview_individual_respuesta_admin);

        toolbar = this.findViewById(R.id.toolbar_consulta_individual_admin);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Ir hacia atras con el boton de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fecha = this.findViewById(R.id.textView_fecha_consulta_individual_admin);
        hora = this.findViewById(R.id.textView_hora_consulta_individual_admin);
        nombre = this.findViewById(R.id.textView_nombre_mascota_consulta_individual_admin);
        asunto = this.findViewById(R.id.textView_asunto_consulta_individual_admin);
        mensaje = this.findViewById(R.id.textView_mensaje_consulta_individual_admin);
        imagen = this.findViewById(R.id.imageView_mascota_consulta_individual_admin);

        final String aux_num_adjuntos = getIntent().getExtras().getString("num_adjuntos");
        final String aux_fecha = getIntent().getExtras().getString("fecha");
        final String aux_hora = getIntent().getExtras().getString("hora");
        final String aux_respuesta = getIntent().getExtras().getString("respuesta");
        final String aux_nombre = getIntent().getExtras().getString("nombre");
        final String aux_asunto = getIntent().getExtras().getString("asunto");
        final String aux_mensaje = getIntent().getExtras().getString("mensaje");
        final String aux_imagen = getIntent().getExtras().getString("imagen");
        final String aux_estado = getIntent().getExtras().getString("estado");
        final String id_consulta = getIntent().getExtras().getString("id");

        fecha.setText(aux_fecha);
        hora.setText(aux_hora);
        nombre.setText(aux_nombre);
        asunto.setText(aux_asunto);
        mensaje.setText(aux_mensaje);
        Glide.with(this).load(aux_imagen).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imagen);

        // Accion al pinchar sobre la imagen adjunta 1, se amplia la imagen ocupando toda la pantalla
        adjunto1 = this.findViewById(R.id.imageView_adjunto_1_consulta_individual_admin);
        adjunto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto1, aux_adjunto1, null);
            }
        });

        // Accion al pinchar sobre la imagen adjunta 2, se amplia la imagen ocupando toda la pantalla
        adjunto2 = this.findViewById(R.id.imageView_adjunto_2_consulta_individual_admin);
        adjunto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto2, aux_adjunto2, null);
            }
        });

        // Accion al pinchar sobre la imagen adjunta 3, se amplia la imagen ocupando toda la pantalla
        adjunto3 = this.findViewById(R.id.imageView_adjunto_3_consulta_individual_admin);
        adjunto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, adjunto3, aux_adjunto3, null);
            }
        });

        zoom1 = this.findViewById(R.id.imageView_zoom_1_admin);
        zoom2 = this.findViewById(R.id.imageView_zoom_2_admin);
        zoom3 = this.findViewById(R.id.imageView_zoom_3_admin);

        fecha_respuesta = this.findViewById(R.id.textView_fecha_respuesta_veterinario_consulta_individual_admin);
        hora_respuesta = this.findViewById(R.id.textView_hora_respuesta_veterinario_consulta_individual_admin);
        mensaje_respuesta = this.findViewById(R.id.textView_respuesta_veterinario_consulta_individual_admin);

        respuesta = this.findViewById(R.id.editText_respuesta_consulta_admin);
        enviar_respuesta = this.findViewById(R.id.button_enviar_respuesta_consulta);

        // Hacer visible el EditText para ingresar la respuesta
        responder = this.findViewById(R.id.button_responder_consulta);
        responder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responder.setVisibility(View.GONE);
                respuesta.setVisibility(View.VISIBLE);
                enviar_respuesta.setVisibility(View.VISIBLE);
            }
        });

        // Responder a la consulta y enviar los datos a la BD
        enviar_respuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(respuesta.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Escriba una respuesta", Toast.LENGTH_SHORT).show();
                }else{
                    final Map<String, Object> dataRespuestaConsulta = new HashMap<>();
                    // Fecha del momento actual
                    Calendar c1 = Calendar.getInstance();
                    int mYear = c1.get(Calendar.YEAR);
                    int mMonth = c1.get(Calendar.MONTH);
                    int mDay = c1.get(Calendar.DAY_OF_MONTH);
                    dataRespuestaConsulta.put("fecha", mDay + "/" + (mMonth + 1) + "/" + mYear);
                    // Hora del momento actual
                    Calendar c2 = Calendar.getInstance();
                    int mHour = c2.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c2.get(Calendar.MINUTE);
                    dataRespuestaConsulta.put("hora", String.format("%02d:%02d", mHour, mMinute));
                    dataRespuestaConsulta.put("mensaje", respuesta.getText().toString());
                    // Insertar respuesta en la consulta
                    getIdClinicaFromFirebase(new idCallback() {
                        @Override
                        public void onCallback(String id_clinica) {
                            mDatabase.child("consultas").child(id_clinica).child(id_consulta).child("respuesta").setValue(dataRespuestaConsulta);
                            mDatabase.child("consultas").child(id_clinica).child(id_consulta).child("respondido").setValue("Si");
                            Intent intent = new Intent(getApplicationContext(), pantalla_consultas_individual_admin.class);
                            intent.putExtra("num_adjuntos", aux_num_adjuntos);
                            intent.putExtra("id", id_consulta);
                            intent.putExtra("nombre", aux_nombre);
                            intent.putExtra("fecha", aux_fecha);
                            intent.putExtra("respuesta", "respondido");
                            intent.putExtra("hora", aux_hora);
                            intent.putExtra("asunto", aux_asunto);
                            intent.putExtra("mensaje", aux_mensaje);
                            intent.putExtra("imagen", aux_imagen);
                            intent.putExtra("estado", aux_estado);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        // Accion de cerrar consulta, la consulta pasa de estado a "Cerrada" y abre la pantalla de consultas cerradas
        cerrar_consulta = this.findViewById(R.id.button_cerrar_consulta_admin);
        cerrar_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Modificar consulta, pasar de estado "Abierta" a "Cerrada"
                getIdClinicaFromFirebase(new idCallback() {
                    @Override
                    public void onCallback(String id_clinica) {
                        mDatabase.child("consultas").child(id_clinica).child(id_consulta).child("estado").setValue("Cerrada");
                        Intent intent = new Intent(getApplicationContext(), pantalla_consultas_cerradas_admin.class);
                        Toast.makeText(getApplicationContext(), "Consulta cerrada con éxito", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(String id);
    }

    // Obtener el id de la clinica
    public void getIdClinicaFromFirebase(final idCallback myCallback) {
        // Referencia a los datos del usuario en la BD
        mDatabase.child("usuarios").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String id_clinica = data.get("clinica").toString();
                myCallback.onCallback(id_clinica);
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
        // Mostrar la respuesta del veterinario solo en caso de haber respondido a la consulta
        getIdClinicaFromFirebase(new idCallback() {
            @Override
            public void onCallback(String id_clinica) {
                // Consulta respondida o no por el veterinario
                aux_respuesta = getIntent().getExtras().getString("respuesta");
                // Si está respondida, cargar los datos desde la BD y mostrar el cardview de la respuesta del veterinario
                if(aux_respuesta.equalsIgnoreCase("Respondido")){
                    String id_consulta = getIntent().getExtras().getString("id");
                    // Referencia a los datos de la respuesta del veterinario a la consulta
                    DatabaseReference ref_data_respuesta = mDatabase.child("consultas").child(id_clinica).child(id_consulta).child("respuesta");
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
                            Log.d("ERROR1", "onCancelled: "+databaseError);
                        }
                    });
                }else {
                    cerrar_consulta.setVisibility(View.INVISIBLE);
                    responder.setVisibility(View.VISIBLE);
                }
            }
        });
        final String aux_estado = getIntent().getExtras().getString("estado");
        final String id_consulta = getIntent().getExtras().getString("id");
        // Cambiar el titulo de la toolbar y el color de ambos cardview segun el estado de la consulta
        if(aux_estado.equalsIgnoreCase("Cerrada")){
            getSupportActionBar().setTitle("Consulta Cerrada #"+id_consulta);
            cardView_usuario.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorNegative)));
            cardView_veterinario.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorNegative)));
            cerrar_consulta.setVisibility(View.INVISIBLE);
        }else if(aux_estado.equalsIgnoreCase("Abierta")){
            getSupportActionBar().setTitle("Consulta Abierta #"+id_consulta);
            cerrar_consulta.setVisibility(View.VISIBLE);
        }
        // Numero de imagenes adjuntas
        String aux_num_adjuntos = getIntent().getExtras().getString("num_adjuntos");
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
