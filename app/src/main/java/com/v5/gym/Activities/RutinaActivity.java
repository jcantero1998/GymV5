package com.v5.gym.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.v5.gym.Adapters.PagerAdapter;
import com.v5.gym.Databases.DataBase;
import com.v5.gym.Fragments.ClockFragment;
import com.v5.gym.Fragments.ListFragment;
import com.v5.gym.Models.Ejercicio;
import com.v5.gym.Models.Rutina;
import com.v5.gym.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RutinaActivity extends AppCompatActivity {

    //region Propiedades
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private static DataBase datos;
    private static SQLiteDatabase db;
    public static List<Ejercicio> ejercicios = new ArrayList<Ejercicio>();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);
        //region Base de datos
        //Asignamos nuestra base de datos a la variable
        datos = new DataBase(this, "Datos", null, 1);


        //Cargar la lista de la base de datos
        try {
            String respuesta = cargarEjercicios();
            if (respuesta != "") {
                Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //endregion

        setupToolBar();
        setTabLayout();
        setViewPager();
        setListenerTabLayout(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        if (ClockFragment.timer!=null){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ClockFragment.timer.cancel();
                            ClockFragment.timer = null;
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(RutinaActivity.this);
            builder.setMessage("La cuenta atrás se parará ¿Desea salir?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }else{
            finish();
        }
    }
    //region ToolBar, Context menu, TabLayout y ViewPager
    private void setupToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(MainActivity.rutinaActual.getNombre().toUpperCase());
        myToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(RutinaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_change_rutina, null);
                ImageButton imageButtonVolver = (ImageButton) mView.findViewById(R.id.imageButtonVolver);
                imageButtonVolver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                final EditText editTextNombre = (EditText) mView.findViewById(R.id.editTextNombre);
                editTextNombre.setText(getSupportActionBar().getTitle());

                Button buttonModificar = (Button) mView.findViewById(R.id.buttonRenombrar);
                buttonModificar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextNombre.getText().toString().equals("")) {
                            Toast.makeText(RutinaActivity.this,"Completa todos los campos",Toast.LENGTH_LONG).show();
                        }else {
                            String respuesta = MainActivity.renameRutina( MainActivity.rutinaActual,editTextNombre.getText().toString().toUpperCase());
                            if (respuesta!=""){
                                Toast.makeText(RutinaActivity.this,respuesta,Toast.LENGTH_LONG).show();
                            }else {
                                getSupportActionBar().setTitle(editTextNombre.getText().toString().toUpperCase());
                                dialog.cancel();
                            }
                        }
                    }
                });
                builder.setView(mView);
                dialog = builder.create();
                dialog.show();
            }
        });
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rutina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.restart_item) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Si hay ejercicios, creamos una nueva lista temporal y le pasamops los ejercicios (Esto se hace pata que no se rompa al modificar un ejercicio en la misma lista que estamos recorriendo)
                            if (ejercicios.size()>0){
                                List<Ejercicio> ejerciciostmp = new ArrayList<Ejercicio>();
                                for (Ejercicio ejercicio:ejercicios) {
                                    ejerciciostmp.add(ejercicio);
                                }
                                //Recorremos los ejercicios de la lista temporal, y modificamos las series con el metodo "modificarSeriesEjercicio()"
                                for (Ejercicio ejercicio:ejerciciostmp) {
                                    ejercicio.setSeriesRestantes(ejercicio.getSeries());
                                    modificarEjercicio(ejercicio.getCodigo(),ejercicio);
                                }
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(RutinaActivity.this);
            builder.setMessage("¿Desea reiniciar las series de todos los ejercicios?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        }else if (id == R.id.add_item){
            builder = new AlertDialog.Builder(RutinaActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_ejercicio, null);
            final EditText editTextNombre = (EditText) mView.findViewById(R.id.editTextNombre);
            final EditText editTextPeso = (EditText) mView.findViewById(R.id.editTextPeso);
            final EditText editTextRepeticiones = (EditText) mView.findViewById(R.id.editTextRepeticiones);
            final EditText editTextSeries = (EditText) mView.findViewById(R.id.editTextSeries);
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
                    if (editTextNombre.getText().toString().equals("") || editTextPeso.getText().toString().equals("")|| editTextRepeticiones.getText().toString().equals("") || editTextSeries.getText().toString().equals("")) {
                        Toast.makeText(RutinaActivity.this,"Completa todos los campos",Toast.LENGTH_LONG).show();
                    }else {
                        Ejercicio addRegistro = new Ejercicio(calcularPosicionEjercicio(),MainActivity.rutinaActual.getCodigo(),editTextNombre.getText().toString().toUpperCase(),editTextSeries.getText().toString(),editTextRepeticiones.getText().toString(),editTextPeso.getText().toString(),editTextSeries.getText().toString());

                        String respuesta = RutinaActivity.agregarEjercicio(addRegistro);
                        if (respuesta!=""){
                            Toast.makeText(RutinaActivity.this,respuesta,Toast.LENGTH_LONG).show();
                        }else {
                            //Borramos los campos de los edit text
                            editTextNombre.setText("");
                            editTextPeso.setText("");
                            editTextRepeticiones.setText("");
                            editTextSeries.setText("");
                        }
                    }
                }
            });
            builder.setView(mView);
            dialog = builder.create();
            dialog.show();
            return true;
        }else if (id == R.id.delete_item){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            eliminarEjercicioRutina(MainActivity.rutinaActual);
                            MainActivity.eliminarRutina(MainActivity.rutinaActual.getCodigo());
                            MainActivity.setEmptyView();
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(RutinaActivity.this);
            builder.setMessage("¿Desea eliminar la rutina "+MainActivity.rutinaActual.getNombre().toUpperCase()+"?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Metodo que calcula la posicion que llevará el ejercicio en el adapter
    private int calcularPosicionEjercicio() {

        if (ejercicios.size()==0){
            return 0;
        }else{
            return ejercicios.get(ejercicios.size()-1).getPosicion()+1;
        }
    }
    private void setTabLayout() {
        // Instancio los objetos tabLayout y ViewPager declarados en el layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        // Añado al tabLayout las pestañas-tabs
        tabLayout.addTab(tabLayout.newTab().setText(R.string.ejercicios));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.descanso));
    }

    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        // En el ViewPager tenemos que crear la clase
        // Instanciamos el PAgerAdapter, hay que tener en cuenta que haga el import
        // de nuestra clase, en vez de una del android
        // El número de tabs en vez de pasar un 3 en este caso le pasamos el getTabCount para que sea
        // más dinámico
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        // Asignamos el adaptador al ViewPager
        viewPager.setAdapter(adapter);
        // Configuramos el listener para que esté escuchando cada vez que cambiamos
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setListenerTabLayout(final ViewPager viewPager) {
        // Definimos que hay que hacer cuándo cambiemos de un tab a otro
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Cuando seleccionamos el tab
                // Toast.makeText(MainActivity.this, "Selected: " + tab.getText(),Toast.LENGTH_SHORT).show();
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Cuando el tab que está activo deja de estarlo
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Cuándo seleccionamos el mismo tab que está activo
            }
        });
    }
    //endregion

    //region CRUD
    public static String agregarEjercicio(Ejercicio addEjercicio) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("posicion", addEjercicio.getPosicion());
            contentValues.put("rutina", addEjercicio.getRutina());
            contentValues.put("nombre", addEjercicio.getNombre().toUpperCase());
            contentValues.put("series", addEjercicio.getSeries());
            contentValues.put("repeticiones", addEjercicio.getRepeticiones());
            contentValues.put("peso", addEjercicio.getPeso());
            contentValues.put("seriesrestantes", addEjercicio.getSeriesRestantes());

            if (db.insert("ejercicios", null, contentValues) == -1) {
                db.close();
                return "Error al añadir el ejercicio";
            } else {
                //Cargamos la lista
                cargarEjercicios();
                //Notificamos el cambio a los adapters
                ListFragment.ejercicioAdapter.notifyDataSetChanged();
                if (ejercicios.size()==1) {
                    ListFragment.setEmptyView();
                }
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }

    public static String modificarEjercicio(int codigo, Ejercicio updateEjercicio) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("posicion", updateEjercicio.getPosicion());
            contentValues.put("rutina", updateEjercicio.getRutina());
            contentValues.put("nombre", updateEjercicio.getNombre().toUpperCase());
            contentValues.put("series", updateEjercicio.getSeries());
            contentValues.put("repeticiones", updateEjercicio.getRepeticiones());
            contentValues.put("peso", updateEjercicio.getPeso());
            contentValues.put("seriesrestantes", updateEjercicio.getSeriesRestantes());


            int cantidadModificados = db.update("ejercicios",contentValues,"codigo=?",new String[]{String.valueOf(codigo)});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar el ejercicio";
            } else {
                //Volvemos a cargar la lista
                cargarEjercicios();
                //Notificamos el cambio a los adapters
                ListFragment.ejercicioAdapter.notifyDataSetChanged();
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }

    public static String modificarSeriesEjercicio(Ejercicio updateEjercicio) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("posicion", updateEjercicio.getPosicion());
            contentValues.put("rutina", updateEjercicio.getRutina());
            contentValues.put("nombre", updateEjercicio.getNombre().toUpperCase());
            contentValues.put("series", updateEjercicio.getSeries());
            contentValues.put("repeticiones", updateEjercicio.getRepeticiones());
            contentValues.put("peso", updateEjercicio.getPeso());
            contentValues.put("seriesrestantes", updateEjercicio.getSeriesRestantes());


            int cantidadModificados = db.update("ejercicios",contentValues,"codigo=?",new String[]{String.valueOf(updateEjercicio.getCodigo())});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar el ejercicio";
            } else {
                //Volvemos a cargar la lista
                cargarEjercicios();
                //Notificamos el cambio a los adapters
                ListFragment.ejercicioAdapter.notifyDataSetChanged();
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }

    public static String actualizarPosicion(Ejercicio ejercicio) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("posicion", ejercicio.getPosicion());

            int cantidadModificados = db.update("ejercicios",contentValues,"codigo=?",new String[]{String.valueOf(ejercicio.getCodigo())});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar el ejercicio";
            } else {
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }

    }

    public static String eliminarEjercicio(int codigo) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            int cantidadBorrados = db.delete("ejercicios", "codigo=?", new String[]{String.valueOf(codigo)});
            if (cantidadBorrados != 1) {
                db.close();
                return "No ha sido posible eliminar el ejercicio";
            } else {
                //Volvemos a cargar la lista
                cargarEjercicios();

               //Actualizamos las posiciones de los ejercicios de la lista
                actualizarPosicionEjercicios();

                //Notificamos el cambio a los adapters
                ListFragment.ejercicioAdapter.notifyDataSetChanged();
                db.close();

                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }
    public static void actualizarPosicionEjercicios(){
        //Actualizamos las posiciones de los ejercicios de la lista
        for (int i = 0; i < ejercicios.size(); i++) {
            ejercicios.get(i).setPosicion(i);
            RutinaActivity.actualizarPosicion(ejercicios.get(i));
        }
    }

    public static String eliminarEjercicioRutina(Rutina rutina) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            int cantidadBorrados = db.delete("ejercicios", "codigo=?",new String[]{String.valueOf(rutina.getCodigo())});
            if (cantidadBorrados < 1) {
                db.close();
                return "No ha sido posible eliminar el ejercicio";
            } else {
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }

    public static String cargarEjercicios() {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();
        if (db != null) {
            //Vaciamos la lista
            ejercicios.clear();

            //Volvemos a cargar la lista
            Cursor fila = db.rawQuery("select * from ejercicios where rutina = ? order by ejercicios.posicion asc", new String[]{String.valueOf(MainActivity.rutinaActual.getCodigo())});
            if (fila.moveToFirst()) {
                do {
                    ejercicios.add(new Ejercicio(fila.getInt(0),fila.getInt(1),fila.getInt(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6),fila.getString(7)));
                } while (fila.moveToNext());
            }
            db.close();
            return "";
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }
    //endregion

}