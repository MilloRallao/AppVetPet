package com.davidcurbelo.vetpetproyectodam.cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.material.tabs.TabLayout;

public class pantalla_mi_mascota_diagnosticos extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imagen;

    private String aux_nombre;
    private String aux_imagen;
    private String aux_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_mi_mascota_diagnosticos);

        toolbar = this.findViewById(R.id.toolbar_mi_mascota_diagnostico);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aux_nombre = getIntent().getExtras().getString("nombre");
        aux_imagen = getIntent().getExtras().getString("imagen");
        aux_id = getIntent().getExtras().getString("id");

        // Poner imagen de la mascota
        imagen = this.findViewById(R.id.imageView_mi_mascota_diagnostico);
        Glide.with(this).load(aux_imagen).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(imagen);

        // Titulo de la toolbar segun la mascota elegida
        getSupportActionBar().setTitle("Diagnósticos de "+ aux_nombre);

        tabLayout = this.findViewById(R.id.tablayout_diagnosticos);

        viewPager = this.findViewById(R.id.viewpager_diagnosticos);
        View view = this.getWindow().getDecorView().findViewById(android.R.id.content);
        MyAdapterDiagnosticos adapter = new MyAdapterDiagnosticos(this,getSupportFragmentManager(), tabLayout.getTabCount(), aux_id, view);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
