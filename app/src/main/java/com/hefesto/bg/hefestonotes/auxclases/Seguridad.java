package com.hefesto.bg.hefestonotes.auxclases;

/**
 * Created by Borja on 20/08/2015.
 * bean de password
 */
public class Seguridad {
    private String Contraseña;

    public Seguridad(String contraseña){
        this.Contraseña=contraseña;
    }

    public void setContraseña(String contraseña) {
        this.Contraseña=contraseña;
    }

    public String getContraseña() {
        return Contraseña;
    }
}
