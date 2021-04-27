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

import com.bumptech.glide.Glide;
import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterVacunasPendientes extends RecyclerView.Adapter<MyAdapterVacunasPendientes.MyViewHolder>{
    private static List<List<String>> vacunas;
    private List<String> vacuna;
    private Context contexto;

    MyAdapterVacunasPendientes(List<List<String>> myDataset1, List<String> myDataSet2, Context context){
        contexto = context;
        vacunas = myDataset1;
        vacuna = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            vacunas.add(i, vacuna);
        }
    }

    @NonNull
    @Override
    public MyAdapterVacunasPendientes.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacuna_cardview, parent, false);
        return new MyAdapterVacunasPendientes.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVacunasPendientes.MyViewHolder holder, int position) {
        // Si el numero de dosis totales de la vacuna especificada es igual al numero de dosis de esa vacuna que se le ha administrado a la mascota, entonces se pone un determinado icono
        if(vacunas.get(position).get(2).equals(vacunas.get(position).get(3))){
            // Si la ultima dosis de una vacuna es de tipo "Anual", se mostrará para repetirse al año, en caso contrario, no se mostrará, puesto que no hay más dosis de esa vacuna
            if(vacunas.get(position).get(4).contains("Anual")){
                holder.vacuna.setText(vacunas.get(position).get(0));
                holder.texto_fecha.setText("Debería hacerse en:");
                holder.fecha.setText(vacunas.get(position).get(1));                                 // CALCULAR LA FECHA PRÓXIMA
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.dosis_anual).into(holder.icono_dosis);
                holder.dosis.setText(vacunas.get(position).get(2).concat("ª dosis (Anual)"));
            }else {
                holder.itemView.findViewById(R.id.linearlayout_vacunas).setVisibility(View.GONE);
            }
        }else{ // En caso contrario, se pone el icono especifico de la siguiente dosis estipulada
            holder.vacuna.setText(vacunas.get(position).get(0));
            holder.texto_fecha.setText("Debería hacerse en:");
            int siguiente_dosis = Integer.parseInt(vacunas.get(position).get(2)) + 1;
            switch (siguiente_dosis){
                case 1:
                    holder.fecha.setText(vacunas.get(position).get(1));                             // CALCULAR LA FECHA PRÓXIMA
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.primera_dosis).into(holder.icono_dosis);
                    break;
                case 2:
                    holder.fecha.setText(vacunas.get(position).get(1));                             // CALCULAR LA FECHA PRÓXIMA
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.segunda_dosis).into(holder.icono_dosis);
                    break;
                case 3:
                    holder.fecha.setText(vacunas.get(position).get(1));                             // CALCULAR LA FECHA PRÓXIMA
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.tercera_dosis).into(holder.icono_dosis);
                    break;
                case 4:
                    holder.fecha.setText(vacunas.get(position).get(1));                             // CALCULAR LA FECHA PRÓXIMA
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.cuarta_dosis).into(holder.icono_dosis);
                    break;
            }
            holder.dosis.setText(siguiente_dosis + "ª dosis");
            holder.pedir_cita.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorNegative)));
        }
    }

    @Override
    public int getItemCount() {
        return vacunas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private CardView cardView;
        private TextView vacuna;
        private TextView texto_fecha;
        private TextView fecha;
        private TextView dosis;
        private Button pedir_cita;
        private ImageView icono_dosis;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_vacuna);
            vacuna = v.findViewById(R.id.textView_nombre_vacuna);
            texto_fecha = v.findViewById(R.id.textView_texto_fecha_vacuna);
            fecha = v.findViewById(R.id.textView_fecha_vacuna);
            dosis = v.findViewById(R.id.textView_nombre_dosis);
            pedir_cita = v.findViewById(R.id.button_pedir_cita_vacuna);
            icono_dosis = v.findViewById(R.id.imageView_dosis);
            pedir_cita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_pedir_cita.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
