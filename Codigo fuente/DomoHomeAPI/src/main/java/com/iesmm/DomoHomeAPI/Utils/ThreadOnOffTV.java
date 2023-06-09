package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.IOException;
import java.util.logging.Logger;

public class ThreadOnOffTV implements Runnable {
    private Logger logger;
    DispositivosModel dispositivo;

    public ThreadOnOffTV(DispositivosModel dispositivo) {
        logger = Logger.getLogger("on_off_tv_thread");
        this.dispositivo = dispositivo;
    }

    @Override
    public void run() {
        if (dispositivo.getTipo().equals(DispositivosModel.Tipo.TV) && dispositivo.getMarca().equals(DispositivosModel.Marca.SAMSUNG)){
            try {
                // Ejecutamos el script de python que enciende/apaga la tv
                Process p = Runtime.getRuntime().exec("python3 ./scripts/on_off_samsungtv.py " + dispositivo.getIp());
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
