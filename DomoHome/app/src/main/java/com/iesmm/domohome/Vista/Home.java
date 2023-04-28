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

import com.google.android.material.snackbar.Snackbar;
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

    private TextView temp, humedad;
    private Logger logger;

    private OkHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inicializamos el logger
        logger = Logger.getLogger("Home");

        // Inicializamos el cliente HTTP que nos permitira hacer peticiones a la API
        client = new OkHttpClient();

        AsyncTempHumedad asyncTempHumedad = new AsyncTempHumedad();
        asyncTempHumedad.execute();
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
    }

    private class AsyncTempHumedad extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (true){
                try {
                    Thread.sleep(5000);

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


                } catch (InterruptedException e) {
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
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
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