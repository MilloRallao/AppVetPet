package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class pantalla_vacunas_mascota_cliente extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;
    private Button agregar_vacuna;

    private List<List<String>> vacunas;
    private List<String> vacuna;
    private RecyclerView rvVacunas;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String id_mascota;
    private String id_cliente;
    private String especie_mascota;
    private List<String> nombres_vacunas;
    private List<List<String>> dosis_vacunas;
    private List<String> dosis_vacuna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_vacunas_mascota_cliente);

        constraintLayout = this.findViewById(R.id.layout_vacunas_mascota_cliente);

        toolbar = this.findViewById(R.id.toolbar_vacunas_mascota_cliente);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Ir hacia atras con el boton de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), pantalla_ver_mascotas_cliente.class);
                i.putExtra("id_cliente", id_cliente);
                startActivity(i);
            }
        });

        id_mascota = getIntent().getExtras().getString("id_mascota");
        id_cliente = getIntent().getExtras().getString("id_cliente");

        // Accion del botón "Agregar Vacuna" que abre un Dialog
        agregar_vacuna = this.findViewById(R.id.button_agregar_vacuna_mascota_cliente);
        agregar_vacuna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Dialog
                Dialog dialog = new Dialog(pantalla_vacunas_mascota_cliente.this);
                // Asignar diseño de layout al Dialog
                dialog.setContentView(R.layout.agregar_vacuna_mascota);
                // Establecer Spinner de vacunas
                final Spinner spinner_vacunas = dialog.findViewById(R.id.spinner_vacunas_agregar_vacuna);
                // Crear el Adapter para rellenar de datos el Spinner de vacunas con los nombres de las vacunas disponibles para esa mascota
                final ArrayAdapter[] arrayAdapter = {new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, nombres_vacunas)};
                arrayAdapter[0].setDropDownViewResource(R.layout.spinner_dropdown_item);
                // Asignar el Adapter al Spinner de vacunas
                spinner_vacunas.setAdapter(arrayAdapter[0]);
                // Establecer Spinner de dosis
                final Spinner spinner_dosis = dialog.findViewById(R.id.spinner_dosis_agregar_vacuna);

                final String[] vacuna_spinner = new String[1];
                final int[] id_vacuna_spinner = new int[1];
                final int[] id_dosis_spinner = new int[1];

                // Establecer la vacuna seleccionada del Spinner y mostrar las dosis de esa vacuna en el siguiente Spinner dinámicamente
                spinner_vacunas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        vacuna_spinner[0] = parent.getItemAtPosition(position).toString();
                        id_vacuna_spinner[0] = (int) id;
                        // Crear el Adapter para rellenar de datos el Spinner de dosis con las dosis de la vacuna elegida en el Spinner de vacunas
                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, dosis_vacunas.get(id_vacuna_spinner[0]));
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        // Asignar el Adapter al Spinner de dosis
                        spinner_dosis.setAdapter(arrayAdapter);
                        spinner_dosis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                id_dosis_spinner[0] = (int) id;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                Button agregar = dialog.findViewById(R.id.button_agregar_vacuna_dialog);
                // Accion del boton "Agregar vacuna" del Dialog para insertar la nueva vacuna con la dosis elegida en la mascota
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getIdVacunaEspecie(new idCallback3() {
                            @Override
                            public void onCallback(final String id_vacuna_especie) {
                                String numero_dosis_spinner = "";
                                switch (id_dosis_spinner[0]){
                                    case 0:
                                        numero_dosis_spinner = "dosis_1";
                                        break;
                                    case 1:
                                        numero_dosis_spinner = "dosis_2";
                                        break;
                                    case 2:
                                        numero_dosis_spinner = "dosis_3";
                                        break;
                                    case 3:
                                        numero_dosis_spinner = "dosis_4";
                                        break;
                                }
                                // Fecha del momento actual
                                Calendar c = Calendar.getInstance();
                                int mYear = c.get(Calendar.YEAR);
                                int mMonth = c.get(Calendar.MONTH);
                                int mDay = c.get(Calendar.DAY_OF_MONTH);
                                final String fecha_actual = mDay + "/" + mMonth + "/" + mYear;
                                // Datos de la dosis
                                final Map<String, String> dataDosis = new HashMap<>();
                                dataDosis.put("nombre", vacuna_spinner[0]);
                                dataDosis.put("fecha", fecha_actual);
                                final String finalNumero_dosis_spinner = numero_dosis_spinner;
                                getNumDosisVacunaMascota(new idCallback4() {
                                    @Override
                                    public void onCallback(int num_dosis_vacuna_mascota) {
                                        // Cuando el número de dosis de la vacuna elegida en el Spinner sea distinta del número de dosis totales que tenga la mascota para esa vacuna, se insertarán las dosis anteriores
                                        if(num_dosis_vacuna_mascota != (id_dosis_spinner[0]+1)){
                                            for (int i = 1; i <= (id_dosis_spinner[0]+1); i++) {
                                                // Insertar vacuna y dosis en la mascota
                                                mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").child(id_vacuna_especie).child("dosis_"+i).setValue(dataDosis);
                                            }
                                            // Actualizar pantalla
                                            Intent i = new Intent(getApplicationContext(), pantalla_vacunas_mascota_cliente.class);
                                            i.putExtra("id_mascota", id_mascota);
                                            i.putExtra("id_cliente", id_cliente);
                                            startActivity(i);
                                        }else{ // En caso contrario, se inserta la dosis concreta
                                            // Insertar vacuna y dosis en la mascota
                                            mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").child(id_vacuna_especie).child(finalNumero_dosis_spinner).setValue(dataDosis);
                                            // Actualizar pantalla
                                            Intent i = new Intent(getApplicationContext(), pantalla_vacunas_mascota_cliente.class);
                                            i.putExtra("id_mascota", id_mascota);
                                            i.putExtra("id_cliente", id_cliente);
                                            startActivity(i);
                                        }
                                    }
                                }, id_cliente, id_mascota, String.valueOf(id_vacuna_especie));
                            }
                        }, especie_mascota, vacuna_spinner[0]);
                    }
                });
                dialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rvVacunas = this.findViewById(R.id.recyclerview_vacunas_mascota_cliente);
        rvVacunas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvVacunas.setLayoutManager(layoutManager);
        vacunas = new ArrayList<>();
        vacuna = new ArrayList<>();
        adapter = new MyAdapterVacunasMascotaCliente(vacunas, vacuna, getApplicationContext());
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback {
        void onCallback(int num_dosis_vacuna, String tipo_ultima_dosis);
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback2 {
        void onCallback(List<String> lista_ids_vacunas_mascota);
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback3 {
        void onCallback(String id_vacuna_especie);
    }

    // Interface para llamar de manera sincrona desde Firebase
    public interface idCallback4 {
        void onCallback(int num_dosis_vacuna_mascota);
    }

    // Obtener el numero de dosis necesarias de una vacuna
    public void getNumeroDosisVacuna(final idCallback myCallback, String id_vacuna, String especie) {
        String especie_final = "";
        if(especie.equalsIgnoreCase("perro")){
            especie_final = "perros";
        }else if(especie.equalsIgnoreCase("gato")){
            especie_final = "gatos";
        }
        mDatabase.child("vacunas").child(especie_final).child(id_vacuna).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num_dosis = (int) dataSnapshot.getChildrenCount() -1;
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Obtener el texto de la ultima dosis para saber que tipo de dosis es (Anual o no)
                String tipo_ultima_dosis = data.get("dosis_"+(num_dosis)).toString();
                myCallback.onCallback(num_dosis, tipo_ultima_dosis);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR0", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener los ID's de las vacunas de la especie de la mascota
    public void getIdsVacunasEspecie(final idCallback2 myCallback, String especie) {
        String especie_final = "";
        if(especie.equalsIgnoreCase("perro")){
            especie_final = "perros";
        }else if(especie.equalsIgnoreCase("gato")){
            especie_final = "gatos";
        }
        mDatabase.child("vacunas").child(especie_final).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> lista_ids_vacunas_especie = new ArrayList<>();
                int aux_contador = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista_ids_vacunas_especie.add(aux_contador, ds.getKey());
                    aux_contador++;
                }
                myCallback.onCallback(lista_ids_vacunas_especie);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR1", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener el ID de la vacuna de la especie de la mascota pasando como parámetro el nombre de la vacuna
    public void getIdVacunaEspecie(final idCallback3 myCallback, String especie, final String nombre_vacuna) {
        String especie_final = "";
        if(especie.equalsIgnoreCase("perro")){
            especie_final = "perros";
        }else if(especie.equalsIgnoreCase("gato")){
            especie_final = "gatos";
        }
        mDatabase.child("vacunas").child(especie_final).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) ds.getValue();
                    if(data.get("nombre").toString().equalsIgnoreCase(nombre_vacuna)){
                        myCallback.onCallback(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR2", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener los ID's de las vacunas que tiene la mascota
    public void getIdsVacunasMascota(final idCallback2 myCallback, String id_cliente, String id_mascota) {
        mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> lista_ids_vacunas_mascota = new ArrayList<>();
                int aux_contador = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista_ids_vacunas_mascota.add(aux_contador, ds.getKey());
                    aux_contador++;
                }
                myCallback.onCallback(lista_ids_vacunas_mascota);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR3", "onCancelled: "+databaseError);
            }
        });
    }

    // Obtener el número de dosis de una vacuna que tiene la mascota
    public void getNumDosisVacunaMascota(final idCallback4 myCallback, String id_cliente, String id_mascota, String id_vacuna) {
        mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").child(id_vacuna).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num_dosis = (int) dataSnapshot.getChildrenCount();
                myCallback.onCallback(num_dosis);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR4", "onCancelled: "+databaseError);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        nombres_vacunas = new ArrayList<>();
        loadAdapter();
        obtenerVacunasEspecie();
    }

    // Cargar adaptador para el Recyclerview de los datos de las vacunas de la mascota
    public void loadAdapter(){
        final DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
        ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                // Poner título de la Toolbar
                String nombre_mascota = data.get("nombre").toString();
                getSupportActionBar().setTitle("Vacunas de " + nombre_mascota);
                especie_mascota = data.get("especie").toString();
                ref_mascota.child("vacunas").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // ID de la ultima vacuna de la mascota
                        int id_ultima_vacuna = 0;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            id_ultima_vacuna = Integer.parseInt(child.getKey());
                        }
                        final int[] contador_aux = {0};
                        // Recorrer todas las vacunas de la mascota
                        for (int i = 0; i <= id_ultima_vacuna; i++) {
                            // Acceder a una vacuna individual
                            final DataSnapshot a = dataSnapshot.child(String.valueOf(i));
                            // Controlar que algun ID de vacuna no existe (Pueden no estar puestos consecutivamente los ID de las vacunas puestas a la mascota)
                            if(a.exists()){
                                final int finalI = i;
                                getNumeroDosisVacuna(new idCallback() {
                                    @Override
                                    public void onCallback(int num_dosis_vacuna, String tipo_ultima_dosis) {
                                        // Numero de dosis de esa vacuna individual
                                        int num_dosis = (int) a.getChildrenCount();
                                        // Acceder a la ultima dosis de esa vacuna individual
                                        DataSnapshot b = a.child("dosis_"+num_dosis);
                                        // Obtener los datos de la última dosis de esa vacuna individual
                                        Map<String, Object> data = (Map<String, Object>) b.getValue();
                                        vacuna.add(0, data.get("nombre").toString());
                                        vacuna.add(1, data.get("fecha").toString());
                                        vacuna.add(2, String.valueOf(num_dosis));
                                        vacuna.add(3, String.valueOf(num_dosis_vacuna));
                                        vacuna.add(4, tipo_ultima_dosis);
                                        vacuna.add(5, id_cliente);
                                        vacuna.add(6, id_mascota);
                                        vacuna.add(7, String.valueOf(finalI));
                                        vacunas.add(contador_aux[0], vacuna);
                                        vacuna = new ArrayList<>(vacuna);
                                        rvVacunas.setAdapter(adapter);
                                        contador_aux[0]++;
                                        // Si hay citas cerradas, se cambia el fondo predeterminado por un color
                                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.color_fondo));
                                    }
                                }, String.valueOf(i), especie_mascota);
                            }
                        }
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

    // Obtener las vacunas disponibles para la especie de la mascota elegida
    private void obtenerVacunasEspecie(){
        getIdsVacunasMascota(new idCallback2() {
            @Override
            public void onCallback(final List<String> lista_ids_vacunas_mascota) {
                DatabaseReference ref_mascota = mDatabase.child("usuarios").child(id_cliente).child("mascotas").child(id_mascota);
                ref_mascota.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        especie_mascota = data.get("especie").toString();
                        getIdsVacunasEspecie(new idCallback2() {
                            @Override
                            public void onCallback(List<String> lista_ids_vacunas_especie) {
                                List<String> vacunas_disponibles = new ArrayList<>(lista_ids_vacunas_especie);
                                // Recorrer los ID's de las vacunas que tiene la mascota
                                for (int i = 0; i < lista_ids_vacunas_mascota.size(); i++) {
                                    // Comparar el ID de la vacuna de la mascota con el ID' de la vacuna de la especie (pasando el valor de la vacuna de la mascota como clave de la vacuna de la especie)
                                    // para eliminar esa vacuna de la lista del Spinner para agregar una nueva vacuna a la mascota en el Dialog al pulsar el botón de "Agregar Vacuna"
                                    if(lista_ids_vacunas_mascota.get(i).equalsIgnoreCase(lista_ids_vacunas_especie.get(Integer.parseInt(lista_ids_vacunas_mascota.get(i))))){
                                        vacunas_disponibles.remove(lista_ids_vacunas_especie.get(Integer.parseInt(lista_ids_vacunas_mascota.get(i))));
                                    }
                                }
                                String especie_mascota_final = "";
                                if(especie_mascota.equalsIgnoreCase("perro")){
                                    especie_mascota_final = "perros";
                                }else if(especie_mascota.equalsIgnoreCase("gato")){
                                    especie_mascota_final = "gatos";
                                }
                                dosis_vacunas = new ArrayList<>();
                                // Referencia a las vacunas
                                DatabaseReference ref_vacunas = mDatabase.child("vacunas").child(especie_mascota_final);
                                // Recorrer las vacunas
                                for (int i = 0; i < vacunas_disponibles.size(); i++) {
                                    final int finalI = i;
                                    // Referencia a solamente las vacunas disponibles y guardar los nombres de esas vacunas
                                    ref_vacunas.child(vacunas_disponibles.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int num_dosis_vacuna = (int) (dataSnapshot.getChildrenCount()-1);
                                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                            dosis_vacuna = new ArrayList<>();
                                            nombres_vacunas.add(finalI, data.get("nombre").toString());
                                            for (int j = 1; j <= num_dosis_vacuna; j++) {
                                                dosis_vacuna.add(j+"ª Dosis");
                                            }
                                            dosis_vacunas.add(finalI, dosis_vacuna);
                                            dosis_vacuna = new ArrayList<>(dosis_vacuna);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d("ERROR7", "onCancelled: "+databaseError);
                                        }
                                    });
                                }
                            }
                        }, especie_mascota);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR8", "onCancelled: " + databaseError);
                    }
                });
            }
        }, id_cliente, id_mascota);
    }
}
