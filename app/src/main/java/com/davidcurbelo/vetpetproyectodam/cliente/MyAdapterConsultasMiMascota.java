package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class MyAdapterConsultasMiMascota extends RecyclerView.Adapter<MyAdapterConsultasMiMascota.MyViewHolder> {
    private static List<List<Object>> consultas;
    private List<Object> consulta;
    private Context contexto;

    MyAdapterConsultasMiMascota(List<java.util.List<Object>> myDataset1, List<Object> myDataSet2, Context context) {
        contexto = context;
        consultas = myDataset1;
        consulta = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            consultas.add(i, consulta);
        }
    }

    @NonNull
    @Override
    public MyAdapterConsultasMiMascota.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mascota_consultas_online_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterConsultasMiMascota.MyViewHolder holder, int position) {
        holder.id.setText("ID Consulta: #" +consultas.get(position).get(0).toString());
        Glide.with(contexto).load(consultas.get(position).get(1)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imagen);
        holder.fecha.setText(consultas.get(position).get(2).toString());
        holder.nombre.setText(consultas.get(position).get(3).toString());
        if(consultas.get(position).get(4).toString().length() >= 20){
            holder.asunto.setText(consultas.get(position).get(4).toString().substring(0,20).concat("..."));
        }else{
            holder.asunto.setText(consultas.get(position).get(4).toString());
        }
        if(Integer.parseInt(consultas.get(position).get(5).toString()) > 1){
            holder.adjuntos.setText(consultas.get(position).get(5).toString().concat(" Imágenes Adjuntas"));
        }else if(Integer.parseInt(consultas.get(position).get(5).toString()) <= 1){
            holder.adjuntos.setText(consultas.get(position).get(5).toString().concat(" Imagen Adjunta"));
        }
        holder.respuesta.setText(consultas.get(position).get(6).toString());
        Glide.with(contexto).load(consultas.get(position).get(7)).into(holder.icono);
        if(consultas.get(position).get(10).toString().equalsIgnoreCase("Cerrada")){
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorNegative)));
        }
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final Context context;
        CardView cardView;
        TextView id;
        ImageView imagen;
        TextView fecha;
        TextView nombre;
        TextView asunto;
        TextView adjuntos;
        TextView respuesta;
        ImageView icono;
        ImageButton ver;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_consulta_online_mi_mascota);
            id = v.findViewById(R.id.textView_id_consulta_consultas_online_mi_mascota);
            imagen = v.findViewById(R.id.imageView_imagen_consulta_online_mi_mascota);
            fecha = v.findViewById(R.id.textView_fecha_consulta_online_mi_mascota);
            nombre = v.findViewById(R.id.textView_nombre_consulta_online_mi_mascota);
            asunto = v.findViewById(R.id.textView_asunto_consulta_online_mi_mascota);
            adjuntos = v.findViewById(R.id.textView_adjuntos_consulta_online_mi_mascota);
            respuesta = v.findViewById(R.id.textView_respuesta_consulta_online_mi_mascota);
            icono = v.findViewById(R.id.imageView_icono_respuesta_consulta_online_mi_mascota);
            ver = v.findViewById(R.id.imageButton_ver_consulta_online_mi_mascota);
            ver.setOnClickListener(this);
        }

        public void onClick(View v) {
            Intent intent = new Intent(context, pantalla_consulta_individual.class);

            String aux_adjunto1;
            String aux_adjunto2;
            String aux_adjunto3;

            String aux_id = consultas.get(getAdapterPosition()).get(0).toString();
            String aux_imagen = consultas.get(getAdapterPosition()).get(1).toString();
            String aux_nombre = nombre.getText().toString();
            String aux_fecha = fecha.getText().toString();
            String aux_respuesta = respuesta.getText().toString();
            String aux_asunto = consultas.get(getAdapterPosition()).get(4).toString();
            String aux_num_adjuntos = consultas.get(getAdapterPosition()).get(5).toString();
            String aux_hora = consultas.get(getAdapterPosition()).get(8).toString();
            String aux_mensaje = consultas.get(getAdapterPosition()).get(9).toString();
            String aux_estado = consultas.get(getAdapterPosition()).get(10).toString();

            intent.putExtra("num_adjuntos", aux_num_adjuntos);

            // Controlar el numero de imagenes adjuntas a enviar a la nueva activity
            switch (aux_num_adjuntos){
                case "1":
                    aux_adjunto1 = consultas.get(getAdapterPosition()).get(11).toString();
                    intent.putExtra("adjunto1", aux_adjunto1);
                    break;
                case "2":
                    aux_adjunto1 = consultas.get(getAdapterPosition()).get(11).toString();
                    aux_adjunto2 = consultas.get(getAdapterPosition()).get(12).toString();
                    intent.putExtra("adjunto1", aux_adjunto1);
                    intent.putExtra("adjunto2", aux_adjunto2);
                    break;
                case "3":
                    aux_adjunto1 = consultas.get(getAdapterPosition()).get(11).toString();
                    aux_adjunto2 = consultas.get(getAdapterPosition()).get(12).toString();
                    aux_adjunto3 = consultas.get(getAdapterPosition()).get(13).toString();
                    intent.putExtra("adjunto1", aux_adjunto1);
                    intent.putExtra("adjunto2", aux_adjunto2);
                    intent.putExtra("adjunto3", aux_adjunto3);
                    break;
            }

            intent.putExtra("id", aux_id);
            intent.putExtra("nombre", aux_nombre);
            intent.putExtra("fecha", aux_fecha);
            intent.putExtra("respuesta", aux_respuesta);
            intent.putExtra("hora", aux_hora);
            intent.putExtra("asunto", aux_asunto);
            intent.putExtra("mensaje", aux_mensaje);
            intent.putExtra("imagen", aux_imagen);
            intent.putExtra("estado", aux_estado);

            context.startActivity(intent);
        }
    }
}
