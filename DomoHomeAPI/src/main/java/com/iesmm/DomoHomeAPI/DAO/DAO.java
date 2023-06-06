package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.*;

import java.util.List;

public interface DAO {
    UsuarioModel filtrarPorUsername(String username);
    CasaModel filtrarCasaPorUsername(String username);
    Boolean registrarUsuario(RegisterParams params);
    List<DispositivosModel> listaDispositivosUsuario(int idUsuario);
    DispositivosModel getDispositivoId(int id_dispositivo);
    Boolean registrarDispositivo(DispositivosModel dispositivo);
    List<RutinaModel> listaRutinasUsuario(int idUsuario);
    List<RutinaModel> listaRutinas();
    Boolean registraRutina(RutinaModel rutina);
    List<UsuarioModel> listarUsuarios();
    Boolean eliminaUsuario(UsuarioModel usuario);
    Boolean actualizarUsuario(UsuarioModel usuario);
    Boolean eliminarRutina(int idRutina);
    Boolean eliminarDispositivo(int idDispositivo);
    List<TempHumedadModel> listarMedidasUsuario(int idUsuario);
    Boolean insertarMedida(TempHumedadModel thModel);
    SensorModel sensorUsuario(int idUsuario);
}
