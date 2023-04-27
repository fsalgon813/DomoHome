package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.UsuarioModel;

public interface DAO {
    UsuarioModel filtrarPorUsername(String username);
}
