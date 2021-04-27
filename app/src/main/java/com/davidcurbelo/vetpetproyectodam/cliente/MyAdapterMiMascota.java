package com.davidcurbelo.vetpetproyectodam.cliente;

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

public class MyAdapterMiMascota extends RecyclerView.Adapter<MyAdapterMiMascota.MyViewHolder> {
    private static List<List<String>> mascotas;
    private List<String> mascota;
    private Context contexto;

    MyAdapterMiMascota(List<List<String>> myDataset1, List<String> myDataSet2, Context context) {
        contexto = context;
        mascotas = myDataset1;
        mascota = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            mascotas.add(i, mascota);
        }
    }

    @NonNull
    @Override
    public MyAdapterMiMascota.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mascota_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.nombre.setText(mascotas.get(position).get(0));
        holder.sexo.setText(mascotas.get(position).get(1));
        holder.especie.setText(mascotas.get(position).get(2));
        holder.edad.setText(mascotas.get(position).get(3));
        holder.peso.setText(mascotas.get(position).get(4));
        Glide.with(contexto).load(mascotas.get(position).get(5)).apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mascotas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        ImageView imageView;
        TextView nombre;
        TextView sexo;
        TextView especie;
        TextView edad;
        TextView peso;
        Button historial_citas;
        ImageButton informacion;
        Button consultas_online;
        Button tratamientos;
        Button vacunas;
        Button diagnosticos;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            imageView = v.findViewById(R.id.imagen_mascota_mi_mascota);
            nombre = v.findViewById(R.id.textView_nombre_mascota_mi_mascota);
            sexo = v.findViewById(R.id.textView_sexo_mascota_mi_mascota);
            especie = v.findViewById(R.id.textView_especie_mascota_mi_mascota);
            edad = v.findViewById(R.id.textView_edad_mascota_mi_mascota);
            peso = v.findViewById(R.id.textView_peso_mascota_mi_mascota);
            historial_citas = v.findViewById(R.id.button_citas_mi_mascota);
            historial_citas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_historial_citas.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("imagen", aux_imagen);
                    intent.putExtra("nombre", aux_nombre);
                    context.startActivity(intent);
                }
            });
            informacion = v.findViewById(R.id.imageButton_info_mi_mascota);
            informacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_informacion.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("imagen", aux_imagen);
                    intent.putExtra("nombre", aux_nombre);
                    context.startActivity(intent);
                }
            });
            consultas_online = v.findViewById(R.id.button_consultas_mi_mascota);
            consultas_online.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_consultas.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("imagen", aux_imagen);
                    intent.putExtra("nombre", aux_nombre);
                    context.startActivity(intent);
                }
            });
            tratamientos = v.findViewById(R.id.button_tratamientos);
            tratamientos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_tratamientos.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("nombre", aux_nombre);
                    intent.putExtra("imagen", aux_imagen);
                    context.startActivity(intent);
                }
            });
            vacunas = v.findViewById(R.id.button_vacunas);
            vacunas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_vacunas.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    String aux_especie = mascotas.get(getAdapterPosition()).get(2);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("nombre", aux_nombre);
                    intent.putExtra("imagen", aux_imagen);
                    intent.putExtra("especie", aux_especie);
                    context.startActivity(intent);
                }
            });
            diagnosticos = v.findViewById(R.id.button_diagnosticos);
            diagnosticos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_diagnosticos.class);
                    String aux_nombre = mascotas.get(getAdapterPosition()).get(0);
                    String aux_imagen = mascotas.get(getAdapterPosition()).get(5);
                    String aux_id = mascotas.get(getAdapterPosition()).get(6);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("nombre", aux_nombre);
                    intent.putExtra("imagen", aux_imagen);
                    context.startActivity(intent);
                }
            });
        }
    }
}