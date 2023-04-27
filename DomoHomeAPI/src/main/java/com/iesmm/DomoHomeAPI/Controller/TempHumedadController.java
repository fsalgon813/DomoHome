package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.Model.TempHumedadModel;
import com.iesmm.DomoHomeAPI.Utils.ThreadTempHumedad;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.logging.Logger;

@Controller
@RequestMapping("/temp_humedad")
public class TempHumedadController {

    private TempHumedadModel thModel = new TempHumedadModel();

    private Logger logger = Logger.getLogger("temp_humedad_controller");

    // Cuando se incia el servicio, se inicia un hilo que se encarga de leer la temperatura y la humedad
    @PostConstruct
    public void init() {
        Thread th = new Thread(new ThreadTempHumedad(thModel));
        th.start();
        logger.info("Iniciando el hilo de lectura de temperatura y humedad");
    }


    // Devuelve un json con el valor de la temperatura
    @PostMapping(value = "/temp", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTemp() {
        return String.valueOf(thModel.getTemp());
    }

    // Devuelve un json con el valor de la humedad
    @PostMapping(value = "/humedad", produces = "application/json")
    @ResponseBody
    public String getHumedad() {
        return String.valueOf(thModel.getHumedad());
    }

}