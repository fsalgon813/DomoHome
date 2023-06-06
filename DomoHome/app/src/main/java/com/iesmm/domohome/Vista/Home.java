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

import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.CasaModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.logging.Logger;

public class Home extends Fragment {

    private TextView temp, humedad, nombreCasa, codInvitacion;
    private Logger logger;
    private UsuarioModel usuario;
    AsyncTempHumedad asyncTempHumedad;
    AsyncCargaCasa asyncCargaCasa;

    private DAO dao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializamos el logger
        logger = Logger.getLogger("Home");;

        // Inicializamos el controlador
        dao = new DAOImpl();

        // Cargamos el usuario que se ha logueado
        usuario = cargaUsuario();
        logger.info("Usuario logueado: " + usuario.getUsername());

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



    public UsuarioModel cargaUsuario() {
        UsuarioModel userTemp = null;
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            userTemp = (UsuarioModel) b.getSerializable("user");
        }
        return userTemp;
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
            publishProgress(dao.getCasa(usuario.getUsername()));
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
                publishProgress( dao.getTemp(), dao.getHumedad());
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    logger.severe("Se ha interrumpido la lectura de temperatura y humedad");
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