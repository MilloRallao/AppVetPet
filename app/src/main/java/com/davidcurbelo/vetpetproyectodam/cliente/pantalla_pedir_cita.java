package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.davidcurbelo.vetpetproyectodam.R;

public class pantalla_pedir_cita extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton  confirmar_cita;
    private ImageButton  historial_citas;
    private ImageButton  proximas_citas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pedir_cita);

        toolbar = this.findViewById(R.id.toolbar_pedir_cita);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confirmar_cita = this.findViewById(R.id.imageButton_confirmar_cita);
        historial_citas = this.findViewById(R.id.imageButton_historial_citas);
        proximas_citas = this.findViewById(R.id.imageButton_proximas_citas);

        confirmar_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_confirmar_cita.class);
                startActivity(intent);
            }
        });

        historial_citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_historial_citas.class);
                startActivity(intent);
            }
        });

        proximas_citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_proximas_citas.class);
                startActivity(intent);
            }
        });
    }
}
