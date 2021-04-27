package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyAdapterHistorialCitasMiMascota extends RecyclerView.Adapter<MyAdapterHistorialCitasMiMascota.MyViewHolder> {
    private static List<List<Object>> citas;
    private List<Object> cita;
    private Context contexto;

    MyAdapterHistorialCitasMiMascota(List<java.util.List<Object>> myDataset1, List<Object> myDataSet2, Context context) {
        contexto = context;
        citas = myDataset1;
        cita = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            citas.add(i, cita);
        }
    }

    @NonNull
    @Override
    public MyAdapterHistorialCitasMiMascota.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mascota_historial_citas_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterHistorialCitasMiMascota.MyViewHolder holder, int position) {
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
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(contexto.getResources().getColor(R.color.colorNegative)));
        }else {
            holder.texto_estado.setText("Cancelar cita");
            holder.texto_estado.setTextColor(Color.parseColor("#600E0E"));
            holder.texto_estado.setTypeface(Typeface.DEFAULT_BOLD);
            holder.cancelar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private CardView cardView;
        private TextView id;
        private ImageView imagen;
        private TextView fecha;
        private TextView hora;
        private TextView nombre;
        private TextView motivo;
        private TextView texto_estado;
        private ImageView estado;
        private ImageButton cancelar;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            cardView = v.findViewById(R.id.cardview_historial_citas_mi_mascota);
            id = v.findViewById(R.id.textView_id_cita_historial_citas_mi_mascota);
            imagen = v.findViewById(R.id.imageView_imagen_historial_citas_mi_mascota);
            fecha = v.findViewById(R.id.textView_fecha_historial_citas_mi_mascota);
            hora = v.findViewById(R.id.textView_hora_historial_citas_mi_mascota);
            nombre = v.findViewById(R.id.textView_nombre_mascota_historial_citas_mi_mascota);
            motivo = v.findViewById(R.id.textView_motivo_historial_citas_mi_mascota);
            texto_estado = v.findViewById(R.id.textView_texto_estado_cita_mi_mascota);
            estado = v.findViewById(R.id.imageView_estado_cita_mi_mascota);
            cancelar = v.findViewById(R.id.imageButton_cancelar_cita_mi_mascota);
            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage("¿Está seguro de que quiere cancelar esta cita?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String id_cita = citas.get(getAdapterPosition()).get(0).toString();
                                    String id_clinica = citas.get(getAdapterPosition()).get(6).toString();
                                    FirebaseDatabase.getInstance().getReference().child("citas").child(id_clinica).child(id_cita).child("estado").setValue("Cancelada");
                                    Intent i = new Intent(v.getContext(), pantalla_proximas_citas.class);
                                    context.startActivity(i);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("AlertDialog", "Negativo");
                        }
                    }).show();
                }
            });
        }
    }
}