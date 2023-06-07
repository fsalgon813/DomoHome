package com.iesmm.domohome.Vista;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.ArrayList;

public class AnyadirDispositivo extends Fragment implements View.OnClickListener {

    Spinner spTipo, spMarca;
    Button btnAnyadir;
    UsuarioModel usuario;
    EditText etNombre, etIp, etUsuarioServicio, etPasswdServicio;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuario = cargaUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anyadir_dispositivo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los elementos del layout
        btnAnyadir = view.findViewById(R.id.btnAnyadeDispositivo);
        spTipo = view.findViewById(R.id.spType);
        spMarca = view.findViewById(R.id.spMarca);
        etNombre = view.findViewById(R.id.etName);
        etIp = view.findViewById(R.id.etIp);
        etUsuarioServicio = view.findViewById(R.id.etUsername);
        etPasswdServicio = view.findViewById(R.id.etPasswd);

        ArrayList<String> tipos = new ArrayList<>();
        tipos.add(getContext().getString(R.string.type));
        for (DispositivoModel.Tipo t:DispositivoModel.Tipo.values()){
            tipos.add(t.toString());
        }

        ArrayList<String> marca = new ArrayList<>();
        marca.add(getContext().getString(R.string.brand));
        for (DispositivoModel.Marca m:DispositivoModel.Marca.values()){
            marca.add(m.toString());
        }

        // Añadimos el adaptador al spinner
        spTipo.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_spinner, tipos));
        spMarca.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_spinner, marca));

        // Añadimos el listener al botón
        btnAnyadir.setOnClickListener(this);
    }

    public UsuarioModel cargaUsuario() {
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
            case R.id.btnAnyadeDispositivo:
                // Comprueba que los campos obligatorios no esten vacios
                if (!spTipo.getSelectedItem().equals(getContext().getString(R.string.type)) && !spMarca.getSelectedItem().equals(getContext().getString(R.string.brand)) && !etNombre.getText().toString().isEmpty() && !etIp.getText().toString().isEmpty()) {

                    // Comprueba si es una bombilla tplink y el usuario y la contraseña estan rellenos o si no es una bombilla tplink no hace falta rellenar el usuario y la contraseña
                    if (((spTipo.getSelectedItem().toString().equalsIgnoreCase("bombilla") && spMarca.getSelectedItem().toString().equalsIgnoreCase("tp_link"))
                            && (!etUsuarioServicio.getText().toString().trim().isEmpty() && !etPasswdServicio.getText().toString().trim().isEmpty()))
                            || (!(spTipo.getSelectedItem().toString().equalsIgnoreCase("bombilla") && spMarca.getSelectedItem().toString().equalsIgnoreCase("tp_link")))) {
                        AsyncRegistraDispositivo asyncRegistraDispositivo = new AsyncRegistraDispositivo();
                        asyncRegistraDispositivo.execute();
                    } else {
                        Snackbar.make(getView().findViewById(R.id.anyadirDispositivo), getText(R.string.not_user_passwd), Snackbar.LENGTH_LONG).show();
                    }
                }
                else {
                    Snackbar.make(getView().findViewById(R.id.anyadirDispositivo), getText(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private class AsyncRegistraDispositivo extends AsyncTask<Void, Boolean, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DAO dao = new DAOImpl();
            Boolean correcto = false;
            correcto = dao.registraDispositivo(new DispositivoModel(0, etNombre.getText().toString(), etIp.getText().toString(), spTipo.getSelectedItem().toString(), spMarca.getSelectedItem().toString(), etUsuarioServicio.getText().toString().trim(), etPasswdServicio.getText().toString().trim(), usuario.getId()), getContext());
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                Snackbar.make(getView().findViewById(R.id.anyadirDispositivo), getText(R.string.device_registered), Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DispositivosInteligentes()).commit();
            } else {
                Snackbar.make(getView().findViewById(R.id.anyadirDispositivo), getText(R.string.device_not_registered), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}