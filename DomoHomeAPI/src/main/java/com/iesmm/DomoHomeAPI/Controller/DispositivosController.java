package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.DispositivosModel;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import com.iesmm.DomoHomeAPI.Utils.ThreadEstadoBombilla;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffBombilla;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffTV;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/dispositivos")
public class DispositivosController {
    DAO dao = new DAOImpl();
    @PostMapping(value = "/getDispositivosUsuario", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<DispositivosModel> getDispositivosUsuario(@RequestBody UsuarioModel usuario) {
        List<DispositivosModel> lista = dao.listaDispositivosUsuario(usuario.getId());
        return lista;
    }

    @PostMapping(value = "/getDispositivoId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DispositivosModel getDispositivoId(@RequestBody Integer idDispositivo) {
        DispositivosModel dispositivo = dao.getDispositivoId(idDispositivo);
        return dispositivo;
    }

    @PostMapping(value = "/OnOffSamsungTv", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void OnOffSamsungTv(@RequestBody DispositivosModel dispositivo) {
        Thread thread = new Thread(new ThreadOnOffTV(dispositivo));
        thread.start();
    }

    @PostMapping(value = "/OnOffBombillaTpLink", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void OnOffBombillaTpLink(@RequestBody DispositivosModel dispositivo) {
        Thread thread = new Thread(new ThreadOnOffBombilla(dispositivo));
        thread.start();
    }

    @PostMapping(value = "/getEstadoBombillaTPLink", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean getEstadoBombillaTPLink(@RequestBody DispositivosModel dispositivo) throws InterruptedException {
        ThreadEstadoBombilla th = new ThreadEstadoBombilla(dispositivo);
        th.start();
        th.join();
        return th.getEstado();
    }

    @PostMapping(value = "/registrarDispositivo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean registrarDispositivo(@RequestBody DispositivosModel dispositivo) {
        return dao.registrarDispositivo(dispositivo);
    }

    @PostMapping(value = "/eliminarDispositivo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean eliminarDispositivo(@RequestBody DispositivosModel dispositivo) {
        return dao.eliminarDispositivo(dispositivo.getIdDispositivo());
    }
}
