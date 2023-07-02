package com.example.gymv5.Fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gymv5.Activities.RutinaActivity;
import com.example.gymv5.Databases.DataBase;
import com.example.gymv5.R;

public class ClockFragment extends Fragment {

    //region Propiedades
    private EditText editTextMinutos;
    private EditText editTextSegundos;
    private Button btnIniciarCuentaAtras;
    private  Button btnReiniciar;
    public static CountDownTimer timer;
    private static DataBase datos;
    private static SQLiteDatabase db;
    private int code;
    private boolean guardarEnDb = true;
    public static int sonar;
    //Variables para el sonido
    private ImageButton silenciar;
    private SoundPool sp;
    private int sonido_de_reproduccion;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        editTextMinutos = (EditText) view.findViewById(R.id.editTextMinutos);
        editTextSegundos = (EditText) view.findViewById(R.id.editTextSegundos);
        silenciar = (ImageButton) view.findViewById(R.id.imageButtonSilenciar);

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sonido_de_reproduccion = sp.load(getContext(),R.raw.alarma, 1);

        //Asignamos nuestra base de datos a la variable
        datos = new DataBase(getContext(), "Datos", null, 1);

        //Cargar tiempo
        try {
            String respuesta = cargarTiempo();;
            if (respuesta != "") {
                Toast.makeText(getContext(), respuesta, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (sonar==1){
            silenciar.setImageResource(R.drawable.sonar);
        }else{
            silenciar.setImageResource(R.drawable.sonarsilenciado);
        }

        //region Si se modifica el tiempo en los edit text, lo guardamos en la bd
        editTextMinutos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (guardarEnDb){
                    if (editTextMinutos.getText().length()==2){
                        modificarTiempo(code, editTextMinutos.getText().toString(), editTextSegundos.getText().toString());
                        btnIniciarCuentaAtras.setEnabled(true);
                        btnReiniciar.setEnabled(false);
                    }
                }
            }
        });

