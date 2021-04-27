package com.davidcurbelo.vetpetproyectodam.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapterVacunasMascotaCliente extends RecyclerView.Adapter<MyAdapterVacunasMascotaCliente.MyViewHolder> {
    private static List<List<String>> vacunas;
    private List<String> vacuna;
    private Context contexto;

    MyAdapterVacunasMascotaCliente(List<List<String>> myDataset1, List<String> myDataSet2, Context context) {
        contexto = context;
        vacunas = myDataset1;
        vacuna = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            vacunas.add(i, vacuna);
        }
    }

    @NonNull
    @Override
    public MyAdapterVacunasMascotaCliente.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacunas_mascota_cliente_cardview, parent, false);
        return new MyAdapterVacunasMascotaCliente.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVacunasMascotaCliente.MyViewHolder holder, int position) {
        holder.vacuna.setText(vacunas.get(position).get(0));
        holder.fecha.setText(vacunas.get(position).get(1));
        // Si el numero de dosis totales de la vacuna especificada es igual al numero de dosis de esa vacuna que se le ha administrado a la mascota y si la ultima dosis de una vacuna es de tipo "Anual", se mostrará su icono
        if(vacunas.get(position).get(2).equals(vacunas.get(position).get(3)) && vacunas.get(position).get(4).contains("Anual")){
            holder.vacuna.setText(vacunas.get(position).get(0));
            holder.fecha.setText(vacunas.get(position).get(1));
            holder.fecha_siguiente.setText("CALCULAR");                                             //CALCULAR
            Glide.with(contexto).load(R.drawable.dosis_anual).into(holder.dosis_imagen);
            holder.dosis.setText(vacunas.get(position).get(2).concat("ª dosis (Anual)"));
            holder.dosis_siguiente.setText(vacunas.get(position).get(2).concat("ª dosis (Anual)"));
        }else{ // En caso contrario, se pone el icono especifico para cada numero de dosis actual hecha
            if(vacunas.get(position).get(2).equals("1")){
                holder.fecha_siguiente.setText("CALCULAR");                                         //CALCULAR
                Glide.with(contexto).load(R.drawable.primera_dosis).into(holder.dosis_imagen);
                holder.dosis_siguiente.setText("2ª dosis");
            }else if(vacunas.get(position).get(2).equals("2")){
                holder.fecha_siguiente.setText("CALCULAR");                                         //CALCULAR
                Glide.with(contexto).load(R.drawable.segunda_dosis).into(holder.dosis_imagen);
                holder.dosis_siguiente.setText("3ª dosis");
            }else if(vacunas.get(position).get(2).equals("3")){
                holder.fecha_siguiente.setText("CALCULAR");                                         //CALCULAR
                Glide.with(contexto).load(R.drawable.tercera_dosis).into(holder.dosis_imagen);
                holder.dosis_siguiente.setText("4ª dosis");
            }else if(vacunas.get(position).get(2).equals("4")){
                holder.fecha_siguiente.setText("CALCULAR");                                         //CALCULAR
                Glide.with(contexto).load(R.drawable.cuarta_dosis).into(holder.dosis_imagen);
                holder.dosis_siguiente.setText("Esta es la última dosis");
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
        private TextView fecha_siguiente;
        private TextView dosis;
        private TextView dosis_siguiente;
        private ImageView dosis_imagen;
        private FloatingActionButton editar;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            vacuna = v.findViewById(R.id.textView_vacuna_mascota_cliente);
            fecha = v.findViewById(R.id.textView_fecha_vacuna_mascota_cliente);
            fecha_siguiente = v.findViewById(R.id.textView_siguiente_fecha_vacuna_mascota_cliente);
            dosis = v.findViewById(R.id.textView_dosis_actual_vacuna_mascota_cliente);
            dosis_siguiente = v.findViewById(R.id.textView_siguiente_dosis_vacuna_mascota_cliente);
            dosis_imagen = v.findViewById(R.id.imageView_dosis_vacuna_mascota_cliente);
            editar = v.findViewById(R.id.floatingActionButton_editar_vacuna_mascota_cliente);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    final String id_cliente = vacunas.get(getAdapterPosition()).get(5);
                    final String id_mascota = vacunas.get(getAdapterPosition()).get(6);
                    final String id_vacuna = vacunas.get(getAdapterPosition()).get(7);

                    final CharSequence[] lista_dosis = new CharSequence[1];
                    lista_dosis[0] = dosis_siguiente.getText().toString();

                    // Fecha del momento actual
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    final String fecha_actual = mDay + "/" + mMonth + "/" + mYear;

                    // Si el numero de dosis que tiene actualmente la mascota es igual al numero de dosis totales de esa vacuna
                    if(vacunas.get(getAdapterPosition()).get(2).equals(vacunas.get(getAdapterPosition()).get(3))){
                        final String numero_dosis_siguiente = vacunas.get(getAdapterPosition()).get(3);
                        // Si el tipo de de la ultima dosis es "Anual", se mostrará en el Dialog esa misma dosis para actualizar su fecha en la BD
                        if(vacunas.get(getAdapterPosition()).get(4).contains("Anual")){
                            dialog.setTitle("\t"+"               Actualizar Vacuna \n" + "\t" + "              - " + vacunas.get(getAdapterPosition()).get(0) + " -")
                                    .setSingleChoiceItems(lista_dosis, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    }).setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").child(id_vacuna).child("dosis_"+numero_dosis_siguiente).child("fecha").setValue(fecha_actual)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent i = new Intent(v.getContext(), pantalla_vacunas_mascota_cliente.class);
                                                            i.putExtra("id_mascota", id_mascota);
                                                            i.putExtra("id_cliente", id_cliente);
                                                            context.startActivity(i);
                                                        }
                                                    });
                                        }
                                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();
                        }else{ // En caso contrario, simplemente se mostará un mensaje explicando que no existen más dosis de esa vacuna para administrar a la mascota
                            dialog.setTitle("\t"+"               Actualizar Vacuna \n" + "\t" + "              - " + vacunas.get(getAdapterPosition()).get(0) + " -")
                                    .setMessage("Esta vacuna no requiere de más dosis")
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();
                        }
                    }else { // En caso contrario, se añadirá en la BD la siguiente dosis estipulada para esa vacuna
                        final int numero_dosis_siguiente = Integer.parseInt(vacunas.get(getAdapterPosition()).get(2)) + 1;
                        dialog.setTitle("\t"+"               Actualizar Vacuna \n" + "\t" + "              - " + vacunas.get(getAdapterPosition()).get(0) + " -")
                                .setSingleChoiceItems(lista_dosis, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, String> nuevaDosis = new HashMap<>();
                                nuevaDosis.put("fecha", fecha_actual);
                                nuevaDosis.put("nombre", vacunas.get(getAdapterPosition()).get(0));
                                FirebaseDatabase.getInstance().getReference().child("usuarios").child(id_cliente).child("mascotas").child(id_mascota).child("vacunas").child(id_vacuna).child("dosis_"+numero_dosis_siguiente).setValue(nuevaDosis)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent i = new Intent(v.getContext(), pantalla_vacunas_mascota_cliente.class);
                                                i.putExtra("id_mascota", id_mascota);
                                                i.putExtra("id_cliente", id_cliente);
                                                context.startActivity(i);
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
        }
    }
}
