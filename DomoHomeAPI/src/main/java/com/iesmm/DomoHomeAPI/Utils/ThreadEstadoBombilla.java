package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ThreadEstadoBombilla extends Thread {
    private Logger logger;
    DispositivosModel dispositivo;
    Boolean estado = false;

    public ThreadEstadoBombilla(DispositivosModel dispositivo) {
        logger = Logger.getLogger("estado_bombilla_thread");
        this.dispositivo = dispositivo;
    }

    @Override
    public void run() {
        if (dispositivo.getTipo().equals(DispositivosModel.Tipo.BOMBILLA) && dispositivo.getMarca().equals(DispositivosModel.Marca.TP_LINK)){
            try {
                // Ejecutamos el script de python que devuelve el estado de la bombilla
                Process p = Runtime.getRuntime().exec("python3 ./scripts/estado_bombilla.py " + dispositivo.getIp() + " " + dispositivo.getUsuarioServicio() + " " + dispositivo.getPasswdServicio());
                // Leemos la salida del script
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                // Guardamos la salida en un String
                String salida = br.readLine();
                if (salida.equalsIgnoreCase("True")){
                    estado = true;
                }
            }
            catch(IOException e){
                logger.severe("Error en la E/S al ejecutar el script de lectura de temperatura y humedad");
            }
            catch(Exception e){
                logger.severe("Error: " + e.getMessage());
            }
        }
    }

    public Boolean getEstado() {
        return estado;
    }
}
