package com.iesmm.domohome.Controlador;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RegisterParams;
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

public class Controlador {
    private OkHttpClient client;
    private Logger logger;

    public Controlador(){
        // Inicializamos el cliente HTTP
        client = new OkHttpClient();

        // Inicializamos el logger
        logger = Logger.getLogger("Controlador");
    }

    public Boolean verificaUsuario(String user, String pass) {
        Boolean correcto = false;
        // Preparamos la peticion
        String url = "http://192.168.0.89:8081/usuario/filtrarUsername";
        MediaType tipo = MediaType.parse("text/plain; charset=utf-8");
        RequestBody cuerpo = RequestBody.create(user, tipo);
        Request request = new Request.Builder().url(url).post(cuerpo).build();

        // Ejecutamos la peticion y obtenemos la respuesta
        try {
            Response response = client.newCall(request).execute();
            // Si la respuesta es correcta, comprobamos la contraseña
            if (response.isSuccessful()) {
                String respuesta = response.body().string();
                if (respuesta != null && !respuesta.equals("")) {
                    JSONObject usuario = new JSONObject(respuesta);
                    String username = usuario.getString("username");
                    String passwd = usuario.getString("password");

                    if (username.equals(username) && passwd.equals(pass)) {
                        correcto = true;
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Error en la E/S al hacer la peticion HTTP");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return correcto;
    }

    public Boolean registraUsuario(RegisterParams params) {
        Boolean correcto = false;
        String url = "http://192.168.0.89:8081/usuario/registrarUsuario";
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
            String urlTemp = "http://192.168.0.89:8081/casa/casaUsername";
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
            String urlTemp = "http://192.168.0.89:8081/temp_humedad/temp";
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
            String url = "http://192.168.0.89:8081/temp_humedad/humedad";
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

    public List<DispositivoModel> getDispositivos(){
        List<DispositivoModel> lista = new ArrayList<>();
        try {
            RequestBody requestBody = RequestBody.create("", null);

            // Preparamos la peticion de los dispositivos inteligentes
            String url = "http://192.168.0.89:8081/dispositivos/getDispositivos";
            Request request = new Request.Builder().url(url).post(requestBody).build();

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
                        int idUsuario = dispositivoJSON.getInt("idUsuario");
                        lista.add(new DispositivoModel(idDispositivo, nombre, ip, tipo, marca, usuarioServicio, passwdServicio, idUsuario));
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
            String url = "http://192.168.0.89:8081/dispositivos/OnOffSamsungTv";

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
            String url = "http://192.168.0.89:8081/dispositivos/OnOffBombillaTpLink";

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
            String url = "http://192.168.0.89:8081/dispositivos/getEstadoBombillaTPLink";

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

}
