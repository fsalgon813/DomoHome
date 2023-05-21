package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ThreadEstadoBombilla extends Thread {
    public enum Marca {TPLINK}
    private Marca marca;
    private Logger logger;
    String ip, usuario, passwd;
    Boolean estado = false;

    public ThreadEstadoBombilla(Marca marca, DispositivosModel dispositivo) {
        this.marca = marca;
        logger = Logger.getLogger("on_off_bombilla_thread");
        this.ip = dispositivo.getIp();
        this.usuario = dispositivo.getUsuarioServicio();
        this.passwd = dispositivo.getPasswdServicio();
    }

    @Override
    public void run() {
        if (marca.equals(Marca.TPLINK)){
            try {
                // Ejecutamos el script de python que devuelve el estado de la bombilla
                Process p = Runtime.getRuntime().exec("python3 ./scripts/estado_bombilla.py " + ip + " " + usuario + " " + passwd);
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
