package com.davidcurbelo.vetpetproyectodam.cliente;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davidcurbelo.vetpetproyectodam.R;

import java.util.List;

public class MyAdapterDiagnosticosHechos extends RecyclerView.Adapter<MyAdapterDiagnosticosHechos.MyViewHolder> {
    private static List<List<String>> diagnosticos;
    private List<String> diagnostico;
    private Context contexto;

    MyAdapterDiagnosticosHechos(List<List<String>> myDataset1, List<String> myDataSet2, Context context){
        contexto = context;
        diagnosticos = myDataset1;
        diagnostico = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            diagnosticos.add(i, diagnostico);
        }
    }

    @NonNull
    @Override
    public MyAdapterDiagnosticosHechos.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnostico_cardview, parent, false);
        return new MyAdapterDiagnosticosHechos.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiagnosticosHechos.MyViewHolder holder, int position) {
        holder.fecha.setText(diagnosticos.get(position).get(0));
        holder.diagnostic.setText(diagnosticos.get(position).get(1));
        holder.descripcion.setText(diagnosticos.get(position).get(2));
        holder.pruebas.setText(diagnosticos.get(position).get(3));
        holder.tratamiento.setText(diagnosticos.get(position).get(4));
    }

    @Override
    public int getItemCount() {
        return diagnosticos.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView fecha;
        private TextView diagnostic;
        private TextView descripcion;
        private TextView pruebas;
        private TextView tratamiento;
        private Button ver_tratamiento;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            fecha = v.findViewById(R.id.textView_fecha_diagnostico_mi_mascota);
            diagnostic = v.findViewById(R.id.textView_diagnostico_mi_mascota);
            descripcion = v.findViewById(R.id.textView_descripcion_diagnostico_mi_mascota);
            pruebas = v.findViewById(R.id.textView_pruebas_diagnostico_mi_mascota);
            tratamiento = v.findViewById(R.id.textView_tratamiento_diagnostico_mi_mascota);
            ver_tratamiento = v.findViewById(R.id.button_ver_tratamiento);
            ver_tratamiento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_mi_mascota_tratamientos.class);
                    String aux_nombre = diagnosticos.get(getAdapterPosition()).get(5);
                    String aux_id = diagnosticos.get(getAdapterPosition()).get(6);
                    String aux_imagen = diagnosticos.get(getAdapterPosition()).get(7);
                    intent.putExtra("id", aux_id);
                    intent.putExtra("nombre", aux_nombre);
                    intent.putExtra("imagen", aux_imagen);
                    context.startActivity(intent);
                }
            });
        }
    }
}
