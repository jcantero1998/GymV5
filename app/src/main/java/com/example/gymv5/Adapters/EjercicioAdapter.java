package com.example.gymv5.Adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymv5.Activities.RutinaActivity;
import com.example.gymv5.Models.Ejercicio;
import com.example.gymv5.Models.Rutina;
import com.example.gymv5.R;

import java.util.ArrayList;
import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.ViewHolder> implements View.OnClickListener {

    private View.OnClickListener listener;
    private List<Ejercicio> ejercicios = new ArrayList<Ejercicio>();
    private AlertDialog dialog;
    AlertDialog.Builder builder;

    public EjercicioAdapter(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ejercicio_view,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarDatos(ejercicios.get(position));
    }

    public Ejercicio getItem(int position) {
        return ejercicios.get(position);
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNombre;
        TextView textViewRepeticiones;
        TextView textViewPeso;
        Button buttonSeries;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = (TextView) itemView.findViewById(R.id.textViewNombre);
            textViewRepeticiones = (TextView) itemView.findViewById(R.id.textViewRepeticiones);
            textViewPeso = (TextView) itemView.findViewById(R.id.textViewPeso);
            buttonSeries = (Button) itemView.findViewById(R.id.buttonSeries);
        }

        public void asignarDatos(Ejercicio ejercicio) {
            textViewNombre.setText(ejercicio.getNombre());
            buttonSeries.setText(ejercicio.getSeriesRestantes());
            textViewRepeticiones.setText(ejercicio.getRepeticiones());
            textViewPeso.setText(ejercicio.getPeso());
            buttonSeries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Restar series
                    int series = Integer.parseInt(ejercicio.getSeriesRestantes());
                    if (series>0){
                        series-=1;
                        ejercicio.setSeriesRestantes(Integer.toString(series));
                        RutinaActivity.modificarSeriesEjercicio(ejercicio);
                    }else{
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        ejercicio.setSeriesRestantes(ejercicio.getSeries());
                                        RutinaActivity.modificarSeriesEjercicio(ejercicio);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("¿Desea reiniciar las series de " + ejercicio.getNombre()+"?").setPositiveButton("Sí", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }
            });
        }

    }
}
