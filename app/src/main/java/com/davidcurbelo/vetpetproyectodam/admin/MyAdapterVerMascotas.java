package com.davidcurbelo.vetpetproyectodam.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterVerMascotas extends RecyclerView.Adapter<MyAdapterVerMascotas.MyViewHolder>  {
    private static List<List<String>> mascotas;
    private List<String> mascota;
    private Context contexto;

    MyAdapterVerMascotas(List<List<String>> myDataset1, List<String> myDataSet2, Context context) {
        contexto = context;
        mascotas = myDataset1;
        mascota = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            mascotas.add(i, mascota);
        }
    }

    @NonNull
    @Override
    public MyAdapterVerMascotas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_mascotas_cardview, parent, false);
        return new MyAdapterVerMascotas.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVerMascotas.MyViewHolder holder, int position) {
        holder.nombre.setText(mascotas.get(position).get(0));
        Glide.with(contexto).load(mascotas.get(position).get(1)).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return mascotas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView nombre;
        private ImageView imagen;
        private Button diagnosticos;
        private Button vacunas;
        private ImageButton info;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            nombre= v.findViewById(R.id.textView_nombre_mascota_cliente);
            imagen = v.findViewById(R.id.imageView_mascota_cliente);
            diagnosticos = v.findViewById(R.id.button_diagnosticos_mascota);
            diagnosticos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id_mascota = mascotas.get(getAdapterPosition()).get(2);
                    String id_cliente = mascotas.get(getAdapterPosition()).get(3);
                    Intent intent = new Intent(context, pantalla_diagnosticos_mascota_cliente.class);
                    intent.putExtra("id_mascota", id_mascota);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            vacunas = v.findViewById(R.id.button_vacunas_mascota);
            vacunas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id_mascota = mascotas.get(getAdapterPosition()).get(2);
                    String id_cliente = mascotas.get(getAdapterPosition()).get(3);
                    Intent intent = new Intent(context, pantalla_vacunas_mascota_cliente.class);
                    intent.putExtra("id_mascota", id_mascota);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            info = v.findViewById(R.id.imageButton_info_mascota);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id_mascota = mascotas.get(getAdapterPosition()).get(2);
                    String id_cliente = mascotas.get(getAdapterPosition()).get(3);
                    Intent intent = new Intent(context, pantalla_info_mascota_cliente.class);
                    intent.putExtra("id_mascota", id_mascota);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
        }
    }
}
