package com.iesmm.DomoHomeAPI.Model;

public class TempHumedadModel {
    private Double temp = 0.0;
    private Double humedad = 0.0;

    public TempHumedadModel() {
    }

    public TempHumedadModel(Double temp, Double humedad) {
        this.temp = temp;
        this.humedad = humedad;
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
}
