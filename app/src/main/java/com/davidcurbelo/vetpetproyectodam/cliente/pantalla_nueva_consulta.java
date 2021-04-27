package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pantalla_nueva_consulta extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinner;
    private String nombre_mascota_seleccionada = "";
    private Long id_mascota_seleccionada;
    private String id_clinica;
    private String nombre_usuario;

    private final int MY_PERMISSIONS_REQUEST = 0;
    private int SELECT_IMG = 0;
    private int REQUEST_CAMERA = 1;
    private int SELECT_MULTIPLE_IMG = 2;

    private Drawable drawable;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private Boolean hueco1;
    private Boolean hueco2;
    private Boolean hueco3;
    private int contador;
    private Button seleccionar_adjunto;
    private Button enviar;
    private EditText asunto;
    private EditText mensaje;

    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseUser user;
    private List<Uri> uriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_nueva_consulta_cliente);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = this.findViewById(R.id.toolbar_nueva_consulta);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = this.findViewById(R.id.spinner);
        enviar = this.findViewById(R.id.button_enviar_consulta);
        asunto = this.findViewById(R.id.editText_asunto);
        mensaje = this.findViewById(R.id.editText_mensaje);
        seleccionar_adjunto = this.findViewById(R.id.button_seleccionar_adjuntos);
        imageView1 = this.findViewById(R.id.imageView1);
        imageView2 = this.findViewById(R.id.imageView2);
        imageView3 = this.findViewById(R.id.imageView3);
        imageButton1 = this.findViewById(R.id.imageButton1);
        imageButton2 = this.findViewById(R.id.imageButton2);
        imageButton3 = this.findViewById(R.id.imageButton3);

        drawable = getResources().getDrawable(R.drawable.icono_imagen_previa);
        hueco1 = false;
        hueco2 = false;
        hueco3 = false;
        contador = 0;
        uriList = new ArrayList<>();

        // PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
        }

        cargarMascotas();

        // Enviar la consulta y guardar datos e imágenes en BD y Storage respectivamente
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String asunto_text = asunto.getText().toString().trim();
                String mensaje_text = mensaje.getText().toString().trim();
                // Control de campos vacíos
                if(asunto_text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Escriba un asunto", Toast.LENGTH_SHORT).show();
                }else if( mensaje_text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Escriba un mensaje", Toast.LENGTH_SHORT).show();
                }else if(nombre_mascota_seleccionada.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Elija una mascota para la consulta", Toast.LENGTH_SHORT).show();
                }else{
                    // Referencia al nodo de consultas
                    final DatabaseReference ref_consultas = mDatabase.child("consultas").child(id_clinica);

                    // Mapa de datos de la consulta que serán insertados en la BD
                    final HashMap<String, Object> dataConsulta = new HashMap<>();
                    dataConsulta.put("asunto", asunto_text);
                    dataConsulta.put("estado", "Abierta");

                    // Fecha del momento actual
                    Calendar c1 = Calendar.getInstance();
                    int mYear = c1.get(Calendar.YEAR);
                    int mMonth = c1.get(Calendar.MONTH);
                    int mDay = c1.get(Calendar.DAY_OF_MONTH);
                    dataConsulta.put("fecha", mDay + "/" + (mMonth + 1) + "/" + mYear);

                    // Hora del momento actual
                    Calendar c2 = Calendar.getInstance();
                    int mHour = c2.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c2.get(Calendar.MINUTE);
                    dataConsulta.put("hora", String.format("%02d:%02d", mHour, mMinute));

                    dataConsulta.put("id_mascota", id_mascota_seleccionada);
                    dataConsulta.put("id_usuario", user.getUid());
                    dataConsulta.put("mascota", nombre_mascota_seleccionada);
                    dataConsulta.put("mensaje", mensaje_text);
                    dataConsulta.put("nombre_cliente", nombre_usuario);
                    dataConsulta.put("respondido", "No");
                    dataConsulta.put("numero_adjuntos", contador);

                    // Lista de enlaces de descarga de las imágenes adjuntas
                    final List<String> listaDownloadUri = new ArrayList<>();

                    // Contador auxiliar para el bucle for
                    final int[] finalI = {0};

                    ref_consultas.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Control de la primera vez que se inserten consultas en la clinica del usuario
                            if(dataSnapshot.getChildrenCount() == 0){
                                // Insertar datos de consulta y subir las imagenes adjuntas a Firebase Storage
                                for(int i = 0; i < contador; i++){
                                    // Referencia a Firebase Storage donde se guardaran las imágenes adjuntas
                                    final StorageReference adjuntosRef = storageReference.child("adjuntos_consultas/"+1+"/"+i+"."+getExtension(uriList.get(i)));

                                    // Insertar las imágenes en Firebase Storage (putFile()) y crear la consulta en la BD
                                    adjuntosRef.putFile(uriList.get(i)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            // Devolver el enlace de descarga de la imagen adjunta
                                            return adjuntosRef.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri downloadUri = task.getResult();
                                                // Agregar el enlace de descarga de la imagen adjunta a la lista de enlaces de descarga
                                                listaDownloadUri.add(finalI[0], downloadUri.toString());
                                                // Agregar una nueva entrada al mapa de datos con el enlace de la lista de enlaces de imágenes adjuntas
                                                dataConsulta.put("imagen_adjunta"+ finalI[0], listaDownloadUri.get(finalI[0]));
                                                finalI[0]++;
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Insertar todos los datos de la consulta en la BD pasando como valor el mapa de datos ya relleno con los enlaces de descarga de los adjuntos
                                            ref_consultas.child("1").setValue(dataConsulta);
                                        }
                                    });
                                }
                                // Referencia a las consultas del usuario
                                final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(user.getUid()).child("consultas");
                                ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Control de la primera vez que se inserten consultas en el usuario
                                        if(dataSnapshot.getChildrenCount() == 0){
                                            ref_data_user.child("id_consulta_"+1).setValue(1);
                                            // Control de si hay mas de una consulta ya insertada en el usuario
                                        }else{
                                            ref_data_user.child("id_consulta_"+(dataSnapshot.getChildrenCount()+1)).setValue(1);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("ERROR4", "onCancelled: "+databaseError);
                                    }
                                });
                            // Control de si hay mas de una consulta ya creada para la clinica del usuario
                            }else{
                                final Long id_consulta = dataSnapshot.getChildrenCount()+1;
                                // Control de numero de imagenes adjuntas para solo insertar imagenes en caso de que hayan
                                if(contador == 0){
                                    // Insertar todos los datos de la consulta en la BD pasando como valor el mapa de datos ya relleno con los enlaces de descarga de los adjuntos
                                    ref_consultas.child(String.valueOf(id_consulta)).setValue(dataConsulta);
                                }else{
                                    // Insertar datos de consulta en "consultas" y subir imagenes adjuntas a Firebase Storage
                                    for(int i = 0; i < contador; i++){
                                        // Referencia a Firebase Storage donde se guardaran las imágenes adjuntas
                                        final StorageReference adjuntosRef = storageReference.child("adjuntos_consultas/"+id_consulta+"/"+i+"."+getExtension(uriList.get(i)));
                                        // Insertar las imagenes en Firebase Storage y crear la consulta en la BD
                                        adjuntosRef.putFile(uriList.get(i)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                // Devolver el enlace de descarga de la imagen adjunta
                                                return adjuntosRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    // Agregar el enlace de descarga de la imagen adjunta a la lista de enlaces de descarga
                                                    listaDownloadUri.add(finalI[0], downloadUri.toString());
                                                    // Agregar una nueva entrada al mapa de datos con el enlace de la lista de enlaces de descarga de las imagenes adjuntas
                                                    dataConsulta.put("imagen_adjunta"+ finalI[0], listaDownloadUri.get(finalI[0]));
                                                    finalI[0]++;
                                                }
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                // Insertar todos los datos de la consulta en la BD pasando como valor el mapa de datos ya relleno con los enlaces de descarga de los adjuntos
                                                ref_consultas.child(String.valueOf(id_consulta)).setValue(dataConsulta);
                                            }
                                        });
                                    }
                                }

                                // Referencia a las consultas del usuario para insertar sus referencias
                                final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(user.getUid()).child("consultas");
                                ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Control de la primera vez que se inserten consultas en el usuario
                                        if(dataSnapshot.getChildrenCount() == 0){
                                            ref_data_user.child("id_consulta_"+1).setValue(id_consulta);
                                            // Control de si hay mas de una consulta ya insertada en el usuario
                                        }else{
                                            ref_data_user.child("id_consulta_"+(dataSnapshot.getChildrenCount()+1)).setValue(id_consulta);
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
                    // Mostrar Dialog de confirmacion de creacion exitosa de consulta
                    showConfirmDialog();
                }
            }
        });

        // Accion del boton de seleccionar imagenes adjuntas
        seleccionar_adjunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Control de maximo de imagenes
                if(contador == 3){
                    Toast.makeText(getApplicationContext(), "BORRA ALGUNA IMAGEN", Toast.LENGTH_SHORT).show();
                }else{
                    selectImage();
                }
            }
        });

        // Accion del boton para borrar 1 imagen adjunta
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hueco 1 pasa a estar disponible
                hueco1 = false;
                contador--;
                // Hacer invisible el boton de eliminar imagen adjunta
                imageButton1.setVisibility(View.GONE);
                // Asignar imagen por defecto de miniatura de imagen adjunta
                imageView1.setImageDrawable(drawable);
                imageView1.setColorFilter(Color.argb(255, 197, 189, 189));
            }
        });
        // Accion del boton para borrar 1 imagen adjunta
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hueco 2 pasa a estar disponible
                hueco2 = false;
                contador--;
                // Hacer invisible el boton de eliminar imagen adjunta
                imageButton2.setVisibility(View.GONE);
                // Asignar imagen por defecto de miniatura de imagen adjunta
                imageView2.setImageDrawable(drawable);
                imageView2.setColorFilter(Color.argb(255, 197, 189, 189));
            }
        });
        // Accion del boton para borrar 1 imagen adjunta
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hueco 3 pasa a estar disponible
                hueco3 = false;
                contador--;
                // Hacer invisible el boton de eliminar imagen adjunta
                imageButton3.setVisibility(View.GONE);
                // Asignar imagen por defecto de miniatura de imagen adjunta
                imageView3.setImageDrawable(drawable);
                imageView3.setColorFilter(Color.argb(255, 197, 189, 189));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Referencia al nodo de la BD del usuario actual
        final DatabaseReference ref_cliente = mDatabase.child("usuarios").child(user.getUid());
        ref_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                id_clinica = data.get("clinica").toString();
                nombre_usuario = data.get("nombre").toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR2", "onCancelled: "+databaseError);
            }
        });

    }

    // Cargar las mascotas del usuario dentro del Spinner
    private void cargarMascotas(){
        // Lista de los nombres de las mascotas
        final List<String> mascotas = new ArrayList<>();
        // Referencia al usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                spinner.setAdapter(arrayAdapter);
                // Establecer la opcion seleccionada del Spinner y mostrarla
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        nombre_mascota_seleccionada = parent.getItemAtPosition(position).toString();
                        id_mascota_seleccionada = id;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }

    // Cargar Dialog personalizado de confirmación de consulta
    private void showConfirmDialog() {
        Dialog dialog = new Dialog(this);
        // Asignar diseño de layout al Dialog
        dialog.setContentView(R.layout.confirm_dialog_consulta);
        // Referencias a los botones del Dialog
        Button volver = dialog.findViewById(R.id.button_volver_cita);
        Button ver_consulta = dialog.findViewById(R.id.button_ver_cita);
        // Accion del boton volver (Pasar a la activity correspondiente)
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consulta_online.class);
                startActivity(intent);
            }
        });
        // Accion del boton ver consulta (Pasar a la activity correspondiente)
        ver_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consultas_abiertas.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    // Dialog de opciones para seleccionar el origen de las imagenes adjuntas
    private void selectImage(){
        // Opciones del Dialog
        final CharSequence[] items={"Sacar foto (Abrir cámara)","Adjuntar 1 imagen","Adjuntar varias imágenes (Máximo 3)", "Cancelar"};
        // Crear el Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(pantalla_nueva_consulta.this);
        // Título del Dialog
        builder.setTitle("Adjuntar Imagen");
        // Establecer cada opcion del Dialog
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Opcion de sacar foto con la camara
                if (items[i].equals("Sacar foto (Abrir cámara)")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    // Opcion de adjuntar varias imagenes desde galeria
                } else if (items[i].equals("Adjuntar varias imágenes (Máximo 3)")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "Selecciona las imágenes (Máximo 3)"), SELECT_MULTIPLE_IMG);
                    // Opcion de adjuntar una unica imagen desde galeria
                } else if(items[i].equals("Adjuntar 1 imagen")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent, "Selecciona la imagen"), SELECT_IMG);
                    // Opcion de cancelar
                } else if (items[i].equals("Cancelar")) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Mostrar el Dialog
        builder.show();
    }

    // Devuelve la extension de la imagen a traves de su URI
    private String getExtension(Uri uriImage) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    // Devuelve el URI de una imagen de tipo Bitmap
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 480, 720,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    // Devuelve la ruta real de una imagen a traves de su URI
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    // Resultado de elegir una opcion del Dialog de opciones para adjuntar imagenes
    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== RESULT_OK){
            if(requestCode==REQUEST_CAMERA){ // Opcion de sacar foto
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                if (hueco1 == false){ // Establecer la imagen capturada en el hueco 1
                    imageView1.setImageBitmap(bmp);
                    // Agregar URI de la imagen a la lista de imagenes adjuntas
                    uriList.add(0, Uri.fromFile(new File(getRealPathFromURI(getImageUri(getApplicationContext(), bmp)))));
                    imageView1.setColorFilter(255);
                    // Hueco no disponible
                    hueco1 = true;
                    // Boton de eliminar imagen adjunta visible
                    imageButton1.setVisibility(View.VISIBLE);
                    ++contador;
                }else if (hueco2 == false){ // Establecer la imagen capturada en el hueco 2
                    imageView2.setImageBitmap(bmp);
                    uriList.add(1, getImageUri(getApplicationContext(), bmp));
                    imageView2.setColorFilter(255);
                    hueco2 = true;
                    imageButton2.setVisibility(View.VISIBLE);
                    ++contador;
                }else if(hueco3 == false){ // Establecer la imagen capturada en el hueco 3
                    imageView3.setImageBitmap(bmp);
                    uriList.add(2, getImageUri(getApplicationContext(), bmp));
                    imageView3.setColorFilter(255);
                    hueco3 = true;
                    imageButton3.setVisibility(View.VISIBLE);
                    ++contador;
                }
            }else if(requestCode==SELECT_MULTIPLE_IMG){ // Opcion de varias imagenes desde galeria
                ClipData clipData = data.getClipData();
                if (clipData != null){
                    if(contador == 0){ // Control de todos los huecos disponibles
                        if(clipData.getItemCount() == 2){
                            imageView1.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(0, clipData.getItemAt(0).getUri());
                            imageView1.setColorFilter(255);
                            imageView2.setImageURI(clipData.getItemAt(1).getUri());
                            uriList.add(1, clipData.getItemAt(1).getUri());
                            imageView2.setColorFilter(255);
                            hueco1 = true;
                            hueco2 = true;
                            imageButton1.setVisibility(View.VISIBLE);
                            imageButton2.setVisibility(View.VISIBLE);
                            contador = 2;
                            // Control de agregar 3 imagenes
                        }else if(clipData.getItemCount() == 3){ // Control de agregar 2 imagenes
                            imageView1.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(0, clipData.getItemAt(0).getUri());
                            imageView1.setColorFilter(255);
                            imageView2.setImageURI(clipData.getItemAt(1).getUri());
                            uriList.add(1, clipData.getItemAt(1).getUri());
                            imageView2.setColorFilter(255);
                            imageView3.setImageURI(clipData.getItemAt(2).getUri());
                            uriList.add(2, clipData.getItemAt(2).getUri());
                            imageView3.setColorFilter(255);
                            hueco1 = true;
                            hueco2 = true;
                            hueco3 = true;
                            imageButton1.setVisibility(View.VISIBLE);
                            imageButton2.setVisibility(View.VISIBLE);
                            imageButton3.setVisibility(View.VISIBLE);
                            contador = 3;
                        }
                    }else if(contador == 1){ // Control de 2 huecos libre
                        if (hueco1 == false && hueco2 == false){ // Hueco 1 y 2 libre
                            imageView1.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(0, clipData.getItemAt(0).getUri());
                            imageView1.setColorFilter(255);
                            imageView2.setImageURI(clipData.getItemAt(1).getUri());
                            uriList.add(1, clipData.getItemAt(1).getUri());
                            imageView2.setColorFilter(255);
                            hueco1 = true;
                            hueco2= true;
                            imageButton1.setVisibility(View.VISIBLE);
                            imageButton2.setVisibility(View.VISIBLE);
                            contador = 3;
                        }else if (hueco2 == false && hueco3 == false){ // Hueco 2 y 3 libre
                            imageView2.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(1, clipData.getItemAt(1).getUri());
                            imageView2.setColorFilter(255);
                            imageView3.setImageURI(clipData.getItemAt(1).getUri());
                            uriList.add(2, clipData.getItemAt(1).getUri());
                            imageView3.setColorFilter(255);
                            hueco2 = true;
                            hueco3= true;
                            imageButton2.setVisibility(View.VISIBLE);
                            imageButton3.setVisibility(View.VISIBLE);
                            contador = 3;
                        }else if(hueco1 == false && hueco3 == false){ // Hueco 1 y 3 libre
                            imageView1.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(0, clipData.getItemAt(0).getUri());
                            imageView1.setColorFilter(255);
                            imageView3.setImageURI(clipData.getItemAt(1).getUri());
                            uriList.add(2, clipData.getItemAt(1).getUri());
                            imageView3.setColorFilter(255);
                            hueco1 = true;
                            hueco3= true;
                            imageButton1.setVisibility(View.VISIBLE);
                            imageButton3.setVisibility(View.VISIBLE);
                            contador = 3;
                        }
                    }else{ // Control de 1 hueco libre
                        if (hueco1 == false){ // Hueco 1 libre
                            imageView1.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(0, clipData.getItemAt(0).getUri());
                            imageView1.setColorFilter(255);
                            hueco1 = true;
                            imageButton1.setVisibility(View.VISIBLE);
                            contador = 3;
                        }else if (hueco2 == false){ // Hueco 2 libre
                            imageView2.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(1, clipData.getItemAt(0).getUri());
                            imageView2.setColorFilter(255);
                            hueco2= true;
                            imageButton2.setVisibility(View.VISIBLE);
                            contador = 3;
                        }else if(hueco3 == false){ // Hueco 3 libre
                            imageView3.setImageURI(clipData.getItemAt(0).getUri());
                            uriList.add(2, clipData.getItemAt(0).getUri());
                            imageView3.setColorFilter(255);
                            hueco3 = true;
                            imageButton3.setVisibility(View.VISIBLE);
                            contador = 3;
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Por favor, elija más de 1 imagen. Para elegir sólo 1 imagen, elija la opción 'Adjuntar 1 imagen'", Toast.LENGTH_LONG).show();
                }
            }else if(requestCode == SELECT_IMG){ // Opcion de una unica imagen desde galeria
                if(data.getData() != null){
                    // Hueco 1 libre
                    if (hueco1 == false){
                        imageView1.setImageURI(data.getData());
                        imageView1.setColorFilter(255);
                        uriList.add(0, data.getData());
                        hueco1 = true;
                        imageButton1.setVisibility(View.VISIBLE);
                        ++contador;
                        // Hueco 2 libre
                    }else if (hueco2 == false){
                        imageView2.setImageURI(data.getData());
                        imageView2.setColorFilter(255);
                        uriList.add(1, data.getData());
                        hueco2 = true;
                        imageButton2.setVisibility(View.VISIBLE);
                        ++contador;
                        // Hueco 3 libre
                    }else if(hueco3 == false){
                        imageView3.setImageURI(data.getData());
                        imageView3.setColorFilter(255);
                        uriList.add(2, data.getData());
                        hueco3 = true;
                        imageButton3.setVisibility(View.VISIBLE);
                        ++contador;
                    }
                }
            }
        }
    }
}
