package com.iesmm.DomoHomeAPI.Model;

public class SensorModel {
    int idSensor;
    // El pin por defecto es el 4
    int pin = 4;
    String tipo;
    int idCasa;

    public SensorModel() {
    }

    public SensorModel(int idSensor, int pin, String tipo, int idCasa) {
        this.idSensor = idSensor;
        this.pin = pin;
        this.tipo = tipo;
        this.idCasa = idCasa;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(int idCasa) {
        this.idCasa = idCasa;
    }
}
