package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.CasaModel;
import com.iesmm.DomoHomeAPI.Model.RegisterParams;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;

public interface DAO {
    UsuarioModel filtrarPorUsername(String username);
    CasaModel filtrarCasaPorUsername(String username);
    Boolean registrarUsuario(RegisterParams params);
}
