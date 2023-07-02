package com.v5.gym.Models;

public class Ejercicio {
    private int codigo;
    private int posicion;
    private int rutina;
    private String nombre;
    private String series;
    private String repeticiones;
    private String peso;
    private String seriesRestantes;

    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public int getPosicion() {
        return posicion;
    }
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    public int getRutina() {
        return rutina;
    }
    public void setRutina(int rutina) {
        this.rutina = rutina;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }
    public String getRepeticiones() {
        return repeticiones;
    }
    public void setRepeticiones(String repeticiones) {
        this.repeticiones = repeticiones;
    }
    public String getPeso() {
        return peso;
    }
    public void setPeso(String peso) {
        this.peso = peso;
    }
    public String getSeriesRestantes() {
        return seriesRestantes;
    }
    public void setSeriesRestantes(String seriesRestantes) {
        this.seriesRestantes = seriesRestantes;
    }

    public Ejercicio() {
        super();
    }
    public Ejercicio(int posicion, int rutina, String nombre, String series, String repeticiones,
                     String peso, String seriesRestantes) {
        super();
        this.posicion = posicion;
        this.rutina = rutina;
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.seriesRestantes = seriesRestantes;
    }
    public Ejercicio(int codigo, int posicion, int rutina, String nombre, String series,
                     String repeticiones, String peso, String seriesRestantes) {
        super();
        this.codigo = codigo;
        this.posicion = posicion;
        this.rutina = rutina;
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.seriesRestantes = seriesRestantes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + codigo;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ejercicio other = (Ejercicio) obj;
        if (codigo != other.codigo)
            return false;
        return true;
    }

}