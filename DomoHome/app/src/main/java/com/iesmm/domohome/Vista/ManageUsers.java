package com.iesmm.domohome.Vista;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.Controlador.AdaptadorListaUsuarios;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.List;

public class ManageUsers extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    List<UsuarioModel> usuarios;
    UsuarioModel usuario;
    ListView lv;
    AdaptadorListaUsuarios adaptador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usuario = cargaUsuario();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los componentes del layout
        lv = view.findViewById(R.id.listaUsuarios);

        // Cargamos los usuarios en la lista
        AsyncCargaUsuarios asyncCargaUsuarios = new AsyncCargaUsuarios();
        asyncCargaUsuarios.execute();

        // Asignamos los Listener a la lista
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle b = new Bundle();
        b.putSerializable("usuarioActualizar", usuarios.get(i));
        UpdateUser updateUser = new UpdateUser();
        updateUser.setArguments(b);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, updateUser).commit();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle(getText(R.string.delete_user));
        builder1.setMessage(getText(R.string.message_delete_user));
        builder1.setPositiveButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle(getText(R.string.delete_user));
                builder2.setMessage(getText(R.string.message_delete_2));
                builder2.setPositiveButton(getText(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Ejecutamos la tarea asincrona que elimina el usuario
                        AsyncBorraUsuario asyncBorraUsuario = new AsyncBorraUsuario();
                        asyncBorraUsuario.execute(position);
                    }
                });
                builder2.setNegativeButton(getText(R.string.deny), null);
                builder2.show();
            }
        });
        builder1.setNegativeButton(getText(R.string.deny), null);
        builder1.show();
        return false;
    }

    private class AsyncCargaUsuarios extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            DAO dao = new DAOImpl();
            usuarios = dao.listarUsuarios();

            // Si el id es el de el usuario logueado, lo eliminamos, para que no salga el en la lista
            for (int n = 0; n < usuarios.size(); n++) {
                if (usuarios.get(n).getId() == usuario.getId()) {
                    usuarios.remove(n);
                }
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adaptador = new AdaptadorListaUsuarios(getContext(), usuarios);
            lv.setAdapter(adaptador);
        }
    }

    private class AsyncBorraUsuario extends AsyncTask<Integer, Boolean, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            DAO dao = new DAOImpl();
            Boolean correcto = dao.eliminarUsuario(usuarios.get(integers[0]));
            usuarios.remove(integers[0]);
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                // Actualizamos la lista
                adaptador.clear();
                adaptador.addAll(usuarios);
                adaptador.notifyDataSetChanged();

                // Mostramos un mensaje indicando que se ha elimiando correctamente
                Snackbar.make(getActivity().findViewById(R.id.manage_users), getString(R.string.user_successfully_delete), Snackbar.LENGTH_LONG).show();
            }
            else {
                // Mostramos un mensaje indicando que ha ocurrido un error al eliminar el usuario
                Snackbar.make(getActivity().findViewById(R.id.manage_users), getString(R.string.user_error_delete), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}