package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterTramientosActuales extends RecyclerView.Adapter<MyAdapterTramientosActuales.MyViewHolder> {
    private static List<List<String>> tratamientos;
    private List<String> tratamiento;
    private Context contexto;

    MyAdapterTramientosActuales(List<List<String>> myDataset1, List<String> myDataSet2, Context context){
        contexto = context;
        tratamientos = myDataset1;
        tratamiento = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            tratamientos.add(i, tratamiento);
        }
    }

    @NonNull
    @Override
    public MyAdapterTramientosActuales.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tratamiento_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterTramientosActuales.MyViewHolder holder, int position) {
        holder.fecha_inicio.setText(tratamientos.get(position).get(0));
        if(tratamientos.get(position).get(1).equalsIgnoreCase("Crónico")){
            holder.icono.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorcancel)));
        }else if(tratamientos.get(position).get(1).equalsIgnoreCase("Sin fecha")){
            holder.icono.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorNegative)));
        }
        holder.fecha_fin.setText(tratamientos.get(position).get(1));
        holder.farmaco.setText(tratamientos.get(position).get(2));
        holder.dosis.setText(tratamientos.get(position).get(3));
        holder.administracion.setText(tratamientos.get(position).get(4));
    }

    @Override
    public int getItemCount() {
        return tratamientos.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private CardView cardView;
        private TextView fecha_inicio;
        private TextView fecha_fin;
        private TextView farmaco;
        private TextView dosis;
        private TextView administracion;
        private ImageView icono;
        private Button ver_diagnostico;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_tratamiento);
            fecha_inicio = v.findViewById(R.id.textView_fecha_inicio_tratamiento_mi_mascota);
            fecha_fin = v.findViewById(R.id.textView_fecha_fin_tratamiento_mi_mascota);
            farmaco = v.findViewById(R.id.textView_farmaco_tratamiento_mi_mascota);
            dosis = v.findViewById(R.id.textView_dosis_tramiento_mi_mascota);
            administracion = v.findViewById(R.id.textView_forma_administracion_tratamiento_mi_mascota);
            icono = v.findViewById(R.id.imageView_icono_tratamiento);
            ver_diagnostico = v.findViewById(R.id.button_ver_diagnostico_tratamientos_mi_mascota);
            ver_diagnostico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_diagnosticos.class);
                    String aux_nombre = tratamientos.get(getAdapterPosition()).get(5);
                    String aux_id = tratamientos.get(getAdapterPosition()).get(6);
                    String aux_imagen = tratamientos.get(getAdapterPosition()).get(7);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("nombre", aux_nombre);
                    intent.putExtra("imagen", aux_imagen);
                    context.startActivity(intent);
                }
            });
        }
    }
}
