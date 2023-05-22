package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

import java.util.List;

public class AdaptadorTipos extends ArrayAdapter<String> {

    List<String> tipos;

    public AdaptadorTipos(@NonNull Context context, int resource) {
        super(context, resource);
        tipos.add(context.getString(R.string.type));
        for (DispositivoModel.Tipo tipo : DispositivoModel.Tipo.values()) {
            add(tipo.toString());
        }
    }
}
