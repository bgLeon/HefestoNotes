package com.hefesto.bg.hefestonotes;

import java.io.Serializable;

/**
 * Created by Borja on 11/08/2015.
 */
public class Nota implements Serializable {
    private static final long serialVersionUID = 4349879151820234260L;
    private String Titulo;
    private String Contenido;
    private String Categoria;
    private boolean Cifrado;
    public static final String NOTA = "Nota";

    public Nota(String titulo, String contenido, String categoria,
                boolean cifrado) {
        this.Titulo = titulo;
        this.Contenido = contenido;
        this.Categoria = categoria;
        this.Cifrado = cifrado;
    }

    public boolean isCifrado() {
        return Cifrado;
    }

    public void setCifrado(boolean cifrado) {
        this.Cifrado = cifrado;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        this.Titulo = titulo;
    }

    public String getContenido() {
        return Contenido;
    }

    public void setContenido(String contenido) {
        this.Contenido = contenido;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        this.Categoria = categoria;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (Cifrado ? 1231 : 1237);
        result = prime * result
                + ((Contenido == null) ? 0 : Contenido.hashCode());
        result = prime * result
                + ((Categoria == null) ? 0 : Categoria.hashCode());
        result = prime * result + ((Titulo == null) ? 0 : Titulo.hashCode());
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
        Nota other = (Nota) obj;
        if (Cifrado != other.Cifrado)
            return false;
        if (Contenido == null) {
            if (other.Contenido != null)
                return false;
        } else if (!Contenido.equals(other.Contenido))
            return false;
        if (Categoria == null) {
            if (other.Categoria != null)
                return false;
        } else if (!Categoria.equals(other.Categoria))
            return false;
        if (Titulo == null) {
            if (other.Titulo != null)
                return false;
        } else if (!Titulo.equals(other.Titulo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Nota [titulo=" + Titulo + ", contenido=" + Contenido
                + ", categoria=" + Categoria + ", cifrado=" + Cifrado + "]";
    }

}
