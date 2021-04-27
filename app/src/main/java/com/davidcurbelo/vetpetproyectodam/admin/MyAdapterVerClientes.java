package com.davidcurbelo.vetpetproyectodam.admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterVerClientes extends RecyclerView.Adapter<MyAdapterVerClientes.MyViewHolder> {
    private static List<List<String>> clientes;
    private List<String> cliente;
    private Context contexto;

    MyAdapterVerClientes(List<List<String>> myDataset1, List<String> myDataSet2, Context context) {
        contexto = context;
        clientes = myDataset1;
        cliente = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            clientes.add(i, cliente);
        }
    }

    @NonNull
    @Override
    public MyAdapterVerClientes.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_clientes_cardview, parent, false);
        return new MyAdapterVerClientes.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterVerClientes.MyViewHolder holder, int position) {
        holder.dni.setText(clientes.get(position).get(0));
        holder.nombre_completo.setText(clientes.get(position).get(1));
        holder.mascotas.setText(clientes.get(position).get(2).concat(" Mascota/s"));
        if(clientes.get(position).get(3).equalsIgnoreCase("Hombre")){
            Glide.with(contexto).load(R.drawable.genero_hombre).into(holder.imagen);
        }else if(clientes.get(position).get(3).equalsIgnoreCase("Mujer")){
            Glide.with(contexto).load(R.drawable.genero_mujer).into(holder.imagen);
        }
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView dni;
        private TextView nombre_completo;
        private TextView mascotas;
        private ImageView imagen;
        private Button ver_mascotas;
        private Button ver_cliente;
        private Button ver_consultas;
        private Button ver_citas;
        private ImageButton llamada;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            dni = v.findViewById(R.id.textView_dni_cliente);
            nombre_completo = v.findViewById(R.id.textView_nombre_cliente);
            mascotas = v.findViewById(R.id.textView_numero_mascotas_cliente);
            imagen = v.findViewById(R.id.imageView_cliente);
            ver_mascotas = v.findViewById(R.id.button_ver_mascotas_cliente);
            ver_mascotas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_ver_mascotas_cliente.class);
                    String id_cliente = clientes.get(getAdapterPosition()).get(5);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            ver_cliente = v.findViewById(R.id.button_ver_cliente);
            ver_cliente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_ver_cliente.class);
                    String id_cliente = clientes.get(getAdapterPosition()).get(5);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            ver_consultas = v.findViewById(R.id.button_ver_consultas_cliente);
            ver_consultas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_ver_consultas_cliente.class);
                    String id_cliente = clientes.get(getAdapterPosition()).get(5);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            ver_citas = v.findViewById(R.id.button_ver_citas_cliente);
            ver_citas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_ver_citas_cliente.class);
                    String id_cliente = clientes.get(getAdapterPosition()).get(5);
                    intent.putExtra("id_cliente", id_cliente);
                    context.startActivity(intent);
                }
            });
            llamada = v.findViewById(R.id.imageButton_llamar_cliente);
            llamada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + clientes.get(getAdapterPosition()).get(4))));
                }
            });
        }
    }
}
