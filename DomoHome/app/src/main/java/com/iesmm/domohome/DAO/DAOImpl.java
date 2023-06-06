package com.iesmm.domohome.DAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.SensorModel;
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.Modelo.UsuarioModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DAOImpl implements DAO {
    private OkHttpClient client;
    private Logger logger;

    private final String URL_BASE = "http://192.168.0.89:8081";

    public DAOImpl(){
        // Inicializamos el cliente HTTP
        client = new OkHttpClient();

        // Inicializamos el logger
        logger = Logger.getLogger("Controlador");
    }

    public UsuarioModel getUsuarioUsername(String username) {
        UsuarioModel usuario = null;
        // Preparamos la peticion
        String url = URL_BASE + "/usuario/filtrarUsername";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");
        RequestBody cuerpo = RequestBody.create(username, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            // Si la respuesta es correcta, comprobamos la contraseña
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    JSONObject usuarioJSON = new JSONObject(respuesta);
                    usuario = new UsuarioModel(usuarioJSON.getInt("id"), usuarioJSON.getString("nombre"), usuarioJSON.getString("username"), usuarioJSON.getString("password"), usuarioJSON.getString("rol"), usuarioJSON.getInt("id_casa"));
                }
            }
        } catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return usuario;
    }

    public Boolean registraUsuario(RegisterParams params) {
        Boolean correcto = false;
        String url = URL_BASE + "/usuario/registrarUsuario";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto RegisterParams a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String registerParamsJSON = gson.toJson(params);
        RequestBody cuerpo = RequestBody.create(registerParamsJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equals("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public CasaModel getCasa(String user) {
        CasaModel casa = null;
        try {
            // Preparamos la peticion para obtener el nombre de la casa a partir del username
            String urlTemp = URL_BASE + "/casa/casaUsername";
            MediaType tipo = MediaType.parse("text/plain; charset=utf-8");
            RequestBody requestBody = RequestBody.create(user, tipo);
            Request request = new Request.Builder().url(urlTemp).post(requestBody).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                String respuesta = response.body().string();
                JSONObject jsonObject = new JSONObject(respuesta);
                int idCasa = jsonObject.getInt("idCasa");
                String nombre = jsonObject.getString("nombre");
                String cod = jsonObject.getString("codInvitacion");
                casa = new CasaModel(idCasa, nombre, cod);
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return casa;
    }

    public String getTemp(){
        String temp = null;
        try {
            RequestBody requestBody = RequestBody.create("", null);

            // Preparamos la peticion de la temperatura
            String urlTemp = URL_BASE + "/temp_humedad/temp";
            Request requestTemp = new Request.Builder().url(urlTemp).post(requestBody).build();

            // Ejecutamos la peticion
            Response response = client.newCall(requestTemp).execute();

            // Si la respuesta es correcta, obtenemos los datos
            if (response.isSuccessful()){
                temp = response.body().string();
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return temp;
    }

    public String getHumedad() {
        String humedad = null;
        try {
            RequestBody requestBody = RequestBody.create("", null);

            // Preparamos la peticion de la humedad
            String url = URL_BASE + "/temp_humedad/humedad";
            Request request = new Request.Builder().url(url).post(requestBody).build();

            // Ejecutamos la peticion
            Response response = client.newCall(request).execute();

            // Si la respuesta es correcta, obtenemos los datos
            if (response.isSuccessful()){
                humedad = response.body().string();
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return humedad;
    }

    public List<DispositivoModel> getDispositivosUsuario(UsuarioModel usuario){
        List<DispositivoModel> lista = new ArrayList<>();
        try {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de los dispositivos inteligentes
            String url = URL_BASE + "/dispositivos/getDispositivosUsuario";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String registerParamsJSON = gson.toJson(usuario);
            RequestBody cuerpo = RequestBody.create(registerParamsJSON, mediaType);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")){
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject dispositivoJSON = jsonArray.getJSONObject(n);
                        int idDispositivo = dispositivoJSON.getInt("idDispositivo");
                        String nombre = dispositivoJSON.getString("nombre");
                        String ip = dispositivoJSON.getString("ip");
                        String tipo = dispositivoJSON.getString("tipo");
                        String marca = dispositivoJSON.getString("marca");
                        String usuarioServicio = dispositivoJSON.getString("usuarioServicio");
                        String passwdServicio = dispositivoJSON.getString("passwdServicio");
                        lista.add(new DispositivoModel(idDispositivo, nombre, ip, tipo, marca, usuarioServicio, passwdServicio, usuario.getId()));
                    }
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Boolean onoffTvSamsung(DispositivoModel dispositivo) {
        Boolean correcto = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de los dispositivos inteligentes
            String url = URL_BASE + "/dispositivos/OnOffSamsungTv";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String registerParamsJSON = gson.toJson(dispositivo);
            RequestBody cuerpo = RequestBody.create(registerParamsJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                correcto = true;
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return correcto;
    }

    public Boolean onoffBombillaTpLink(DispositivoModel dispositivo) {
        Boolean correcto = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de los dispositivos inteligentes
            String url = URL_BASE + "/dispositivos/OnOffBombillaTpLink";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String registerParamsJSON = gson.toJson(dispositivo);
            RequestBody cuerpo = RequestBody.create(registerParamsJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                correcto = true;
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return correcto;
    }

    public Boolean getEstadoBombillaTpLink(DispositivoModel dispositivo) {
        Boolean encendido = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de los dispositivos inteligentes
            String url = URL_BASE + "/dispositivos/getEstadoBombillaTPLink";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String dispositivoJSON = gson.toJson(dispositivo);
            RequestBody cuerpo = RequestBody.create(dispositivoJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                String respuesta = response.body().string();
                if (respuesta.equals("true")){
                    encendido = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return encendido;
    }

    public Boolean registraDispositivo(DispositivoModel dispositivo) {
        Boolean correcto = false;
        String url = URL_BASE + "/dispositivos/registrarDispositivo";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto DispositivoModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String dispositivoJSON = gson.toJson(dispositivo);
        RequestBody cuerpo = RequestBody.create(dispositivoJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<RutinaModel> getRutinas(UsuarioModel usuario){
        List<RutinaModel> lista = new ArrayList<>();
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de las rutinas
            String url = URL_BASE + "/rutinas/getRutinas";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String usuarioJSON = gson.toJson(usuario);
            RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")){
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject rutinaJSON = jsonArray.getJSONObject(n);
                        RutinaModel rutina = new RutinaModel();
                        rutina.setIdRutina(rutinaJSON.getInt("idRutina"));
                        rutina.setFecha_hora(rutinaJSON.getString("fecha_hora"));
                        rutina.setTipo(rutinaJSON.getString("tipo"));
                        rutina.setIdDispositivo(rutinaJSON.getInt("idDispositivo"));
                        lista.add(rutina);
                    }
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public DispositivoModel getDispositivoId(Integer idDispositivo) {
        DispositivoModel dispositivo = null;
        // Preparamos la peticion
        String url = URL_BASE + "/dispositivos/getDispositivoId";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody cuerpo = RequestBody.create(String.valueOf(idDispositivo), mediaType);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            // Si la respuesta es correcta, comprobamos la contraseña
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    JSONObject dispositivoJSON = new JSONObject(respuesta);
                    String nombre = dispositivoJSON.getString("nombre");
                    String ip = dispositivoJSON.getString("ip");
                    String tipo = dispositivoJSON.getString("tipo");
                    String marca = dispositivoJSON.getString("marca");
                    String usuarioServicio = dispositivoJSON.getString("usuarioServicio");
                    String passwdServicio = dispositivoJSON.getString("passwdServicio");
                    int idUsuario = dispositivoJSON.getInt("idUsuario");
                    dispositivo = new DispositivoModel(idDispositivo, nombre, ip, tipo, marca, usuarioServicio, passwdServicio, idUsuario);
                }
            }
        } catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return dispositivo;
    }

    @Override
    public Boolean registraRutina(RutinaModel rutina) {
        Boolean correcto = false;
        String url = URL_BASE + "/rutinas/registraRutina";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto RutinaModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String rutinaJSON = gson.toJson(rutina);
        RequestBody cuerpo = RequestBody.create(rutinaJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<UsuarioModel> listarUsuarios() {
        List<UsuarioModel> usuarios = new ArrayList<>();

        String url = URL_BASE + "/usuario/listarUsuarios";
        RequestBody requestBody = RequestBody.create("", null);

        Request request = new Request.Builder().url(url).post(requestBody).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            // Si la respuesta es correcta, comprobamos la contraseña
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject usuarioJSON = jsonArray.getJSONObject(n);
                        UsuarioModel usuario = new UsuarioModel(usuarioJSON.getInt("id"), usuarioJSON.getString("nombre"), usuarioJSON.getString("username"), usuarioJSON.getString("password"), usuarioJSON.getString("rol"), usuarioJSON.getInt("id_casa"));
                        usuarios.add(usuario);
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public Boolean eliminarUsuario(UsuarioModel usuario) {
        Boolean correcto = false;
        String url = URL_BASE + "/usuario/eliminaUsuario";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto UsuarioModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String usuarioJSON = gson.toJson(usuario);
        RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public Boolean actualizaUsuario(UsuarioModel usuario) {
        Boolean correcto = false;
        String url = URL_BASE + "/usuario/actualizaUsuario";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto UsuarioModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String usuarioJSON = gson.toJson(usuario);
        RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public Boolean eliminarDispositivo(DispositivoModel dispositivo) {
        Boolean correcto = false;
        String url = URL_BASE + "/dispositivos/eliminarDispositivo";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto DispositivoModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String dispositivoJSON = gson.toJson(dispositivo);
        RequestBody cuerpo = RequestBody.create(dispositivoJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public Boolean eliminarRutina(RutinaModel rutina) {
        Boolean correcto = false;
        String url = URL_BASE + "/rutinas/eliminarRutina";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto RutinaModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String rutinaJSON = gson.toJson(rutina);
        RequestBody cuerpo = RequestBody.create(rutinaJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<TempHumedadModel> listarMedidasUsuario(UsuarioModel usuario){
        List<TempHumedadModel> lista = new ArrayList<>();
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion de las rutinas
            String url = URL_BASE + "/temp_humedad/getMedidasUsuario";

            // Convertimos el objeto UsuarioModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String usuarioJSON = gson.toJson(usuario);
            RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")){
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        JSONObject thJSON = jsonArray.getJSONObject(n);
                        TempHumedadModel thModel = new TempHumedadModel();
                        thModel.setIdMedida(thJSON.getInt("idMedida"));
                        thModel.setTemp(thJSON.getDouble("temp"));
                        thModel.setHumedad(thJSON.getDouble("humedad"));
                        thModel.setFecha_hora(thJSON.getString("fecha_hora"));
                        thModel.setIdSensor(thJSON.getInt("idSensor"));
                        lista.add(thModel);
                    }
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Boolean insertarMedida(TempHumedadModel thModel) {
        Boolean correcto = false;
        String url = URL_BASE + "/temp_humedad/insertarMedida";
        MediaType tipo = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto TempHumedadModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String thModelJSON = gson.toJson(thModel);
        RequestBody cuerpo = RequestBody.create(thModelJSON, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        try {
            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (response.body().string().equalsIgnoreCase("true")){
                    correcto = true;
                }
            }
        }
        catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public SensorModel getSensorUsuario(UsuarioModel usuario) {
        SensorModel sensor = null;
        // Preparamos la peticion
        String url = URL_BASE + "/sensores/getSensorUsuario";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // Convertimos el objeto UsuarioModel a JSON usando GSON
        Gson gson = new GsonBuilder().create();
        String usuarioJSON = gson.toJson(usuario);
        RequestBody cuerpo = RequestBody.create(usuarioJSON, mediaType);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            // Si la respuesta es correcta, comprobamos la contraseña
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    JSONObject sensorJSON = new JSONObject(respuesta);
                     sensor = new SensorModel();
                     sensor.setIdSensor(sensorJSON.getInt("idSensor"));
                     sensor.setPin(sensorJSON.getInt("pin"));
                     sensor.setTipo(sensorJSON.getString("tipo"));
                     sensor.setIdCasa(sensorJSON.getInt("idCasa"));
                }
            }
        } catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return sensor;
    }
}
