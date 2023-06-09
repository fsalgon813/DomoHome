package com.iesmm.domohome.DAO;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.SensorModel;
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

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

    public UsuarioModel getUsuarioUsername(String username, Context c) {
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return usuario;
    }

    public Boolean registraUsuario(RegisterParams params, Context c) {
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public CasaModel getCasa(String user, Context c) {
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el "toast" en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return casa;
    }

    public String getTemp(Context c){
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return temp;
    }

    public String getHumedad(Context c) {
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el "toast" en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return humedad;
    }

    public List<DispositivoModel> getDispositivosUsuario(UsuarioModel usuario, Context c){
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
                    // Sacamos el array de dispositivos de json
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        // Cojemos el elemento de json
                        JSONObject dispositivoJSON = jsonArray.getJSONObject(n);

                        int idDispositivo = dispositivoJSON.getInt("idDispositivo");
                        String nombre = dispositivoJSON.getString("nombre");
                        String ip = dispositivoJSON.getString("ip");
                        String tipo = dispositivoJSON.getString("tipo");
                        String marca = dispositivoJSON.getString("marca");
                        String usuarioServicio = dispositivoJSON.getString("usuarioServicio");
                        String passwdServicio = dispositivoJSON.getString("passwdServicio");

                        // Añadimos a la lista el dispositivo
                        lista.add(new DispositivoModel(idDispositivo, nombre, ip, tipo, marca, usuarioServicio, passwdServicio, usuario.getId()));
                    }
                }
            }
        }
        catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Boolean onoffTvSamsung(DispositivoModel dispositivo, Context c) {
        Boolean correcto = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion para encender/apagar el dispositivo samsung
            String url = URL_BASE + "/dispositivos/OnOffSamsungTv";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String dispositivoJSON = gson.toJson(dispositivo);
            RequestBody cuerpo = RequestBody.create(dispositivoJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                correcto = true;
            }
        }
        catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return correcto;
    }

    public Boolean onoffBombillaTpLink(DispositivoModel dispositivo, Context c) {
        Boolean correcto = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion para encender/apagar la bombilla tplink
            String url = URL_BASE + "/dispositivos/OnOffBombillaTpLink";

            // Convertimos el objeto DispositivoModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String dispositivoJSON = gson.toJson(dispositivo);
            RequestBody cuerpo = RequestBody.create(dispositivoJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                correcto = true;
            }
        }
        catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return correcto;
    }

    public Boolean getEstadoBombillaTpLink(DispositivoModel dispositivo, Context c) {
        Boolean encendido = false;
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion para sacar el estado de la bombilla
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el "toast" en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return encendido;
    }

    public Boolean registraDispositivo(DispositivoModel dispositivo, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion que registra el dispositivo inteligente
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<RutinaModel> getRutinas(UsuarioModel usuario, Context c){
        List<RutinaModel> lista = new ArrayList<>();
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion para obtener las rutinas
            String url = URL_BASE + "/rutinas/getRutinas";

            // Convertimos el objeto UsuarioModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String usuarioJSON = gson.toJson(usuario);
            RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Obtenemos la respuesta
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")){
                    // Sacamos el array de rutinas de JSON
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        // Sacamos la rutina
                        JSONObject rutinaJSON = jsonArray.getJSONObject(n);
                        RutinaModel rutina = new RutinaModel();
                        rutina.setIdRutina(rutinaJSON.getInt("idRutina"));
                        rutina.setFecha_hora(rutinaJSON.getString("fecha_hora"));
                        rutina.setTipo(rutinaJSON.getString("tipo"));
                        rutina.setIdDispositivo(rutinaJSON.getInt("idDispositivo"));

                        // La añadimos a la lista
                        lista.add(rutina);
                    }
                }
            }
        }
        catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public DispositivoModel getDispositivoId(Integer idDispositivo, Context c) {
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
                    // Sacamos el dispositivo
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return dispositivo;
    }

    @Override
    public Boolean registraRutina(RutinaModel rutina, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para registrar la rutina
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<UsuarioModel> listarUsuarios(Context c) {
        List<UsuarioModel> usuarios = new ArrayList<>();

        // Preparamos la peticion para listar los usuarios
        String url = URL_BASE + "/usuario/listarUsuarios";
        RequestBody requestBody = RequestBody.create("", null);

        Request request = new Request.Builder().url(url).post(requestBody).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // Sacamos la respuesta
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    // Sacamos el array de usuarios de JSON
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        // Obtenemos el usuario
                        JSONObject usuarioJSON = jsonArray.getJSONObject(n);
                        UsuarioModel usuario = new UsuarioModel(usuarioJSON.getInt("id"), usuarioJSON.getString("nombre"), usuarioJSON.getString("username"), usuarioJSON.getString("password"), usuarioJSON.getString("rol"), usuarioJSON.getInt("id_casa"));

                        // Lo añadimos a la lista
                        usuarios.add(usuario);
                    }
                }
            }
        } catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public Boolean eliminarUsuario(UsuarioModel usuario, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para eliminar el usuario
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public Boolean actualizaUsuario(UsuarioModel usuario, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para actualizar el usuario
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public Boolean eliminarDispositivo(DispositivoModel dispositivo, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para eliminar el dispositivo
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public Boolean eliminarRutina(RutinaModel rutina, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para eliminar la rutina
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    public List<TempHumedadModel> listarMedidasUsuario(UsuarioModel usuario, Context c){
        List<TempHumedadModel> lista = new ArrayList<>();
        try {
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");

            // Preparamos la peticion para obtener las medidas
            String url = URL_BASE + "/temp_humedad/getMedidasUsuario";

            // Convertimos el objeto UsuarioModel a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String usuarioJSON = gson.toJson(usuario);
            RequestBody cuerpo = RequestBody.create(usuarioJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Obtenemos la respuesta
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")){
                    // Sacamos el array de medidas de JSON
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int n = 0; n < jsonArray.length(); n++) {
                        // Obtenemos la medida
                        JSONObject thJSON = jsonArray.getJSONObject(n);
                        TempHumedadModel thModel = new TempHumedadModel();
                        thModel.setIdMedida(thJSON.getInt("idMedida"));
                        thModel.setTemp(thJSON.getDouble("temp"));
                        thModel.setHumedad(thJSON.getDouble("humedad"));
                        thModel.setFecha_hora(thJSON.getString("fecha_hora"));
                        thModel.setIdSensor(thJSON.getInt("idSensor"));

                        // La insertamos en la lista
                        lista.add(thModel);
                    }
                }
            }
        }
        catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el "toast" en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Boolean insertarMedida(TempHumedadModel thModel, Context c) {
        Boolean correcto = false;

        // Preparamos la peticion para insertar la medida
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
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        }
        catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
        }

        return correcto;
    }

    @Override
    public SensorModel getSensorUsuario(UsuarioModel usuario, Context c) {
        SensorModel sensor = null;

        // Preparamos la peticion para obtener el sensor
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
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    // Obtenemos el sensor
                    JSONObject sensorJSON = new JSONObject(respuesta);
                     sensor = new SensorModel();
                     sensor.setIdSensor(sensorJSON.getInt("idSensor"));
                     sensor.setPin(sensorJSON.getInt("pin"));
                     sensor.setTipo(sensorJSON.getString("tipo"));
                     sensor.setIdCasa(sensorJSON.getInt("idCasa"));
                }
            }
        } catch (IOException e) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Utiliza el Handler para mostrar el toast en el hilo principal
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c, c.getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return sensor;
    }
}
