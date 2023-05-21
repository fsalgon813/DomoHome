package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.DispositivosModel;
import com.iesmm.DomoHomeAPI.Utils.ThreadEstadoBombilla;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffBombilla;
import com.iesmm.DomoHomeAPI.Utils.ThreadOnOffTV;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Controller
@RequestMapping("/dispositivos")
public class DispositivosController {
    DAO dao = new DAOImpl();
    @PostMapping(value = "/getDispositivos", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<DispositivosModel> getDispositivos() {
        List<DispositivosModel> lista = dao.listaDispositivos();
        return lista;
    }

    @PostMapping(value = "/OnOffSamsungTv", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void OnOffSamsungTv(@RequestBody DispositivosModel dispositivo) {
        Thread thread = new Thread(new ThreadOnOffTV(ThreadOnOffTV.Marca.SAMSUNG, dispositivo));
        thread.start();
    }

    @PostMapping(value = "/OnOffBombillaTpLink", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void OnOffBombillaTpLink(@RequestBody DispositivosModel dispositivo) {
        Thread thread = new Thread(new ThreadOnOffBombilla(ThreadOnOffBombilla.Marca.TPLINK, dispositivo));
        thread.start();
    }

    @PostMapping(value = "/getEstadoBombillaTPLink", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean getEstadoBombillaTPLink(@RequestBody DispositivosModel dispositivo) throws InterruptedException {
        ThreadEstadoBombilla th = new ThreadEstadoBombilla(ThreadEstadoBombilla.Marca.TPLINK, dispositivo);
        th.start();
        th.join();
        return th.getEstado();
    }
}
