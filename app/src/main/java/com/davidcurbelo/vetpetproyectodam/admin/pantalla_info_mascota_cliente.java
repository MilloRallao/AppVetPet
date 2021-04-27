package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Calendar;
import java.util.Map;

public class pantalla_info_mascota_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imagen;
    private TextView nombre_mascota;
    private Button cambiarImagen;
    private TextView propietario;
    private EditText especie;
    private EditText raza;
    private TextView sexo;
    private RadioGroup sexos;
    private EditText fecha_nacimiento;
    private TextView edad;
    private EditText peso;
    private TextView numVacunas;
    private TextView numDiagnosticos;
    private Button editar;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private final int MY_PERMISSIONS_REQUEST = 0;
    private int SELECT_IMG = 0;
    private int REQUEST_CAMERA = 1;
    private Uri uri_imagen;
    private Boolean imagen_cargada = false;

    private String id_mascota;
    private String id_cliente;
    private String sexo_elegido;
    private int num_mascotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_info_mascota_cliente);

        toolbar = this.findViewById(R.id.toolbar_informacion_mascota_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_ver_mascotas_cliente.class);
                String id_cliente = getIntent().getExtras().getString("id_cliente");
                intent.putExtra("id_cliente", id_cliente);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
        }

        id_mascota = getIntent().getExtras().getString("id_mascota");
        id_cliente = getIntent().getExtras().getString("id_cliente");

        imagen = this.findViewById(R.id.imageView_imagen_mascota_informacion_mascota_cliente);
        nombre_mascota = this.findViewById(R.id.textView_nombre_mascota_informacion_mascota_cliente);

        // Accion al pulsar el botón para cambiar la imagen de la mascota
        cambiarImagen = this.findViewById(R.id.button_cambiar_imagen_mascota_informacion_mascota_cliente);
        cambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        propietario = this.findViewById(R.id.textView_propietario_mascota_informacion_mascota_cliente);
        especie = this.findViewById(R.id.editText_especie_mascota_informacion_mascota_cliente);
        raza = this.findViewById(R.id.editText_raza_mascota_informacion_mascota_cliente);
        sexo = this.findViewById(R.id.textView_sexo_mascota_informacion_mascota_cliente);
        sexos = this.findViewById(R.id.radioGroup_sexo_mascota_informacion_mascota_cliente);

        fecha_nacimiento = this.findViewById(R.id.editText_fecha_nacimiento_mascota_informacion_mascota_cliente);
        fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fecha_nacimiento);
            }
        });

        edad = this.findViewById(R.id.textView_edad_mascota_informacion_mascota_cliente);
        peso = this.findViewById(R.id.editText_peso_mascota_informacion_mascota_cliente);
        numVacunas = this.findViewById(R.id.textView_numero_vacunas_mascota_informacion_mascota_cliente);
        numDiagnosticos = this.findViewById(R.id.textView_numero_diagnosticos_mascota_informacion_mascota_cliente);

        // Acción al pulsar el botón de editar, habilita todos los Editext
        editar = this.findViewById(R.id.button_editar_información_mascota_informacion_mascota_cliente);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editar.getText().toString().contains("Editar")){
                    editar.setText("Actualizar información");
                    especie.setEnabled(true);
                    raza.setEnabled(true);
                    sexo.setVisibility(View.INVISIBLE);
                    sexos.setVisibility(View.VISIBLE);
                    if(sexo.getText().toString().equalsIgnoreCase("macho")){
                        sexos.check(R.id.radioButton_macho);
                    }else{
                        sexos.check(R.id.radioButton_hembra);
                    }
                    fecha_nacimiento.setEnabled(true);
                    peso.setEnabled(true);
                    cambiarImagen.setVisibility(View.VISIBLE);
                }else if(editar.getText().toString().contains("Actualizar")){
                    final DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
                    ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ref_mascota.child("especie").setValue(especie.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_mascota.child("raza").setValue(raza.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_mascota.child("sexo").setValue(sexo_elegido);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_mascota.child("fecha_nacimiento").setValue(fecha_nacimiento.getText().toString());
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    ref_mascota.child("peso").setValue(peso.getText().toString() + " Kg");
                                    if(imagen_cargada){
                                        // Referencia a Firebase Storage donde se guardará la imagen de la mascota
                                        final StorageReference imagenMascotasRef = storageReference.child("mascotas/"+id_cliente+"/"+num_mascotas+"."+getExtension(uri_imagen));
                                        // Insertar las imágenes en Firebase Storage (putFile()) y crear la consulta en la BD
                                        imagenMascotasRef.putFile(uri_imagen).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                // Devolver el enlace de descarga de la imagen de la mascota
                                                return imagenMascotasRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    ref_mascota.child("foto").setValue(downloadUri.toString());
                                                    Toast.makeText(getApplicationContext(), "¡Datos actualizados!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(), pantalla_info_mascota_cliente.class);
                                                    intent.putExtra("id_cliente", id_cliente);
                                                    intent.putExtra("id_mascota", id_mascota);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "¡Datos actualizados!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), pantalla_info_mascota_cliente.class);
                                        intent.putExtra("id_cliente", id_cliente);
                                        intent.putExtra("id_mascota", id_mascota);
                                        startActivity(intent);
                                    }

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

        sexos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton_macho){
                    sexo_elegido = "Macho";
                }else if(checkedId == R.id.radioButton_hembra){
                    sexo_elegido = "Hembra";
                }
            }
        });

        rellenarDatos();
    }

    // Rellenar los campos con los datos de la mascota
    public void rellenarDatos(){
        // Referencia a los datos del cliente
        final DatabaseReference ref_data_user = mDatabase.child("usuarios").child(id_cliente);
        ref_data_user.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String nombre = data.get("nombre").toString();
                String apellidos = data.get("apellidos").toString();
                String nombre_completo = nombre + " " + apellidos;
                propietario.setText(nombre_completo);
                num_mascotas = (int) dataSnapshot.child("mascotas").getChildrenCount();
                // Referencia a los datos de la mascota
                DatabaseReference ref_mascota = ref_data_user.child("mascotas").child(id_mascota);
                ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        getSupportActionBar().setTitle("Información de " + data.get("nombre").toString());
                        nombre_mascota.setText(data.get("nombre").toString());
                        Glide.with(getApplicationContext()).load(data.get("foto")).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imagen);
                        especie.setText(data.get("especie").toString());
                        raza.setText(data.get("raza").toString());
                        sexo.setText(data.get("sexo").toString());
                        fecha_nacimiento.setText(data.get("fecha_nacimiento").toString());
                        edad.setText(data.get("edad").toString());
                        String[] peso_mascota = data.get("peso").toString().split(" ");
                        peso.setText(peso_mascota[0]);
                        final String num_vacunas = String.valueOf(dataSnapshot.child("vacunas").getChildrenCount());
                        getNumeroVacunasEspecie(new idCallback() {
                            @Override
                            public void onCallback(int num_vacunas_especie) {
                                numVacunas.setText(num_vacunas + "/" + num_vacunas_especie);
                                String num_diagnosticos = String.valueOf(dataSnapshot.child("diagnosticos").getChildrenCount());
                                numDiagnosticos.setText(num_diagnosticos);
                            }
                        }, data.get("especie").toString());
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

    // Mostrar el Dialog para escoger la fecha de nacimiento de la mascota
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
                Log.d("ERROR3", "onCancelled: "+databaseError);
            }
        });
    }

    // Dialog de opciones para seleccionar el origen de la imagen de la mascota
    private void selectImage(){
        // Opciones del Dialog
        final CharSequence[] items={"Sacar foto (Abrir cámara)","Abrir Galería", "Cancelar"};
        // Crear el Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(pantalla_info_mascota_cliente.this);
        // Título del Dialog
        builder.setTitle("Selecciona Imagen de la mascota");
        // Establecer cada opcion del Dialog
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Opcion de sacar foto con la camara
                if (items[i].equals("Sacar foto (Abrir cámara)")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    // Opcion de adjuntar varias imagenes desde galeria
                } else if(items[i].equals("Abrir Galería")){
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

    // Resultado de elegir una opcion del Dialog de opciones para elegir una imagen
    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== RESULT_OK){
            // Opcion de sacar foto
            if(requestCode==REQUEST_CAMERA){
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imagen.setImageBitmap(bmp);
                // Agregar URI de la imagen a la lista de imagenes adjuntas
                uri_imagen = Uri.fromFile(new File(getRealPathFromURI(getImageUri(getApplicationContext(), bmp))));
                // Control de imagen cargada
                imagen_cargada = true;
            }else if(requestCode == SELECT_IMG){ // Opcion de una imagen desde galeria
                if(data.getData() != null){
                    imagen.setImageURI(data.getData());
                    uri_imagen = data.getData();
                    imagen_cargada = true;
                }
            }
        }
    }
}
