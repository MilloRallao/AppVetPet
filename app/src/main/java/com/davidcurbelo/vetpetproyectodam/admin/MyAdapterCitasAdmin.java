package com.davidcurbelo.vetpetproyectodam.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterCitasAdmin extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static List<List<Object>> citas;
    private List<Object> cita;
    private Context context;
    private int controlCitas;

    MyAdapterCitasAdmin(List<List<Object>> myDataset1, List<Object> myDataSet2, Context context, int control) {
        this.context = context;
        citas = myDataset1;
        cita = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            citas.add(i, cita);
        }
        controlCitas = control;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1) {
            return new ProximasCitasViewHolder((LayoutInflater.from(parent.getContext()).inflate(R.layout.proximas_citas_admin_cardview, parent, false)));
        }else if(viewType == 2) {
            return new HistorialCitasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.historial_citas_admin_cardview, parent, false));
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == 1) {
            ProximasCitasViewHolder proximasCitasViewHolder = (ProximasCitasViewHolder) holder;
            Glide.with(context).load(citas.get(position).get(0)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(proximasCitasViewHolder.imagen);
            proximasCitasViewHolder.nombre_mascota.setText(citas.get(position).get(1).toString());
            proximasCitasViewHolder.nombre.setText(citas.get(position).get(2).toString());
            proximasCitasViewHolder.motivo.setText(citas.get(position).get(3).toString());
            proximasCitasViewHolder.fecha.setText(citas.get(position).get(4).toString());
            proximasCitasViewHolder.hora.setText(citas.get(position).get(5).toString());
        }else if(getItemViewType(position) == 2){
                HistorialCitasViewHolder historialCitasviewHolder = (HistorialCitasViewHolder) holder;
                Glide.with(historialCitasviewHolder.itemView.getContext()).load(citas.get(position).get(0)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into((historialCitasviewHolder).imagen);
                historialCitasviewHolder.nombre_mascota.setText(citas.get(position).get(1).toString());
                historialCitasviewHolder.nombre.setText(citas.get(position).get(2).toString());
                historialCitasviewHolder.motivo.setText(citas.get(position).get(3).toString());
                historialCitasviewHolder.fecha.setText(citas.get(position).get(4).toString());
                historialCitasviewHolder.hora.setText(citas.get(position).get(5).toString());
        }
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return controlCitas;
    }

    public static class ProximasCitasViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imagen;
        TextView nombre_mascota;
        TextView nombre;
        TextView fecha;
        TextView hora;
        TextView motivo;
        ProximasCitasViewHolder(View v){
            super(v);
            cardView = v.findViewById(R.id.cardview_proximas_citas_admin);
            imagen = v.findViewById(R.id.imageView_imagen_mascota_cita_admin);
            nombre_mascota = v.findViewById(R.id.textView_nombre_mascota_cita_admin);
            nombre = v.findViewById(R.id.textView_nombre_cliente_cita_admin);
            motivo = v.findViewById(R.id.textView_motivo_cita_admin);
            fecha = v.findViewById(R.id.textView_fecha_cita_admin);
            hora = v.findViewById(R.id.textView_hora_cita_admin);
        }
    }

    public static class HistorialCitasViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imagen;
        TextView nombre_mascota;
        TextView nombre;
        TextView fecha;
        TextView hora;
        TextView motivo;
        HistorialCitasViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cardview_historial_citas_admin);
            imagen = v.findViewById(R.id.imageView_imagen_mascota_historial_cita_admin);
            nombre_mascota = v.findViewById(R.id.textView_nombre_mascota_historial_cita_admin);
            nombre = v.findViewById(R.id.textView_nombre_cliente_historial_cita_admin);
            motivo = v.findViewById(R.id.textView_motivo_historial_cita_admin);
            fecha = v.findViewById(R.id.textView_fecha_historial_cita_admin);
            hora = v.findViewById(R.id.textView_hora_historial_cita_admin);
        }
    }
}
