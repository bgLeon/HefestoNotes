package com.hefesto.bg.hefestonotes.auxclases;

import java.io.Serializable;

/**
 * Created by Borja on 20/08/2015.
 * bean de seguridad
 */
public class Seguridad implements Serializable {
    private static final long serialVersionUID = 4349879151820234261L;

    public static final String SEGURIDAD = "Seguridad";
    private String Contraseña;
    private String Pregunta;
    private String Respuesta;

    public Seguridad(String contraseña, String pregunta, String respuesta){
        this.Contraseña=contraseña;
        this.Pregunta=pregunta;
        this.Respuesta=respuesta;
    }

    public void setContraseña(String contraseña) {
        this.Contraseña=contraseña;
    }

    public String getContraseña() {
        return Contraseña;
    }
    public void setPregunta(String pregunta) {
        this.Pregunta=pregunta;
    }

    public String getPregunta() {
        return Pregunta;
    }
    public void setRespuesta(String respuesta) {
        this.Respuesta=respuesta;
    }

    public String getRespuesta() {
        return Respuesta;
    }
}
