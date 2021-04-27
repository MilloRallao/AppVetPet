package com.davidcurbelo.vetpetproyectodam.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.davidcurbelo.vetpetproyectodam.R;

public class pantalla_ver_consultas_online extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton consultas_abiertas;
    private ImageButton consultas_cerradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_ver_consultas_online);

        toolbar = this.findViewById(R.id.toolbar_ver_consultas_online);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        consultas_abiertas = this.findViewById(R.id.imageButton_consultas_abiertas_admin);
        consultas_abiertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consultas_abiertas_admin.class);
                startActivity(intent);
            }
        });
        consultas_cerradas = this.findViewById(R.id.imageButton_consultas_cerradas_admin);
        consultas_cerradas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pantalla_consultas_cerradas_admin.class);
                startActivity(intent);
            }
        });
    }
}
