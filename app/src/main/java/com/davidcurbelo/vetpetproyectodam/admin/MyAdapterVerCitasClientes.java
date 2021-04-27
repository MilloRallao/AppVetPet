package com.davidcurbelo.vetpetproyectodam.admin;

import android.content.Context;
import android.content.res.ColorStateList;
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

public class MyAdapterVerCitasClientes extends RecyclerView.Adapter<MyAdapterVerCitasClientes.MyViewHolder>{
    private static List<List<Object>> citas;
    private List<Object> cita;
    private Context contexto;

    MyAdapterVerCitasClientes(List<List<Object>> myDataset1, List<Object> myDataSet2, Context context) {
        this.contexto = context;
        citas = myDataset1;
        cita = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            citas.add(i, cita);
        }
    }

    @NonNull
    @Override
    public MyAdapterVerCitasClientes.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_citas_cliente_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVerCitasClientes.MyViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(citas.get(position).get(0)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imagen);
        holder.nombre_mascota.setText(citas.get(position).get(1).toString());
        if (citas.get(position).get(6).toString().equalsIgnoreCase("Abierta")){
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorAccent)));
            holder.nombre_cita_abierta.setText(citas.get(position).get(2).toString());
            holder.texto_motivo_cita_abierta.setVisibility(View.VISIBLE);
            holder.motivo_cita_abierta.setText(citas.get(position).get(3).toString());
            holder.fecha_cita_abierta.setText(citas.get(position).get(4).toString());
            holder.hora_cita_abierta.setText(citas.get(position).get(5).toString());
        }else if(citas.get(position).get(6).toString().equalsIgnoreCase("Realizada")){
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorNegative)));
            holder.nombre_cita_cerrada.setText(citas.get(position).get(2).toString());
            holder.texto_motivo_cita_cerrada.setVisibility(View.VISIBLE);
            holder.motivo_cita_cerrada.setText(citas.get(position).get(3).toString());
            holder.fecha_cita_cerrada.setText(citas.get(position).get(4).toString());
            holder.hora_cita_cerrada.setText(citas.get(position).get(5).toString());
            holder.texto_cita_realizada.setVisibility(View.VISIBLE);
            holder.icono_cita_realizada.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        CardView cardView;
        ImageView imagen;
        TextView nombre_mascota;
        TextView nombre_cita_abierta;
        TextView nombre_cita_cerrada;
        TextView fecha_cita_abierta;
        TextView fecha_cita_cerrada;
        TextView hora_cita_abierta;
        TextView hora_cita_cerrada;
        TextView texto_motivo_cita_abierta;
        TextView texto_motivo_cita_cerrada;
        TextView motivo_cita_abierta;
        TextView motivo_cita_cerrada;
        TextView texto_cita_realizada;
        ImageView icono_cita_realizada;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_ver_citas_cliente_admin);
            imagen = v.findViewById(R.id.imageView_imagen_mascota_cita_cliente_admin);
            nombre_mascota = v.findViewById(R.id.textView_nombre_mascota_cita_cliente_admin);
            nombre_cita_abierta = v.findViewById(R.id.textView_nombre_cliente_cita_abierta_cita_cliente_admin);
            texto_motivo_cita_abierta = v.findViewById(R.id.textView_texto_motivo_cita_abierta_cita_cliente_admin);
            motivo_cita_abierta = v.findViewById(R.id.textView_motivo_cita_abierta_cita_cliente_admin);
            fecha_cita_abierta = v.findViewById(R.id.textView_fecha_cita_abierta_cita_cliente_admin);
            hora_cita_abierta = v.findViewById(R.id.textView_hora_cita_abierta_cita_cliente_admin);
            nombre_cita_cerrada = v.findViewById(R.id.textView_nombre_cliente_cita_cerrada_cita_cliente_admin);
            texto_motivo_cita_cerrada = v.findViewById(R.id.textView_texto_motivo_cita_cerrada_cita_cliente_admin);
            motivo_cita_cerrada = v.findViewById(R.id.textView_motivo_cita_cerrada_cita_cliente_admin);
            fecha_cita_cerrada = v.findViewById(R.id.textView_fecha_cita_cerrada_cita_cliente_admin);
            hora_cita_cerrada = v.findViewById(R.id.textView_hora_cita_cerrada_cita_cliente_admin);
            texto_cita_realizada = v.findViewById(R.id.textView_texto_cita_realizada_cita_cliente_admin);
            icono_cita_realizada = v.findViewById(R.id.imageView_cita_realizada_cita_cliente_admin);
        }
    }
}
