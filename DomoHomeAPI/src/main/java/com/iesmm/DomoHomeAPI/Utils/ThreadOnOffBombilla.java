package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.DispositivosModel;

import java.io.IOException;
import java.util.logging.Logger;

public class ThreadOnOffBombilla implements Runnable {
        public enum Marca {TPLINK}
        private Marca marca;
        private Logger logger;
        String ip, usuario, passwd;

        public ThreadOnOffBombilla(Marca marca, DispositivosModel dispositivo) {
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
                    // Ejecutamos el script de python que enciende/apaga la tv
                    Process p = Runtime.getRuntime().exec("python3 ./scripts/on_off_bombilla.py " + ip + " " + usuario + " " + passwd);
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
