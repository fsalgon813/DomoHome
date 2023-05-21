package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Model.TempHumedadModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ThreadTempHumedad implements Runnable{

    private TempHumedadModel thModel = null;
    private Logger logger = Logger.getLogger("temp_humedad_thread");
    private static final int DELAY = 5000;

     public ThreadTempHumedad(TempHumedadModel thModel){
            this.thModel = thModel;
     }
    @Override
    public void run() {
        while (true){
            try{
                // Ejecutamos el script de Python que nos devuelve la temperatura y humedad en el mismo formato que un csv
                // (temperatura;humedad)
                Process p = Runtime.getRuntime().exec("python3 ./scripts/temp_humedad.py");
                // Leemos la salida del script
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                // Guardamos la salida en un String
                String salida = br.readLine();

                // Asignamos los valores que nos ha devuelto el script a sus respectivas variables
                thModel.setTemp(Double.parseDouble(salida.split(";")[0]));
                thModel.setHumedad(Double.parseDouble(salida.split(";")[1]));
                Thread.sleep(DELAY);
            }
            catch(IOException e){
                logger.severe("Error en la E/S al ejecutar el script de lectura de temperatura y humedad");
            }
            catch(InterruptedException e){
                logger.severe("Error en el hilo de lectura de temperatura y humedad");
            }
            catch(Exception e){
                logger.severe("Error: " + e.getMessage());
            }
        }
    }
}
