package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.davidcurbelo.vetpetproyectodam.R;

public class pantalla_consulta_online extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton nueva_consulta;
    private ImageButton consultas_abiertas;
    private ImageButton consultas_cerradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_consulta_online);

        toolbar = this.findViewById(R.id.toolbar_consulta_online_usuario);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nueva_consulta = this.findViewById(R.id.imageButton_nueva_consulta);
        consultas_abiertas = this.findViewById(R.id.imageButton_consultas_abiertas);
        consultas_cerradas = this.findViewById(R.id.imageButton_consultas_cerradas);

        nueva_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_nueva_consulta.class);
                startActivity(intent);
            }
        });

        consultas_abiertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consultas_abiertas.class);
                startActivity(intent);
            }
        });

        consultas_cerradas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consultas_cerradas.class);
                startActivity(intent);
            }
        });
    }
}
