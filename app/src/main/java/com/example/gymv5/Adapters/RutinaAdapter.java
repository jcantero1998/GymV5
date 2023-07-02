package com.example.gymv5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymv5.Models.Rutina;
import com.example.gymv5.R;

import java.util.ArrayList;
import java.util.List;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.ViewHolder> implements View.OnClickListener {

    private View.OnClickListener listener;
    private List<Rutina> rutinas = new ArrayList<Rutina>();
    private AlertDialog dialog;
    AlertDialog.Builder builder;

    public RutinaAdapter(List<Rutina> rutinas) {
        this.rutinas = rutinas;
    }

    @NonNull
    @Override
    public RutinaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rutina_view,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaAdapter.ViewHolder holder, int position) {
        holder.asignarDatos(rutinas.get(position));
    }

    public Rutina getItem(int position) {
        return rutinas.get(position);
    }

    @Override
    public int getItemCount() {
        return rutinas.size();
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

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void asignarDatos(Rutina rutina) {
            textView.setText(rutina.getNombre());
        }
    }
}