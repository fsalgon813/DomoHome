package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.RutinaModel;
import com.iesmm.DomoHomeAPI.Model.SensorModel;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/sensores")
public class SensoresController {

    DAO dao = new DAOImpl();

    @PostMapping(value = "/getSensorUsuario", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SensorModel getSensorUsuario(@RequestBody UsuarioModel usuario) {
        SensorModel sensor = dao.sensorUsuario(usuario.getId());
        return sensor;
    }

}
