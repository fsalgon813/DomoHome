package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.RegisterParams;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuariosController {
    DAO dao;

    @PostConstruct
    public void init() {
        dao = new DAOImpl();
    }

    @PostMapping(value="/filtrarUsername", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UsuarioModel filtrarUsername(@RequestBody String username) {
        UsuarioModel usuario = dao.filtrarPorUsername(username);
        return usuario;
    }

    @PostMapping(value="/registrarUsuario", produces= MediaType.APPLICATION_JSON_VALUE, consumes= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean registrarUsuario(@RequestBody RegisterParams params) {
        Boolean registrado = dao.registrarUsuario(params);
        return registrado;
    }
}
