package com.iesmm.domohome.Modelo;

public class TempHumedadModel {

    private int idMedida;
    private Double temp = 0.0;
    private Double humedad = 0.0;
    private String fecha_hora;
    private int idSensor;

    public TempHumedadModel() {
    }

    public TempHumedadModel(Double temp, Double humedad) {
        this.temp = temp;
        this.humedad = humedad;
    }

    public TempHumedadModel(Double temp, Double humedad, String fecha_hora) {
        this.temp = temp;
        this.humedad = humedad;
        this.fecha_hora = fecha_hora;
    }

    public int getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public Double getHumedad() {
        return humedad;
    }

    public Double getTemp() {
        return temp;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }
}
