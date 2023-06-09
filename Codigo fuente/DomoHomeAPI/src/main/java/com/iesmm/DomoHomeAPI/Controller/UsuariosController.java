package com.iesmm.DomoHomeAPI.Controller;

import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.RegisterParams;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuario")
public class UsuariosController {
    DAO dao;

    @PostConstruct
    public void init() {
        dao = new DAOImpl();
    }

    @PostMapping(value="/filtrarUsername", produces= MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value="/listarUsuarios", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UsuarioModel> listarUsuarios() {
        List<UsuarioModel> usuarios = dao.listarUsuarios();
        return usuarios;
    }

    @PostMapping(value="/eliminaUsuario", produces= MediaType.APPLICATION_JSON_VALUE, consumes= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean eliminaUsuario(@RequestBody UsuarioModel usuario) {
        Boolean correcto = dao.eliminaUsuario(usuario);
        return correcto;
    }

    @PostMapping(value="/actualizaUsuario", produces= MediaType.APPLICATION_JSON_VALUE, consumes= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean actualizaUsuario(@RequestBody UsuarioModel usuario) {
        Boolean correcto = dao.actualizarUsuario(usuario);
        return correcto;
    }
}
