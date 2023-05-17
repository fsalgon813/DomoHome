package com.iesmm.domohome.Modelo;

public class CasaModel {
    private int idCasa;
    private String nombre;
    private String codInvitacion;

    public CasaModel() {
    }

    public CasaModel(int idCasa, String nombre, String codInvitacion) {
        this.idCasa = idCasa;
        this.nombre = nombre;
        this.codInvitacion = codInvitacion;
    }

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodInvitacion() {
        return codInvitacion;
    }

    public void setCodInvitacion(String codInvitacion) {
        this.codInvitacion = codInvitacion;
    }
}
