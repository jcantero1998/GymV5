package com.v5.gym.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    String crearEjercicios = "CREATE TABLE ejercicios(codigo INTEGER PRIMARY KEY AUTOINCREMENT,posicion INTEGER, rutina text, nombre text, series text, repeticiones text, peso text, seriesrestantes text)";
    String crearRutinas = "CREATE TABLE rutinas(codigo INTEGER PRIMARY KEY AUTOINCREMENT, posicion INTEGER, nombre text)";
    String crearTiempo = "CREATE TABLE tiempo(codigo INTEGER PRIMARY KEY AUTOINCREMENT, minutos text, segundos text, sonar INTEGER)";

    public DataBase(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(contexto,nombre,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(crearEjercicios);
        db.execSQL(crearRutinas);
        db.execSQL(crearTiempo);
        db.execSQL("INSERT INTO tiempo(minutos, segundos, sonar) VALUES('01','30',1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int versionAnt,int versionNue)
    {
        db.execSQL("DROP TABLE IF EXISTS ejercicios");
        db.execSQL(crearEjercicios);
        db.execSQL("DROP TABLE IF EXISTS rutinas");
        db.execSQL(crearRutinas);
        db.execSQL("DROP TABLE IF EXISTS tiempo");
        db.execSQL(crearTiempo);
    }
}