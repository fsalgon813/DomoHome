package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

import org.w3c.dom.Text;

import java.util.List;

public class AdaptadorSpinnerDispositivos extends BaseAdapter implements SpinnerAdapter {

    private List<DispositivoModel> dispositivos;

    public AdaptadorSpinnerDispositivos(List<DispositivoModel> dispositivos) {
        this.dispositivos = dispositivos;
    }



    @Override
    public int getCount() {
        return dispositivos.size();
    }

    @Override
    public Object getItem(int i) {
        return dispositivos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dispositivos.get(i).getIdDispositivo();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Le ponemos el layout personalizado que creamos para los items del spinner
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_spinner, viewGroup, false);

        // Devolvemos el textview de nuestro layout con el nombre del dispositivo
        TextView textView = v.findViewById(R.id.text1);
        textView.setText(dispositivos.get(i).getNombre());
        return textView;
    }
}
