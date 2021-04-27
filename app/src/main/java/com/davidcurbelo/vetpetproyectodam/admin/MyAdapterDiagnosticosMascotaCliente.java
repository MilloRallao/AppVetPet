package com.davidcurbelo.vetpetproyectodam.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davidcurbelo.vetpetproyectodam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class MyAdapterDiagnosticosMascotaCliente extends RecyclerView.Adapter<MyAdapterDiagnosticosMascotaCliente.MyViewHolder> {
    private static List<List<String>> diagnosticos;
    private List<String> diagnostico;
    private Context contexto;

    MyAdapterDiagnosticosMascotaCliente(List<List<String>> myDataset1, List<String> myDataSet2, Context context){
        contexto = context;
        diagnosticos = myDataset1;
        diagnostico = myDataSet2;
        for (int i = 0; i < getItemCount(); i++) {
            diagnosticos.add(i, diagnostico);
        }
    }

    @NonNull
    @Override
    public MyAdapterDiagnosticosMascotaCliente.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnostico_cardview, parent, false);
        return new MyAdapterDiagnosticosMascotaCliente.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDiagnosticosMascotaCliente.MyViewHolder holder, int position) {
        holder.fecha.setText(diagnosticos.get(position).get(0));
        holder.diagnostico.setText(diagnosticos.get(position).get(1));
        holder.texto_descripcion.setText("Anamnesis:");
        holder.descripcion.setText(diagnosticos.get(position).get(2));
        holder.pruebas.setText(diagnosticos.get(position).get(3));
        holder.tratamiento.setText(diagnosticos.get(position).get(4));
        holder.editar.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return diagnosticos.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private TextView fecha;
        private TextView diagnostico;
        private TextView texto_descripcion;
        private TextView descripcion;
        private TextView pruebas;
        private TextView tratamiento;
        private Button ver_tratamiento;
        private FloatingActionButton editar;
        MyViewHolder(View v) {
            super(v);
            context = itemView.getContext();
            fecha = v.findViewById(R.id.textView_fecha_diagnostico_mi_mascota);
            diagnostico = v.findViewById(R.id.textView_diagnostico_mi_mascota);
            texto_descripcion = v.findViewById(R.id.textView_texto_descripcion_diagnostico_mi_mascota);
            descripcion = v.findViewById(R.id.textView_descripcion_diagnostico_mi_mascota);
            pruebas = v.findViewById(R.id.textView_pruebas_diagnostico_mi_mascota);
            tratamiento = v.findViewById(R.id.textView_tratamiento_diagnostico_mi_mascota);
            ver_tratamiento = v.findViewById(R.id.button_ver_tratamiento);
            ver_tratamiento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id_diagnostico = diagnosticos.get(getAdapterPosition()).get(7);
                    String id_clinica = diagnosticos.get(getAdapterPosition()).get(8);
                    // Crear el Dialog
                    final Dialog dialog = new Dialog(context);
                    // Asignar diseño de layout al Dialog
                    dialog.setContentView(R.layout.ver_tratamiento_diagnosticos);
                    // Elementos del Dialog
                    final TextView fecha_inicio = dialog.findViewById(R.id.textView_fecha_inicio_tratamiento_diagnostico_agregar_diagnostico_tratamiento);
                    final TextView fecha_fin = dialog.findViewById(R.id.textView_fecha_fin_tratamiento_diagnostico_agregar_diagnostico_tratamiento);
                    final TextView farmaco = dialog.findViewById(R.id.textView_farmaco_tratamiento_diagnostico_agregar_diagnostico_tratamiento);
                    final TextView dosis = dialog.findViewById(R.id.textView_dosis_tratamiento_agregar_diagnostico_tratamiento);
                    final TextView forma_administracion = dialog.findViewById(R.id.textView_forma_administracion_tratamiento_diagnostico_agregar_diagnostico_tratamiento);
                    final ImageView icono = dialog.findViewById(R.id.imageView_icono_tratamiento_diagnostico_agregar_diagnostico_tratamiento);
                    // Referencia al diagnóstico para sacar el ID del tratamiento
                    final DatabaseReference ref_diagnostico = FirebaseDatabase.getInstance().getReference().child("diagnosticos").child(id_clinica).child(id_diagnostico);
                    ref_diagnostico.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Referencia al cliente para sacar los datos del tratamiento
                            DatabaseReference ref_tratamiento = ref_diagnostico.child("tratamiento");
                            ref_tratamiento.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    fecha_inicio.setText(data.get("fecha_inicio").toString());
                                    fecha_fin.setText(data.get("fecha_fin").toString());
                                    farmaco.setText(data.get("farmaco").toString());
                                    dosis.setText(data.get("dosis").toString());
                                    forma_administracion.setText(data.get("administracion").toString());
                                    if(data.get("fecha_fin").toString().equalsIgnoreCase("Crónico") || data.get("fecha_fin").toString().equalsIgnoreCase("Sin fecha")){
                                        icono.setVisibility(View.VISIBLE);
                                        fecha_fin.setTextColor(context.getResources().getColor(R.color.colorcancel));
                                        fecha_fin.setTypeface(null, Typeface.BOLD);
                                    }
                                    dialog.show();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERROR0", "onCancelled: "+databaseError);
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ERROR1", "onCancelled: "+databaseError);
                        }
                    });
                }
            });
            editar = v.findViewById(R.id.floatingActionButton_editar_diagnostico);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, pantalla_agregar_diagnostico_tratamiento.class);
                    intent.putExtra("id_mascota", diagnosticos.get(getAdapterPosition()).get(5));
                    intent.putExtra("id_cliente", diagnosticos.get(getAdapterPosition()).get(6));
                    intent.putExtra("agregar_actualizar", 1);
                    intent.putExtra("id_diagnostico", diagnosticos.get(getAdapterPosition()).get(7));
                    context.startActivity(intent);
                }
            });
        }
    }
}
