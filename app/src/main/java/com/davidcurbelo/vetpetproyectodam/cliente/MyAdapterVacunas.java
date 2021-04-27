package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapterVacunas extends FragmentPagerAdapter {
    private Context myContext;
    private int totalTabs;
    private String id_mascota;
    private String especie;
    private View myView;

    public MyAdapterVacunas(Context context, FragmentManager fm, int totalTabs, String id, View view, String especie) {
        super(fm, totalTabs);
        myContext = context;
        this.totalTabs = totalTabs;
        id_mascota = id;
        this.especie = especie;
        myView = view;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new fragment_vacunas_hechas(myView, id_mascota, especie);
            case 1:
                return new fragment_vacunas_pendientes(myView, id_mascota, especie);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
