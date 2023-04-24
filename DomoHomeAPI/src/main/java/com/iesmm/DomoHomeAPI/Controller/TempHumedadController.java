package com.iesmm.DomoHomeAPI.Controller;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/temp_humedad")
public class TempHumedadController {

    private Double temp = 0.0;
    private Double humedad = 0.0;
    Logger logger = Logger.getLogger("temp_humedad");

    // Devuelve un json con el valor de la temperatura
    @GetMapping(value = "/temp", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTemp() {
        return String.valueOf(temp);
    }

    // Devuelve un json con el valor de la humedad
    @GetMapping(value = "/humedad", produces = "application/json")
    @ResponseBody
    public String getHumedad() {
        return String.valueOf(humedad);
    }

    @Scheduled(fixedRate = 5000)
    public void readValues() {
        try{
            // Ejecutamos el script de Python que nos devuelve la temperatura y humedad en el mismo formato que un csv
            // (temperatura;humedad)
            Process p = Runtime.getRuntime().exec("python3 ./scripts/temp_humedad.py");
            // Leemos la salida del script
            BufferedReader br = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            // Guardamos la salida en un String
            String salida = br.readLine();

            // Asignamos los valores que nos ha devuelto el script a sus respectivas variables
            temp = Double.parseDouble(salida.split(";")[0]);
            humedad = Double.parseDouble(salida.split(";")[1]);
        }
        catch(IOException e){
            logger.severe("Error en la E/S al ejecutar el script de lectura de temperatura y humedad");
        }
        catch(Exception e){
            logger.severe("Error: " + e.getMessage());
        }
    }

}