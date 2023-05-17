package com.iesmm.domohome.Vista;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Home extends Fragment {

    private TextView temp, humedad, nombreCasa, codInvitacion;
    private Logger logger;
    private OkHttpClient client;
    private String username = "";
    AsyncTempHumedad asyncTempHumedad;
    AsyncCargaCasa asyncCargaCasa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inicializamos el logger
        logger = Logger.getLogger("Home");

        // Inicializamos el cliente HTTP que nos permitira hacer peticiones a la API
        client = new OkHttpClient();

        // Cargamos el usuario que se ha logueado
        username = cargaUsuario();
        logger.info("Usuario logueado: " + username);

        // Se inicializan las AsyncTask
        asyncCargaCasa = new AsyncCargaCasa();
        asyncTempHumedad = new AsyncTempHumedad();

        // Se ejecutan las tareas asincronas
        cargaAsyncTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los TextView(se ponen en el onviewcreated porque en el oncreate todavia no se ha inicializado la vista y el metodo getview devuelve null
        temp = (TextView) view.findViewById(R.id.tvTemperatura);
        humedad = (TextView) view.findViewById(R.id.tvHumedad);
        nombreCasa = (TextView) view.findViewById(R.id.tvCasa);
        codInvitacion = (TextView) view.findViewById(R.id.tvInvitacion);


    }

    @Override
    public void onResume() {
        cargaAsyncTasks();
        super.onResume();
    }

    @Override
    public void onPause() {
        paraAsyncTasks();
        super.onPause();
    }



    public String cargaUsuario() {
        String username = "";
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            username = b.getString("username");
        }
        return username;
    }

    private void cargaAsyncTasks() {
        // Ejecutamos la tarea asincrona para obtener el nombre de la casa
        // Primero se ejecuta esta tarea asincrona debido a que la otra se ejecuta siempre
        if (asyncCargaCasa == null || asyncCargaCasa.getStatus() == AsyncTask.Status.FINISHED || asyncCargaCasa.getStatus() == AsyncTask.Status.PENDING){
            asyncCargaCasa = new AsyncCargaCasa();
            asyncCargaCasa.execute();
        }

        // Ejecutamos la tarea asincrona para obtener la temperatura y la humedad
        if (asyncTempHumedad == null || asyncTempHumedad.getStatus() == AsyncTask.Status.FINISHED || asyncTempHumedad.getStatus() == AsyncTask.Status.PENDING) {
            asyncTempHumedad = new AsyncTempHumedad();
            asyncTempHumedad.execute();
        }
    }

    private void paraAsyncTasks() {
        if (asyncCargaCasa != null || asyncCargaCasa.getStatus() == AsyncTask.Status.RUNNING){
            asyncCargaCasa.cancel(true);
        }
        if (asyncTempHumedad != null || asyncTempHumedad.getStatus() == AsyncTask.Status.RUNNING){
            asyncTempHumedad.cancel(true);
        }
    }

    private class AsyncCargaCasa extends AsyncTask<Void, CasaModel, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Preparamos la peticion para obtener el nombre de la casa a partir del username
                String urlTemp = "http://192.168.0.89:8081/casa/casaUsername";
                MediaType tipo = MediaType.parse("text/plain; charset=utf-8");
                RequestBody requestBody = RequestBody.create(username, tipo);
                Request request = new Request.Builder().url(urlTemp).post(requestBody).build();
                // Ejecutamos la peticion y obtenemos la respuesta
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    String respuesta = response.body().string();
                    JSONObject jsonObject = new JSONObject(respuesta);
                    int idCasa = jsonObject.getInt("idCasa");
                    String nombre = jsonObject.getString("nombre");
                    String cod = jsonObject.getString("codInvitacion");
                    CasaModel casa = new CasaModel(idCasa, nombre, cod);
                    publishProgress(casa);
                }
            }
            catch (IOException e) {
                logger.severe("Error en la E/S al hacer la peticion HTTP");
            }
            catch (Exception e) {
                logger.severe("Error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(CasaModel... values) {
            if (values[0] != null){
                nombreCasa.setText(values[0].getNombre());
                String codeString = getString(R.string.code);
                codInvitacion.setText(codeString + ": " + values[0].getCodInvitacion());
            }
        }
    }

    private class AsyncTempHumedad extends AsyncTask<Void, String, Void> {

        private final int DELAY = 5000;

        @Override
        protected Void doInBackground(Void... voids) {
            // Mientras no se cancele la tarea asincrona, coge la temperatura y la humedad
            while (!isCancelled()) {
                try {
                    RequestBody requestBody = RequestBody.create("", null);

                    // Preparamos la peticion de la temperatura
                    String urlTemp = "http://192.168.0.89:8081/temp_humedad/temp";
                    Request requestTemp = new Request.Builder().url(urlTemp).post(requestBody).build();

                    // Preparamos la peticion de la humedad
                    String urlHumedad = "http://192.168.0.89:8081/temp_humedad/humedad";
                    Request requestHumedad = new Request.Builder().url(urlHumedad).post(requestBody).build();

                    // Ejecutamos las peticiones y obtenemos la respuesta
                    Response responseTemp = client.newCall(requestTemp).execute();
                    Response responseHumedad = client.newCall(requestHumedad).execute();

                    if (responseTemp.isSuccessful() && responseHumedad.isSuccessful()){
                        String respuestaTemp = responseTemp.body().string();
                        String respuestaHumedad = responseHumedad.body().string();
                        publishProgress(respuestaTemp, respuestaHumedad);
                    }

                    // Esperamos el tiempo especificado antes de volver a hacer la peticion
                    Thread.sleep(DELAY);
                }
                catch (InterruptedException e) {
                    logger.severe("Se ha interrumpido la lectura de temperatura y humedad");
                }
                catch (IOException e) {
                    logger.severe("Error en la E/S al hacer la peticion HTTP");
                }
                catch (Exception e) {
                    logger.severe("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0] != null && values[1] != null && Double.valueOf(values[0]) > 0 && Double.valueOf(values[1]) > 0){
                temp.setText(values[0] + "ºC");
                humedad.setText(values[1] + "%");
            }
            else{
                logger.severe("Error al obtener la temperatura y la humedad");
            }
        }

    }
}