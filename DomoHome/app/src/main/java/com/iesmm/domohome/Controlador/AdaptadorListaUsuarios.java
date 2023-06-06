package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.List;

public class AdaptadorListaUsuarios extends ArrayAdapter<UsuarioModel> {
    List<UsuarioModel> usuarios;

    public AdaptadorListaUsuarios(@NonNull Context context, List<UsuarioModel> usuarios) {
        super(context, 0, usuarios);
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UsuarioModel usuario = usuarios.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_usuario, parent, false);

        TextView txtUsername = convertView.findViewById(R.id.txtUsername);
        txtUsername.setText(usuario.getUsername());

        return convertView;
    }
}
