package com.iesmm.domohome.Vista;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.ArrayList;

public class EditarUsuario extends Fragment implements View.OnClickListener {
    EditText etUsername, etPasswd, etNombre;
    Spinner spRol;
    UsuarioModel usuario;
    Button btnUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargamos el usuario
        usuario = cargaUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los componentes del layout
        etUsername = view.findViewById(R.id.etUsername);
        etPasswd = view.findViewById(R.id.etPasswd);
        etNombre = view.findViewById(R.id.etName);
        spRol = view.findViewById(R.id.spRol);
        btnUpdate = view.findViewById(R.id.btnActualiza);

        // Asignamos al spinner los datos
        // Primero sacamos los strings
        ArrayList<String> roles = new ArrayList<>();
        roles.add(getContext().getString(R.string.rol));
        for (UsuarioModel.Rol r : UsuarioModel.Rol.values()){
            roles.add(r.toString());
        }

        // Despues, le asignamos el adaptador
        spRol.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_spinner, roles));

        etUsername.setText(usuario.getUsername());
        etPasswd.setText(usuario.getPassword());
        etNombre.setText(usuario.getNombre());
        if (usuario.getRol().equals(UsuarioModel.Rol.ADMIN)){
            spRol.setSelection(2);
        }
        else {
            spRol.setSelection(1);
        }


        // Asignamos el listener al boton
        btnUpdate.setOnClickListener(this);
    }

    public UsuarioModel cargaUsuario() {
        UsuarioModel userTemp = null;
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            userTemp = (UsuarioModel) b.getSerializable("user");
        }
        return userTemp;
    }

    public void guardaUsuario(UsuarioModel usuarioActualizado) {
        // Sacamos el bundle de nuestro intent y guardamos el nuevo usuario
        Bundle b = this.getActivity().getIntent().getExtras();
        b.putSerializable("user", usuarioActualizado);

        // Guardamos el bundle con el nuevo usuario en nuestro intent actual(para no viajar a otra clase)
        this.getActivity().getIntent().putExtras(b);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnActualiza:
                if (!etUsername.getText().toString().isEmpty() && !etNombre.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty() && !spRol.getSelectedItem().equals(getText(R.string.rol))) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setTitle(getText(R.string.edit_user));
                    builder1.setMessage(getText(R.string.message_edit_user));
                    builder1.setPositiveButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AsyncActualizaUsuario asyncActualizaUsuario = new AsyncActualizaUsuario();
                            asyncActualizaUsuario.execute();
                        }
                    });
                    builder1.setNegativeButton(getText(R.string.deny), null);
                    builder1.show();
                }
                else {
                    // Mostramos un mensaje indicando que deben estar rellenos todos los campos
                    Snackbar.make(getActivity().findViewById(R.id.editarUsuario), getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private class AsyncActualizaUsuario extends AsyncTask<Void, Boolean, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Creamos el modelo del usuario a actualizar
            UsuarioModel usuarioActualiza = new UsuarioModel(usuario.getId(), etNombre.getText().toString(), etUsername.getText().toString(), etPasswd.getText().toString(), spRol.getSelectedItem().toString(), usuario.getId_casa());

            DAO dao = new DAOImpl();
            Boolean correcto = dao.actualizaUsuario(usuarioActualiza);

            // Si se ha actualizado correctamente, guardamos el nuevo usuario
            if (correcto) {
                guardaUsuario(usuarioActualiza);
            }

            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                // Mostramos un mensaje indicando que se ha actualizado correctamente
                Snackbar.make(getActivity().findViewById(R.id.editarUsuario), getString(R.string.user_successfully_update), Snackbar.LENGTH_LONG).show();

                // Redirigimos a otra pantalla
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            }
            else {
                // Mostramos un mensaje indicando que ha ocurrido un error al actualizar el usuario
                Snackbar.make(getActivity().findViewById(R.id.editarUsuario), getString(R.string.user_error_update), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}