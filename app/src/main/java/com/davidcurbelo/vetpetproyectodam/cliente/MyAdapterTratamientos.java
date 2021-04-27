package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapterTratamientos extends FragmentPagerAdapter {
    private Context myContext;
    private int totalTabs;
    private String id_mascota;
    private View myView;

    public MyAdapterTratamientos(Context context, FragmentManager fm, int totalTabs, String id, View view) {
        super(fm, totalTabs);
        myContext = context;
        this.totalTabs = totalTabs;
        id_mascota = id;
        myView = view;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new fragment_tratamientos_actuales(myView, id_mascota);
            case 1:
                return new fragment_tratamientos_anteriores(myView, id_mascota);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
