package com.iesmm.domohome.Vista;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iesmm.domohome.Controlador.AdaptadorDispositivos;
import com.iesmm.domohome.Controlador.Controlador;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DispositivosInteligentes extends Fragment implements View.OnClickListener {

    private RecyclerView rv;
    private Button btnAnyadirDispositivo;
    private List<DispositivoModel> dispositivos = new ArrayList<DispositivoModel>();
    private Logger logger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el logger
        logger = Logger.getLogger("DispositivosInteligentes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dispositivos_inteligentes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los componentes del layout
        rv = view.findViewById(R.id.rvDispositivos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAnyadirDispositivo = view.findViewById(R.id.btnAnyadeDispositivo);

        // Cargamos los dispositivos
        AsyncCargarDispositivos asyncCargarDispositivos = new AsyncCargarDispositivos();
        asyncCargarDispositivos.execute();

        // Añadimos el listener al botón
        btnAnyadirDispositivo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnAnyadeDispositivo:
                // Cambiamos de fragment al de añadir dispositivo
                Fragment fragment = new AnyadirDispositivo();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                break;
        }
    }

    private class AsyncCargarDispositivos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Controlador controlador = new Controlador();
            dispositivos = controlador.getDispositivos();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Cuando se termine la tarea asincrona, se cargan los datos en el RecyclerView
            rv.setAdapter(new AdaptadorDispositivos(dispositivos, getContext()));
        }
    }
}