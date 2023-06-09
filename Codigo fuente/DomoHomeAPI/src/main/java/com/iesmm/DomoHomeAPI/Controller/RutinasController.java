package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.DispositivosModel;
import com.iesmm.DomoHomeAPI.Model.RutinaModel;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import com.iesmm.DomoHomeAPI.Utils.ThreadGuardaMedida;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffBombilla;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffTV;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/rutinas")
public class RutinasController {
    DAO dao = new DAOImpl();
    List<RutinaModel> rutinas = new ArrayList<>();
    Logger logger = Logger.getLogger("rutinasController");

    @PostConstruct
    private void init() {
        // Cargamos todas las rutinas
        rutinas = dao.listaRutinas();

        if (rutinas.size() > 0) {
            for (RutinaModel rutina:rutinas) {
                creaTareaProgramada(rutina);
            }
        }
    }


    @PostMapping(value = "/getRutinas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<RutinaModel> getRutinas(@RequestBody UsuarioModel usuario) {
        List<RutinaModel> lista = dao.listaRutinasUsuario(usuario.getId());
        return lista;
    }

    @PostMapping(value = "/registraRutina", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean registraRutina(@RequestBody RutinaModel rutina) {
        Boolean correcto = dao.registraRutina(rutina);
        creaTareaProgramada(rutina);
        return correcto;
    }

    @PostMapping(value = "/eliminarRutina", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean eliminarRutina(@RequestBody RutinaModel rutina) {
        Boolean correcto = dao.eliminarRutina(rutina.getIdRutina());
        return correcto;
    }

    private void creaTareaProgramada(RutinaModel rutina) {
        try {
            //Creamos un Timer
            Timer timer = new Timer();

            // Creamos una clase anonima timertask que sera la que ejecutara el hilo necesario
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (rutina.getTipo().equals(RutinaModel.Tipo.ENCENDER) || rutina.getTipo().equals(RutinaModel.Tipo.APAGAR)) {
                        DispositivosModel dispositivo = dao.getDispositivoId(rutina.getIdDispositivo());

                        if (dispositivo.getTipo().equals(DispositivosModel.Tipo.BOMBILLA)) {
                            Thread thread = new Thread(new ThreadOnOffBombilla(dispositivo));
                            thread.start();
                        }
                        else if (dispositivo.getTipo().equals(DispositivosModel.Tipo.TV)) {
                            Thread thread = new Thread(new ThreadOnOffTV(dispositivo));
                            thread.start();
                        }
                    }
                    else if (rutina.getTipo().equals(RutinaModel.Tipo.GUARDAR_MEDIDA)) {
                        Thread thread = new Thread(new ThreadGuardaMedida(rutina.getIdSensor()));
                        thread.start();
                    }

                    Boolean correcto = dao.eliminarRutina(rutina.getIdRutina());
                    // Cuando se ha terminado de ejecutar, cancelamos la rutina
                    this.cancel();
                }
            };

            SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fechaEjecucion = sdt.parse(rutina.getFecha_hora());

            // Creamos la tarea programada
            timer.schedule(timerTask, fechaEjecucion);
        }
        catch (ParseException e) {
            logger.severe("Error al parsear la fecha");
        }
        catch (Exception e) {
            logger.severe("Error: " + e);
        }
    }
}
