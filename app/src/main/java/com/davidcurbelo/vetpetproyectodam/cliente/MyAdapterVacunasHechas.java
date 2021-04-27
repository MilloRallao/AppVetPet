package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterVacunasHechas extends RecyclerView.Adapter<MyAdapterVacunasHechas.MyViewHolder>{
    private static List<List<String>> vacunas;
    private List<String> vacuna;
    private Context contexto;

    MyAdapterVacunasHechas(List<List<String>> myDataset1, List<String> myDataSet2, Context context){
        contexto = context;
        vacunas = myDataset1;
        vacuna = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            vacunas.add(i, vacuna);
        }
    }

    @NonNull
    @Override
    public MyAdapterVacunasHechas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacuna_cardview, parent, false);
        return new MyAdapterVacunasHechas.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVacunasHechas.MyViewHolder holder, int position) {
        holder.vacuna.setText(vacunas.get(position).get(0));
        holder.fecha.setText(vacunas.get(position).get(1));
        // Si el numero de dosis totales de la vacuna especificada es igual al numero de dosis de esa vacuna que se le ha administrado a la mascota, entonces se pone un determinado icono
        if(vacunas.get(position).get(2).equals(vacunas.get(position).get(3))){
            // Si la ultima dosis de una vacuna es de tipo "Anual", se mostrará su icono
            if(vacunas.get(position).get(4).contains("Anual")){
                holder.vacuna.setText(vacunas.get(position).get(0));
                holder.fecha.setText(vacunas.get(position).get(1));
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.dosis_anual).into(holder.icono_dosis);
                holder.dosis.setText(vacunas.get(position).get(2).concat("ª dosis (Anual)"));
            }else{ // En caso contrario, se pone el icono especifico para cada numero de dosis actual hecha
                if(vacunas.get(position).get(2).equals("1")){
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.primera_dosis).into(holder.icono_dosis);
                }else if(vacunas.get(position).get(2).equals("2")){
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.segunda_dosis).into(holder.icono_dosis);
                }else if(vacunas.get(position).get(2).equals("3")){
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.tercera_dosis).into(holder.icono_dosis);
                }else if(vacunas.get(position).get(2).equals("4")){
                    holder.icono_dosis.setVisibility(View.VISIBLE);
                    Glide.with(contexto).load(R.drawable.cuarta_dosis).into(holder.icono_dosis);
                }
                holder.dosis.setText(vacunas.get(position).get(2).concat("ª dosis"));
            }
        }else{ // En caso contrario, se pone el icono especifico para cada numero de dosis actual hecha
            if(vacunas.get(position).get(2).equals("1")){
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.primera_dosis).into(holder.icono_dosis);
            }else if(vacunas.get(position).get(2).equals("2")){
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.segunda_dosis).into(holder.icono_dosis);
            }else if(vacunas.get(position).get(2).equals("3")){
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.tercera_dosis).into(holder.icono_dosis);
            }else if(vacunas.get(position).get(2).equals("4")){
                holder.icono_dosis.setVisibility(View.VISIBLE);
                Glide.with(contexto).load(R.drawable.cuarta_dosis).into(holder.icono_dosis);
            }
            holder.dosis.setText(vacunas.get(position).get(2).concat("ª dosis"));
        }
    }

    @Override
    public int getItemCount() {
        return vacunas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView vacuna;
        private TextView fecha;
        private TextView dosis;
        private ImageView icono_dosis;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            vacuna = v.findViewById(R.id.textView_nombre_vacuna);
            fecha = v.findViewById(R.id.textView_fecha_vacuna);
            dosis = v.findViewById(R.id.textView_nombre_dosis);
            icono_dosis = v.findViewById(R.id.imageView_dosis);
        }
    }
}
