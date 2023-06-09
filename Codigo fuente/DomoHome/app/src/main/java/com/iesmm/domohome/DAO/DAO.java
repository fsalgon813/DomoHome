package com.iesmm.domohome.DAO;

import android.content.Context;

import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.SensorModel;
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.Modelo.UsuarioModel;

import java.util.List;

public interface DAO {
    UsuarioModel getUsuarioUsername(String username, Context c);
    Boolean registraUsuario(RegisterParams params, Context c);
    CasaModel getCasa(String username, Context c);
    String getTemp(Context c);
    String getHumedad(Context c);
    List<DispositivoModel> getDispositivosUsuario(UsuarioModel usuario, Context c);
    DispositivoModel getDispositivoId(Integer idDispositivo, Context c);
    Boolean onoffTvSamsung(DispositivoModel dispositivo, Context c);
    Boolean onoffBombillaTpLink(DispositivoModel dispositivo, Context c);
    Boolean getEstadoBombillaTpLink(DispositivoModel dispositivo, Context c);
    Boolean registraDispositivo(DispositivoModel dispositivo, Context c);
    List<RutinaModel> getRutinas(UsuarioModel usuario, Context c);
    Boolean registraRutina(RutinaModel rutina, Context c);
    List<UsuarioModel> listarUsuarios(Context c);
    Boolean eliminarUsuario(UsuarioModel usuario, Context c);
    Boolean actualizaUsuario(UsuarioModel usuario, Context c);
    Boolean eliminarDispositivo(DispositivoModel dispositivoModel, Context c);
    Boolean eliminarRutina(RutinaModel rutina, Context c);
    List<TempHumedadModel> listarMedidasUsuario(UsuarioModel usuario, Context c);
    Boolean insertarMedida(TempHumedadModel thModel, Context c);
    SensorModel getSensorUsuario(UsuarioModel usuario, Context c);
}
