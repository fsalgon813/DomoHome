package com.iesmm.domohome.DAO;

import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.SensorModel;
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.Modelo.UsuarioModel;

import java.util.List;

public interface DAO {
    UsuarioModel getUsuarioUsername(String username);
    Boolean registraUsuario(RegisterParams params);
    CasaModel getCasa(String username);
    String getTemp();
    String getHumedad();
    List<DispositivoModel> getDispositivosUsuario(UsuarioModel usuario);
    DispositivoModel getDispositivoId(Integer idDispositivo);
    Boolean onoffTvSamsung(DispositivoModel dispositivo);
    Boolean onoffBombillaTpLink(DispositivoModel dispositivo);
    Boolean getEstadoBombillaTpLink(DispositivoModel dispositivo);
    Boolean registraDispositivo(DispositivoModel dispositivo);
    List<RutinaModel> getRutinas(UsuarioModel usuario);
    Boolean registraRutina(RutinaModel rutina);
    List<UsuarioModel> listarUsuarios();
    Boolean eliminarUsuario(UsuarioModel usuario);
    Boolean actualizaUsuario(UsuarioModel usuario);
    Boolean eliminarDispositivo(DispositivoModel dispositivoModel);
    Boolean eliminarRutina(RutinaModel rutina);
    List<TempHumedadModel> listarMedidasUsuario(UsuarioModel usuario);
    Boolean insertarMedida(TempHumedadModel thModel);
    SensorModel getSensorUsuario(UsuarioModel usuario);
}
