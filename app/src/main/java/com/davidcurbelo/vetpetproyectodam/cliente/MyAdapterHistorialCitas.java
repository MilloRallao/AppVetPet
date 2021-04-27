package com.davidcurbelo.vetpetproyectodam.cliente;

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

public class MyAdapterHistorialCitas extends  RecyclerView.Adapter<MyAdapterHistorialCitas.MyViewHolder>{

    private static List<List<Object>> citas;
    private List<Object> cita;
    private Context contexto;

    MyAdapterHistorialCitas(List<List<Object>> myDataset1, List<Object> myDataSet2, Context context) {
        contexto = context;
        citas = myDataset1;
        cita = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            citas.add(i, cita);
        }
    }

    @NonNull
    @Override
    public MyAdapterHistorialCitas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.historial_citas_cardview, parent, false);
        return new MyAdapterHistorialCitas.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterHistorialCitas.MyViewHolder holder, int position) {
        holder.id.setText("ID Cita: #" + citas.get(position).get(0).toString());
        Glide.with(contexto).load(citas.get(position).get(1)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imagen);
        holder.fecha.setText(citas.get(position).get(2).toString());
        holder.hora.setText(citas.get(position).get(3).toString());
        holder.nombre.setText(citas.get(position).get(4).toString());
        holder.motivo.setText(citas.get(position).get(5).toString());
        if(citas.get(position).get(6).toString().equalsIgnoreCase("Cancelada")){
            holder.texto_estado.setText("Cita Cancelada");
            Glide.with(contexto).load(R.drawable.icono_cancelar_pressed).into(holder.estado);
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorcancel)));
        }else if(citas.get(position).get(6).toString().equalsIgnoreCase("Realizada")){
            holder.texto_estado.setText("Cita Realizada");
            Glide.with(contexto).load(R.drawable.icono_realizado).into(holder.estado);
        }
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private Context context;
        CardView cardView;
        TextView id;
        ImageView imagen;
        TextView fecha;
        TextView hora;
        TextView nombre;
        TextView motivo;
        TextView texto_estado;
        ImageView estado;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_historial_citas);
            id = v.findViewById(R.id.textView_id_cita_historial_citas);
            imagen = v.findViewById(R.id.imageView_imagen_historial_citas);
            fecha = v.findViewById(R.id.textView_fecha_historial_citas);
            hora = v.findViewById(R.id.textView_hora_historial_citas);
            nombre = v.findViewById(R.id.textView_nombre_mascota_historial_citas);
            motivo = v.findViewById(R.id.textView_texto_motivo_historial_citas);
            texto_estado = v.findViewById(R.id.textView_texto_estado_cita);
            estado = v.findViewById(R.id.imageView_estado_cita);
        }
    }
}
