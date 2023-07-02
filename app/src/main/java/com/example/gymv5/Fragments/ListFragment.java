package com.example.gymv5.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymv5.Activities.MainActivity;
import com.example.gymv5.Activities.RutinaActivity;
import com.example.gymv5.Adapters.EjercicioAdapter;
import com.example.gymv5.Models.Ejercicio;
import com.example.gymv5.R;

import java.util.Collections;

public class ListFragment extends Fragment {

    public static RecyclerView recycler;
    private AlertDialog mDialog;
    AlertDialog.Builder builder;
    public static EjercicioAdapter ejercicioAdapter;
    private static TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        //RecyclerView
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        ejercicioAdapter = new EjercicioAdapter(RutinaActivity.ejercicios);

        emptyView = (TextView) view.findViewById(R.id.empty_view);

        setEmptyView();

        ejercicioAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Editar ejercicio
                builder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_change_ejercicio, null);

                Ejercicio ejercico = RutinaActivity.ejercicios.get(recycler.getChildAdapterPosition(v));

                final EditText editTextNombre = (EditText) mView.findViewById(R.id.editTextNombre);
                final EditText editTextSeries = (EditText) mView.findViewById(R.id.editTextSeries);
                final EditText editTextRepeticiones = (EditText) mView.findViewById(R.id.editTextRepeticiones);
                final EditText editTextPeso = (EditText) mView.findViewById(R.id.editTextPeso);

                final int code = ejercico.getCodigo();
                final String seriesRestantes = ejercico.getSeriesRestantes();

                editTextNombre.setText(ejercico.getNombre());
                editTextSeries.setText(ejercico.getSeries());
                editTextRepeticiones.setText(ejercico.getRepeticiones());
                editTextPeso.setText(ejercico.getPeso());

                Button buttonModificar = (Button) mView.findViewById(R.id.buttonModificar);
                buttonModificar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextNombre.getText().toString().equals("") || editTextSeries.getText().toString().equals("")|| editTextRepeticiones.getText().toString().equals("") || editTextPeso.getText().toString().equals("")) {
                            Toast.makeText(getContext(),"Completa todos los campos",Toast.LENGTH_LONG).show();
                        }else {
                            Ejercicio updateRegistro = new Ejercicio(ejercico.getPosicion(), MainActivity.rutinaActual.getCodigo(),editTextNombre.getText().toString(),editTextSeries.getText().toString(),editTextRepeticiones.getText().toString(),editTextPeso.getText().toString(),seriesRestantes);
                            String respuesta = RutinaActivity.modificarEjercicio(code,updateRegistro);

                            if (respuesta!=""){
                                Toast.makeText(getContext(),respuesta,Toast.LENGTH_LONG).show();
                            }else {
                                mDialog.cancel();
                            }
                        }
                    }
                });
                Button buttonBorrar = (Button) mView.findViewById(R.id.buttonBorrar);
                buttonBorrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    RutinaActivity.eliminarEjercicio(ejercico.getCodigo());
                                    mDialog.cancel();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("¿Desea eliminar el ejercicio " + ejercico.getNombre()+"?").setPositiveButton("Sí", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    }
                });
                ImageButton imageButtonVolver = (ImageButton) mView.findViewById(R.id.imageButtonVolver);
                imageButtonVolver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });
                builder.setView(mView);
                mDialog = builder.create();
                mDialog.show();


            }
        });

        recycler.setAdapter(ejercicioAdapter);

        //region Drag and drop
        ItemTouchHelper helper = new ItemTouchHelper((new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        0);
            }
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = dragged.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(RutinaActivity.ejercicios, i, i + 1);

                        int order1 = RutinaActivity.ejercicios.get(i).getPosicion();
                        int order2 = RutinaActivity.ejercicios.get(i + 1).getPosicion();
                        RutinaActivity.ejercicios.get(i).setPosicion(order2);
                        RutinaActivity.ejercicios.get(i + 1).setPosicion(order1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(RutinaActivity.ejercicios, i, i - 1);
                        int order1 = RutinaActivity.ejercicios.get(i).getPosicion();
                        int order2 = RutinaActivity.ejercicios.get(i - 1).getPosicion();
                        RutinaActivity.ejercicios.get(i).setPosicion(order2);
                        RutinaActivity.ejercicios.get(i - 1).setPosicion(order1);
                    }
                }
                ejercicioAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                for (int i = 0; i < ejercicioAdapter.getItemCount(); ++i) {
                    RutinaActivity.actualizarPosicion(ejercicioAdapter.getItem(i));
                }
            }


        }));
        helper.attachToRecyclerView(recycler);
        //endregion

        return view;

    }

    public static void setEmptyView(){
        if (RutinaActivity.ejercicios.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