        editTextSegundos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (guardarEnDb){
                    if (editTextSegundos.getText().length()==2){
                        modificarTiempo(code, editTextMinutos.getText().toString(), editTextSegundos.getText().toString());
                        btnIniciarCuentaAtras.setEnabled(true);
                        btnReiniciar.setEnabled(false);
                    }
                }
            }
        });

        //endregion

        btnReiniciar = (Button) view.findViewById(R.id.btnReiniciar);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciar();
            }
        });
        btnIniciarCuentaAtras = (Button) view.findViewById(R.id.btnIniciarCuentaAtras);
        btnIniciarCuentaAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (btnIniciarCuentaAtras.getText().toString()){
                    case "INICIAR":
                        if (editTextSegundos.getText().length() != 2 || editTextMinutos.getText().length() != 2 || Integer.valueOf(editTextMinutos.getText().toString()) + Integer.valueOf(editTextSegundos.getText().toString()) <=0 ) {
                            Toast.makeText(getContext(), "Establezca un tiempo válido", Toast.LENGTH_SHORT).show();
                        } else {
                            iniciar();
                            btnIniciarCuentaAtras.setText("PARAR");
                        }
                        break;
                    case "START":
                        if (editTextSegundos.getText().length() != 2 || editTextMinutos.getText().length() != 2 || Integer.valueOf(editTextMinutos.getText().toString()) + Integer.valueOf(editTextSegundos.getText().toString()) <=0 ) {
                            Toast.makeText(getContext(), "Set a valid time", Toast.LENGTH_SHORT).show();
                        } else {
                            iniciar();
                            btnIniciarCuentaAtras.setText("STOP");
                        }
                        break;
                    case "PARAR":
                    case "STOP":
                        parar();
                        break;
                }
            }
        });

        //Activar o desactivar sonido
        silenciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sonar==1){
                    sonar = 0;
                    silenciar.setImageResource(R.drawable.sonarsilenciado);
                    guardarSonarBd(code,sonar);
                }else{
                    sonar = 1;
                    silenciar.setImageResource(R.drawable.sonar);
                    guardarSonarBd(code,sonar);
                }
            }
        });
        return view;
    }

    //Metodo para hacer sonar el coronometro
    public void AudioSoundPool(View view){
        if (sonar==1){
            sp.play(sonido_de_reproduccion,1,1,1,0,0);
        }
    }

    private void parar(){
        if(timer != null) {
            timer.cancel();
            timer = null;
            if (btnIniciarCuentaAtras.getText().equals("PARAR")){
                btnIniciarCuentaAtras.setText("INICIAR");
            } else if (btnIniciarCuentaAtras.getText().equals("STOP")){
                btnIniciarCuentaAtras.setText("START");
            }
        }
        guardarEnDb =true;
        editTextMinutos.setEnabled(true);
        editTextSegundos.setEnabled(true);
    }

    public void reiniciar(){
        editTextMinutos.clearFocus();
        editTextSegundos.clearFocus();
        btnIniciarCuentaAtras.setEnabled(true);
        btnReiniciar.setEnabled(false);
        //Cargar tiempo
        parar();
        try {
            String respuesta = cargarTiempo();;
            if (respuesta != "") {
                Toast.makeText(getContext(), respuesta, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void iniciar() {
        guardarEnDb =false;
        btnReiniciar.setEnabled(true);
        editTextMinutos.setEnabled(false);
        editTextSegundos.setEnabled(false);
        int minuto= (Integer.valueOf(editTextMinutos.getText().toString())*60)*1000;
        int segundos= Integer.valueOf(editTextSegundos.getText().toString())*1000;
        long valor = minuto + segundos;
        timer = new CountDownTimer(valor,1000) {
            @Override
            public void onTick(long l) {
                long tiempo = l / 1000;
                int minutos = (int) (tiempo / 60);
                long segundos = tiempo % 60;
                String minutos_mostrar = String.format("%02d",minutos);
                String segundos_mostrar = String.format("%02d",segundos);
                editTextMinutos.setText(minutos_mostrar);
                editTextSegundos.setText(segundos_mostrar);
            }
            @Override
            public void onFinish() {
                AudioSoundPool(getView());
                btnIniciarCuentaAtras.setEnabled(false);
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                if (btnIniciarCuentaAtras.getText().equals("PARAR")){
                    btnIniciarCuentaAtras.setText("INICIAR");
                } else if (btnIniciarCuentaAtras.getText().equals("STOP")){
                    btnIniciarCuentaAtras.setText("START");
                }
                guardarEnDb=true;
            }
        }.start();
    }

    public String cargarTiempo() {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            //Volvemos a cargar la lista
            Cursor fila = db.rawQuery("select * from tiempo", null);
            if (fila.moveToFirst()) {
                code = fila.getInt(0);
                editTextMinutos.setText(fila.getString(1));
                editTextSegundos.setText(fila.getString(2));
                sonar = fila.getInt(3);
            }
            db.close();
            return "";
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }

    public static String modificarTiempo(int codigo, String minutos, String segundos) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();

            contentValues.put("minutos", minutos);
            contentValues.put("segundos", segundos);
            contentValues.put("sonar", sonar);

            int cantidadModificados = db.update("tiempo",contentValues,"codigo=?",new String[]{String.valueOf(codigo)});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar el tiempo";
            } else {
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }

    public static String guardarSonarBd(int codigo, int sonar) {
        //Abrimos la base de datos en modo lectura y escritura
        db = datos.getWritableDatabase();

        if (db != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("sonar", sonar);

            int cantidadModificados = db.update("tiempo",contentValues,"codigo=?",new String[]{String.valueOf(codigo)});
            if (cantidadModificados != 1) {
                db.close();
                return "No ha sido posible modificar el tiempo";
            } else {
                db.close();
                return "";
            }
        } else {
            db.close();
            return "Error al acceder a la base de datos";
        }
    }

}