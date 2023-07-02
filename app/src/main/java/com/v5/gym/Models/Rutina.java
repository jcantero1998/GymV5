package com.v5.gym.Models;

public class Rutina {

    private int codigo;
    private int posicion;
    private String nombre;

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
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rutina() {
        super();
    }
    public Rutina(int posicion, String nombre) {
        super();
        this.posicion = posicion;
        this.nombre = nombre;
    }
    public Rutina(int codigo, int posicion, String nombre) {
        super();
        this.codigo = codigo;
        this.posicion = posicion;
        this.nombre = nombre;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
        Rutina other = (Rutina) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        return true;
    }

}