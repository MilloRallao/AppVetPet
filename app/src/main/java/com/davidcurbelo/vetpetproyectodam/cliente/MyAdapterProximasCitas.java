package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyAdapterProximasCitas extends RecyclerView.Adapter<MyAdapterProximasCitas.MyViewHolder>{
    private static List<List<Object>> citas;
    private List<Object> cita;
    private Context context;

    MyAdapterProximasCitas(List<List<Object>> myDataset1, List<Object> myDataSet2, Context context) {
        this.context = context;
        citas = myDataset1;
        cita = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            citas.add(i, cita);
        }
    }

    @NonNull
    @Override
    public MyAdapterProximasCitas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.proximas_citas_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterProximasCitas.MyViewHolder holder, int position) {
        holder.id.setText("ID Cita: #" + citas.get(position).get(0).toString());
        Glide.with(context).load(citas.get(position).get(1)).apply(new RequestOptions().transform(new RoundedCorners(50)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imagen);
        holder.fecha.setText(citas.get(position).get(2).toString());
        holder.hora.setText(citas.get(position).get(3).toString());
        holder.nombre.setText(citas.get(position).get(4).toString());
        holder.motivo.setText(citas.get(position).get(5).toString());
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private final Context context;
        TextView id;
        ImageView imagen;
        TextView fecha;
        TextView hora;
        TextView nombre;
        TextView motivo;
        ImageButton cancelar;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            id = v.findViewById(R.id.textView_id_cita_proximas_citas);
            imagen = v.findViewById(R.id.imageView_mascota_proximas_citas);
            fecha = v.findViewById(R.id.textView_fecha_proximas_citas);
            hora = v.findViewById(R.id.textView_hora_proximas_citas);
            nombre = v.findViewById(R.id.textView_nombre_proximas_citas);
            motivo = v.findViewById(R.id.textView_motivo_proximas_citas);
            cancelar = v.findViewById(R.id.imageButton_cancelar_cita_proximas_citas);
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
