package com.iesmm.domohome.Modelo;

public class RutinaModel {

    public enum Tipo {
        ENCENDER, APAGAR, GUARDAR_MEDIDA;

        @Override
        public String toString() {
            switch (this) {
                case ENCENDER:
                    return "ENCENDER";
                case APAGAR:
                    return "APAGAR";
                case GUARDAR_MEDIDA:
                    return "GUARDAR_MEDIDA";
            }
            return super.toString();
        }
    }

    private int idRutina;
    private String fecha_hora;
    private Tipo tipo;
    private int idDispositivo;
    private int idSensor;

    public RutinaModel() {
    }

    public RutinaModel(int idRutina, String fecha_hora, String tipo, int idDispositivo, int idSensor) {
        this.idRutina = idRutina;
        this.fecha_hora = fecha_hora;
        this.tipo = StringToTipo(tipo);
        this.idDispositivo = idDispositivo;
        this.idSensor = idSensor;
    }

    public int getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = StringToTipo(tipo);
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public Tipo StringToTipo(String tipo) {
        switch (tipo.toUpperCase()) {
            case "ENCENDER":
                return Tipo.ENCENDER;
            case "APAGAR":
                return Tipo.APAGAR;
            case "GUARDAR_MEDIDA":
                return Tipo.GUARDAR_MEDIDA;
        }
        return null;
    }
}
