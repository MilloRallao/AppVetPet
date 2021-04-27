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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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

public class pantalla_agregar_mascota extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText nombre;
    private Spinner sexo;
    private Spinner especie;
    private EditText raza;
    private EditText edad;
    private Spinner edad_tiempo;
    private EditText fecha_nacimiento;
    private EditText peso;
    private FloatingActionButton agregar_mascota;
    private Button seleccionar_imagen;
    private ImageView imagen_mascota;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private final int MY_PERMISSIONS_REQUEST = 0;
    private int SELECT_IMG = 0;
    private int REQUEST_CAMERA = 1;

    private String id_cliente;
    private String sexo_seleccionado;
    private String especie_seleccionada;
    private String tiempo_seleccionado;
    private Uri uri_imagen;
    private Boolean imagen_cargada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_agregar_mascota);

        toolbar = this.findViewById(R.id.toolbar_agregar_mascota);
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // PERMISOS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
        }

        nombre = this.findViewById(R.id.editText_nombre_mascota_agregar_mascota);
        sexo = this.findViewById(R.id.spinner_sexo_mascota_agregar_mascota);
        especie = this.findViewById(R.id.spinner_especie_mascota_agregar_mascota);
        raza = this.findViewById(R.id.editText_raza_mascota_agregar_mascota);
        edad = this.findViewById(R.id.editText_edad_mascota_agregar_mascota);
        edad_tiempo = this.findViewById(R.id.spinner_edad_mascota_agregar_mascota);
        peso = this.findViewById(R.id.editText_peso_mascota_agregar_mascota);
        imagen_mascota = this.findViewById(R.id.imageView_foto_mascota);

        seleccionar_imagen = this.findViewById(R.id.button_elegir_foto_mascota);
        seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        fecha_nacimiento = this.findViewById(R.id.editText_fecha_nacimiento_mascota_agregar_mascota);
        fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        agregar_mascota = this.findViewById(R.id.floatingActionButton_agregar_mascota);
        agregar_mascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Control de campos vacíos
                if (nombre.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ponga un nombre a la mascota", Toast.LENGTH_SHORT).show();
                }else if(sexo_seleccionado.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga el sexo de la mascota", Toast.LENGTH_SHORT).show();
                }else if(especie_seleccionada.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga la especie de la mascota", Toast.LENGTH_SHORT).show();
                }else if(raza.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga la raza de la mascota", Toast.LENGTH_SHORT).show();
                }else if(edad.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga la edad de la mascota", Toast.LENGTH_SHORT).show();
                }else if(tiempo_seleccionado.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga el tiempo de la edad de la mascota", Toast.LENGTH_SHORT).show();
                }else if(fecha_nacimiento.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga la fecha de nacimiento de la mascota", Toast.LENGTH_SHORT).show();
                }else if(peso.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Ponga el peso de la mascota", Toast.LENGTH_SHORT).show();
                }else if(!imagen_cargada){
                    Toast.makeText(getApplicationContext(), "Elija una imagen para la mascota", Toast.LENGTH_SHORT).show();
                }else{
                    // Mapa de datos de la mascota que serán insertados en la BD
                    final HashMap<String, Object> dataMascota = new HashMap<>();
                    dataMascota.put("nombre", nombre.getText().toString());
                    dataMascota.put("sexo", sexo_seleccionado);
                    dataMascota.put("especie", especie_seleccionada);
                    dataMascota.put("raza", raza.getText().toString());
                    dataMascota.put("edad", edad.getText().toString().concat(" "+tiempo_seleccionado));
                    dataMascota.put("fecha_nacimiento", fecha_nacimiento.getText().toString());
                    dataMascota.put("peso", peso.getText().toString().concat(" kg"));
                    // Referencia al cliente al que se le insertará su mascota
                    final DatabaseReference ref_usuario = mDatabase.child("usuarios").child(id_cliente).child("mascotas");
                    ref_usuario.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final int num_mascotas = (int) dataSnapshot.getChildrenCount();
                            // Control de la primera vez que se insertan mascotas en el cliente
                            if(num_mascotas == 0){
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
                                            dataMascota.put("foto", downloadUri.toString());
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Insertar todos los datos de la mascota en la BD pasando como valor el mapa de datos ya relleno con el enlace de descarga de la imagen de la mascota
                                        ref_usuario.child("0").setValue(dataMascota);
                                        Toast.makeText(getApplicationContext(), "¡Mascota agregada con éxito!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }else{ // Control de si hay mas de una mascota ya insertada en el cliente
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
                                            dataMascota.put("foto", downloadUri.toString());
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Insertar todos los datos de la mascota en la BD pasando como valor el mapa de datos ya relleno con el enlace de descarga de la imagen de la mascota
                                        ref_usuario.child(""+(num_mascotas)).setValue(dataMascota);
                                        Toast.makeText(getApplicationContext(), "¡Mascota agregada con éxito!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR1", "onCancelled: "+databaseError);
                        }
                    });
                }
            }
        });

        cargarEspecies();
        cargarSexos();
        cargarTiempos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        id_cliente = getIntent().getExtras().getString("id_cliente");
        DatabaseReference ref_cliente = mDatabase.child("usuarios").child(id_cliente);
        ref_cliente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Acceder a los datos de un cliente
                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String nombre_cliente = data.get("nombre").toString();
                String apellidos_cliente = data.get("apellidos").toString();
                // Poner título de la toolbar
                getSupportActionBar().setTitle("Agregar Mascota a  " + nombre_cliente + " " + apellidos_cliente);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }

    // Dialog de opciones para seleccionar el origen de la imagen de la mascota
    private void selectImage(){
        // Opciones del Dialog
        final CharSequence[] items={"Sacar foto (Abrir cámara)","Abrir Galería", "Cancelar"};
        // Crear el Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(pantalla_agregar_mascota.this);
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
                imagen_mascota.setImageBitmap(bmp);
                // Agregar URI de la imagen a la lista de imagenes adjuntas
                uri_imagen = Uri.fromFile(new File(getRealPathFromURI(getImageUri(getApplicationContext(), bmp))));
                imagen_mascota.setColorFilter(255);
                // Control de imagen cargada
                imagen_cargada = true;
            }else if(requestCode == SELECT_IMG){ // Opcion de una imagen desde galeria
                if(data.getData() != null){
                    imagen_mascota.setImageURI(data.getData());
                    imagen_mascota.setColorFilter(255);
                    uri_imagen = data.getData();
                    imagen_cargada = true;
                }
            }
        }
    }

    // Cargar los sexos dentro del Spinner
    private void cargarSexos(){
        List<String> sexos = new ArrayList<>();
        sexos.add(0, "Macho");
        sexos.add(1, "Hembra");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sexos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sexo.setAdapter(adapter);
        sexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sexo_seleccionado = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Cargar las especies dentro del Spinner
    private void cargarEspecies(){
        List<String> especies = new ArrayList<>();
        especies.add(0, "Perro");
        especies.add(1, "Gato");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, especies);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        especie.setAdapter(adapter);
        especie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                especie_seleccionada = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Cargar los tiempos dentro del Spinner
    private void cargarTiempos(){
        List<String> tiempos = new ArrayList<>();
        tiempos.add(0, "Días");
        tiempos.add(1, "Semanas");
        tiempos.add(2, "Meses");
        tiempos.add(3, "Años");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, tiempos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        edad_tiempo.setAdapter(adapter);
        edad_tiempo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tiempo_seleccionado = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Mostrar el Dialog para escoger la fecha de nacimiento de la mascota
    private void showDatePickerDialog() {
        // Fecha del momento actual
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        //Crear DatePicker Dialog que se mostrará
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fecha_nacimiento.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
