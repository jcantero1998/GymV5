package com.example.gymv5.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymv5.Adapters.EjercicioAdapter;
import com.example.gymv5.Adapters.RutinaAdapter;
import com.example.gymv5.Databases.DataBase;
import com.example.gymv5.Models.Ejercicio;
import com.example.gymv5.Models.Rutina;
import com.example.gymv5.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    //region Propiedades
    public static List<Rutina> rutinas = new ArrayList<Rutina>();
    public static RecyclerView recycler;
    public static RutinaAdapter rutinaAdapter;
    private static DataBase datos;
    private static SQLiteDatabase db;
    public static Rutina rutinaActual;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private static TextView emptyView;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datos = new DataBase(this, "Datos", null, 1);

        //region Cargar la lista de rutinas de la base de datos
        try {
            String respuesta = cargarRurinas();
            if (respuesta != "") {
                Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //endregion

        //RecyclerView
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
        rutinaAdapter = new RutinaAdapter(rutinas);

        emptyView = (TextView) findViewById(R.id.empty_view);

        setEmptyView();

        rutinaAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rutinaActual = rutinas.get(recycler.getChildAdapterPosition(v));
                Intent intent = new Intent(MainActivity.this, RutinaActivity.class);
                startActivity(intent);
            }
        });
        recycler.setAdapter(rutinaAdapter);

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
                        Collections.swap(rutinas, i, i + 1);

                        int order1 = rutinas.get(i).getPosicion();
                        int order2 = rutinas.get(i + 1).getPosicion();
                        rutinas.get(i).setPosicion(order2);
                        rutinas.get(i + 1).setPosicion(order1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(rutinas, i, i - 1);
                        int order1 = rutinas.get(i).getPosicion();
                        int order2 = rutinas.get(i - 1).getPosicion();
                        rutinas.get(i).setPosicion(order2);
                        rutinas.get(i - 1).setPosicion(order1);
                    }
                }
                rutinaAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                for (int i = 0; i < rutinaAdapter.getItemCount(); ++i) {
                    actualizarPosicion(rutinaAdapter.getItem(i));
                }
            }
        }));
        helper.attachToRecyclerView(recycler);
        //endregion

        //region btnAñadir
        Button btnAñadir = findViewById(R.id.btnAñadir);
        btnAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_rutina, null);
                final EditText editTextNombre = (EditText) mView.findViewById(R.id.editTextNombre);
                ImageButton imageButtonVolver = (ImageButton) mView.findViewById(R.id.imageButtonVolver);
                imageButtonVolver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                Button buttonAgregar = (Button) mView.findViewById(R.id.buttonAgregar);
                buttonAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextNombre.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this,"Escribe un nombre para la rutina",Toast.LENGTH_LONG).show();
                        }else {
                            Rutina addRutina = new Rutina(calcularPosicionRutina(), editTextNombre.getText().toString());

                            String respuesta = agregarRutina(addRutina);
                            if (respuesta!=""){
                                Toast.makeText(MainActivity.this,respuesta,Toast.LENGTH_LONG).show();
                            }else {
                                rutinaActual = rutinas.get(rutinas.size()-1);
                                setEmptyView();
                                dialog.cancel();
                                Intent intent = new Intent(MainActivity.this, RutinaActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                });
                builder.setView(mView);
                dialog = builder.create();
                dialog.show();
            }
        });
        //endregion
    }

    //Metodo que calcula la posicion que llevará la rutina en el adapter
    private int calcularPosicionRutina() {

        if (rutinas.size()==0){
            return 0;
        }else{
            return rutinas.get(rutinas.size()-1).getPosicion()+1;
        }
    }

    public static void setEmptyView(){
        if (MainActivity.rutinas.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //region CRUD
    public static String cargarRurinas() {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            //Vaciamos la lista
            rutinas.clear();

            //Volvemos a cargar la lista
            Cursor fila = db.rawQuery("select * from rutinas order by rutinas.posicion asc", null);
            if (fila.moveToFirst()) {
                do {
                    rutinas.add(new Rutina(fila.getInt(0),fila.getInt(1),fila.getString(2)));
                } while (fila.moveToNext());
            }
            db.close();
            return "";
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }
    public static String agregarRutina(Rutina addRutina) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues registro = new ContentValues();

            registro.put("nombre", addRutina.getNombre().toUpperCase());
            registro.put("posicion", addRutina.getPosicion());

            if (db.insert("rutinas", null, registro) == -1) {
                db.close();
                return "Error al añadir la rutina";
            } else {
                //Cargamos la lista
                cargarRurinas();
                //Notificamos el cambio a los adapters
                rutinaAdapter.notifyDataSetChanged();
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }
    public static String renameRutina(Rutina rutina, String nombreDeseado) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("nombre", nombreDeseado.toUpperCase());

            int cantidadModificados = db.update("rutinas",contentValues,"codigo=?",new String[]{String.valueOf(rutina.getCodigo())});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar la rutina";
            } else {
                //Volvemos a cargar la lista
                cargarRurinas();
                //Notificamos el cambio a los adapters
                rutinaAdapter.notifyDataSetChanged();
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }
    public static String actualizarPosicion(Rutina rutina) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("posicion", rutina.getPosicion());

            int cantidadModificados = db.update("rutinas",contentValues,"codigo=?",new String[]{String.valueOf(rutina.getCodigo())});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar la rutina";
            } else {
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }
    public static String eliminarRutina(int codigo) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            int cantidadBorrados = db.delete("rutinas", "codigo=?", new String[]{String.valueOf(codigo)});
            if (cantidadBorrados != 1) {
                db.close();
                return "No ha sido posible eliminar la rutina";
            } else {
                //Volvemos a cargar la lista
                cargarRurinas();

                //Actualizamos las posiciones de los ejercicios de la lista
                actualizarPosicionRutinas();

                //Notificamos el cambio a los adapters
                rutinaAdapter.notifyDataSetChanged();
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }
    public static void actualizarPosicionRutinas(){
        //Actualizamos las posiciones de los ejercicios de la lista
        for (int i = 0; i < rutinas.size(); i++) {
            rutinas.get(i).setPosicion(i);
           actualizarPosicion(rutinas.get(i));
        }
    }
    //endregion

}