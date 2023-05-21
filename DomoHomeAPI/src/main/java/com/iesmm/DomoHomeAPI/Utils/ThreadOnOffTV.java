package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.IOException;
import java.util.logging.Logger;

public class ThreadOnOffTV implements Runnable {
    public enum Marca {SAMSUNG}
    private Marca marca;
    private Logger logger;
    String ip;

    public ThreadOnOffTV(Marca marca, DispositivosModel dispositivo) {
        this.marca = marca;
        logger = Logger.getLogger("on_off_tv_thread");
        this.ip = dispositivo.getIp();
    }

    @Override
    public void run() {
        if (marca.equals(Marca.SAMSUNG)){
            try {
                // Ejecutamos el script de python que enciende/apaga la tv
                Process p = Runtime.getRuntime().exec("python3 ./scripts/on_off_samsungtv.py " + ip);
            }
            catch(IOException e){
            logger.severe("Error en la E/S al ejecutar el script de lectura de temperatura y humedad");
            }
            catch(Exception e){
            logger.severe("Error: " + e.getMessage());
            }
        }
    }
}
