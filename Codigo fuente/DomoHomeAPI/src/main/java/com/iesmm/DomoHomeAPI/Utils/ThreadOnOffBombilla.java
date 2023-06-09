package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.IOException;
import java.util.logging.Logger;

public class ThreadOnOffBombilla implements Runnable {
        private Logger logger;
        DispositivosModel dispositivo;

        public ThreadOnOffBombilla(DispositivosModel dispositivo) {
            logger = Logger.getLogger("on_off_bombilla_thread");
            this.dispositivo = dispositivo;
        }

        @Override
        public void run() {
            if (dispositivo.getTipo().equals(DispositivosModel.Tipo.BOMBILLA) && dispositivo.getMarca().equals(DispositivosModel.Marca.TP_LINK)){
                try {
                    // Ejecutamos el script de python que enciende/apaga la tv
                    Process p = Runtime.getRuntime().exec("python3 ./scripts/on_off_bombilla.py " + dispositivo.getIp() + " " + dispositivo.getUsuarioServicio() + " " + dispositivo.getPasswdServicio());
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
