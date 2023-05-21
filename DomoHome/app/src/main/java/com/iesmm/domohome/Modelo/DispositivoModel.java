package com.iesmm.domohome.Modelo;

import java.io.Serializable;

public class DispositivoModel implements Serializable {
    int idDispositivo;
    String nombre;
    String ip;
    String tipo;
    String marca;
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
        this.tipo = tipo;
        this.marca = marca;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
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
}
