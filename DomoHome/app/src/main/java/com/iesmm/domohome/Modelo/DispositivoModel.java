package com.iesmm.domohome.Modelo;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DispositivoModel implements Serializable {

    public enum Marca {
        SAMSUNG, TP_LINK;


        @NonNull
        @Override
        public String toString() {
            switch (this) {
                case SAMSUNG:
                    return "SAMSUNG";
                case TP_LINK:
                    return "TP_LINK";
            }
            return super.toString();
        }
    }
    public enum Tipo {
        TV, BOMBILLA;


        @NonNull
        @Override
        public String toString() {
            switch (this) {
                case TV:
                    return "TV";
                case BOMBILLA:
                    return "BOMBILLA";
            }
            return super.toString();
        }
    }

    int idDispositivo;
    String nombre;
    String ip;
    Tipo tipo;
    Marca marca;
    String usuarioServicio;
    String passwdServicio;
    int idUsuario;
    Boolean estado = false;

    public DispositivoModel() {
    }

    public DispositivoModel(int idDispositivo, String nombre, String ip, String tipo, String marca, String usuarioServicio, String passwdServicio, int idUsuario) {
        this.idDispositivo = idDispositivo;
        this.nombre = nombre;
        this.ip = ip;
        this.tipo = StringToTipo(tipo);
        this.marca = StringToMarca(marca);
        this.usuarioServicio = usuarioServicio;
        this.passwdServicio = passwdServicio;
        this.idUsuario = idUsuario;
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = StringToTipo(tipo);
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = StringToMarca(marca);
    }

    public String getUsuarioServicio() {
        return usuarioServicio;
    }

    public void setUsuarioServicio(String usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    public String getPasswdServicio() {
        return passwdServicio;
    }

    public void setPasswdServicio(String passwdServicio) {
        this.passwdServicio = passwdServicio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Tipo StringToTipo(String tipo){
        switch (tipo.toUpperCase()){
            case "TV":
                return Tipo.TV;
            case "BOMBILLA":
                return Tipo.BOMBILLA;
            default:
                return null;
        }
    }

    public Marca StringToMarca(String marca){
        switch (marca.toUpperCase()){
            case "SAMSUNG":
                return Marca.SAMSUNG;
            case "TP_LINK":
                return Marca.TP_LINK;
            default:
                return null;
        }
    }

}
