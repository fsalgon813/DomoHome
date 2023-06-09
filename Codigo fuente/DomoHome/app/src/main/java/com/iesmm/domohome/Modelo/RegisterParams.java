package com.iesmm.domohome.Modelo;

import java.io.Serializable;

public class RegisterParams implements Serializable {
    private String username;
    private String passwd;
    private String nombre;
    private String nombreCasa;
    private String codInvitacion;

    public RegisterParams() {
    }

    public RegisterParams(String username, String passwd, String nombre, String nombreCasa, String codInvitacion) {
        this.username = username;
        this.passwd = passwd;
        this.nombre = nombre;
        this.nombreCasa = nombreCasa;
        this.codInvitacion = codInvitacion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCasa() {
        return nombreCasa;
    }

    public void setNombreCasa(String nombreCasa) {
        this.nombreCasa = nombreCasa;
    }

    public String getCodInvitacion() {
        return codInvitacion;
    }

    public void setCodInvitacion(String codInvitacion) {
        this.codInvitacion = codInvitacion;
    }
}
