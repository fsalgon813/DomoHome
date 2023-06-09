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

import com.iesmm.domohome.Controlador.AdaptadorRutinas;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Rutinas extends Fragment implements View.OnClickListener {
    private Logger logger;
    private RecyclerView rv;
    private Button btnAnyadeRutina;
    UsuarioModel usuario;
    private List<RutinaModel> rutinas = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el logger
        logger = Logger.getLogger("rutinas");

        // Cargamos el usuario
        usuario = cargaUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rutinas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los componentes del layout
        btnAnyadeRutina = view.findViewById(R.id.btnAnyadeRutina);
        rv = view.findViewById(R.id.rvRoutines);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargamos las rutinas
        AsyncCargaRutinas asyncCargaRutinas = new AsyncCargaRutinas();
        asyncCargaRutinas.execute();

        // Asignamos el listener al boton
        btnAnyadeRutina.setOnClickListener(this);
    }

    public UsuarioModel cargaUsuario() {
        // Cargamos el usuario que ha iniciado sesion
        UsuarioModel userTemp = null;
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            userTemp = (UsuarioModel) b.getSerializable("user");
        }
        return userTemp;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnAnyadeRutina:
                // Cambiamos de fragment al de añadir rutina
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AnyadirRutina()).commit();
                break;
        }
    }

    private class AsyncCargaRutinas extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DAO dao = new DAOImpl();
            // Obtenemos las rutinas del usuario
            rutinas = dao.getRutinas(usuario, getContext());
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Cargamos las rutinas en el recyclerview
            rv.setAdapter(new AdaptadorRutinas(rutinas, getContext()));
        }
    }
}