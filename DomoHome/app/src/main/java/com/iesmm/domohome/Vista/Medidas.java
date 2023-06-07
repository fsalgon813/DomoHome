package com.iesmm.domohome.Vista;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iesmm.domohome.Controlador.AdaptadorDispositivos;
import com.iesmm.domohome.Controlador.AdaptadorMedidas;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Medidas extends Fragment {
    private RecyclerView rv;
    private List<TempHumedadModel> medidas = new ArrayList<>();
    private Logger logger;
    private UsuarioModel usuario;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el logger
        logger = Logger.getLogger("medidas");

        // Cargamos el usuario
        usuario = cargaUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medidas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los componentes del layout
        rv = view.findViewById(R.id.rvMedidas);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargamos las medidas del usuario
        AsyncCargarMedidas asyncCargarMedidas = new AsyncCargarMedidas();
        asyncCargarMedidas.execute();
    }

    public UsuarioModel cargaUsuario() {
        UsuarioModel userTemp = null;
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            userTemp = (UsuarioModel) b.getSerializable("user");
        }
        return userTemp;
    }

    private class AsyncCargarMedidas extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DAO dao = new DAOImpl();
            medidas = dao.listarMedidasUsuario(usuario, getContext());
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Cuando se termine la tarea asincrona, se cargan los datos en el RecyclerView
            rv.setAdapter(new AdaptadorMedidas(medidas, getContext()));
        }
    }
}