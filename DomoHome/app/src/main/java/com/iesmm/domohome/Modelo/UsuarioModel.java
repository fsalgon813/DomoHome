package com.iesmm.domohome.Modelo;

import java.io.Serializable;

public class UsuarioModel implements Serializable {
    private int id;
    private String nombre;
    private String username;
    private String password;
    private int id_casa;

    public UsuarioModel() {
    }

    public UsuarioModel(int id, String nombre, String username, String password, int id_casa) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.id_casa = id_casa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId_casa() {
        return id_casa;
    }

    public void setId_casa(int id_casa) {
        this.id_casa = id_casa;
    }
}

