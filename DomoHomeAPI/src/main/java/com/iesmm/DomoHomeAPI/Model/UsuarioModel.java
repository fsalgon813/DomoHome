package com.iesmm.DomoHomeAPI.Model;

public class UsuarioModel {

    public enum Rol {
        USUARIO, ADMIN;

        @Override
        public String toString() {
            switch (this) {
                case USUARIO:
                    return "USUARIO";
                case ADMIN:
                    return "ADMIN";
            }
            return super.toString();
        }
    }

    private int id;
    private String nombre;
    private String username;
    private String password;
    private Rol rol;
    private int id_casa;

    public UsuarioModel() {
    }

    public UsuarioModel(int id, String nombre, String username, String password, String rol, int id_casa) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.rol = StringToRol(rol);
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = StringToRol(rol);
    }

    public Rol StringToRol(String rol){
        switch (rol.toUpperCase()){
            case "USUARIO":
                return Rol.USUARIO;
            case "ADMIN":
                return Rol.ADMIN;
            default:
                return null;
        }
    }
}
