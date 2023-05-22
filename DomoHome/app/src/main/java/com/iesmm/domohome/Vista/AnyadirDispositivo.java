package com.iesmm.domohome.Vista;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

public class AnyadirDispositivo extends Fragment {

    Spinner spTipo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anyadir_dispositivo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spTipo = view.findViewById(R.id.spType);
        spTipo.setSelection(-1);
        spTipo.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, DispositivoModel.Marca.values()));

    }
}