package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.CasaModel;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/casa")
public class CasaController {

    DAO dao;

    @PostConstruct
    public void init() {
        dao = new DAOImpl();
    }

    @PostMapping(value = "/casaUsername", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CasaModel casaUsername(@RequestBody String username) {
        CasaModel casa = dao.filtrarCasaPorUsername(username);
        return casa;
    }

}
