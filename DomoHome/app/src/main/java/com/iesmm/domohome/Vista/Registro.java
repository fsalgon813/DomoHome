package com.iesmm.domohome.Vista;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.R;

import java.util.logging.Logger;

public class Registro extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private EditText etUsername, etPasswd, etNombre;
    private String codInvitacion = null, nombreCasa = null;
    private TextView tvLogin;
    private Button btnRegister;
    private Logger logger;
    private DAOImpl dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Asigno las variables a los elementos del layout
        etUsername = findViewById(R.id.etUsername);
        etPasswd = findViewById(R.id.etPasswd);
        etNombre = findViewById(R.id.etName);
        tvLogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Asigno el listener a los elementos del layout
        tvLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        // Inicializamos el logger
        logger = Logger.getLogger("Register");

        // Inicializamos el controlador
        dao = new DAOImpl();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tvLogin:
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                break;
            case R.id.btnRegister:
                // Se vuelven a poner a null por si el usuario ha cancelado la operacion y ha vuelto a pulsar el boton
                codInvitacion = null;
                nombreCasa = null;
                if (!etNombre.getText().toString().isEmpty() && !etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty()){
                    // Preguntamos al usuario mediante AlertDialog si tiene un codigo de invitacion o quiere crear una casa nueva
                    AlertDialog.Builder builderOpcion = new AlertDialog.Builder(this);
                    builderOpcion.setTitle(R.string.select_option);
                    builderOpcion.setMessage(R.string.code_house);
                    builderOpcion.setPositiveButton(R.string.code, this);
                    builderOpcion.setNegativeButton(R.string.new_house, this);
                    builderOpcion.show();
                }
                else {
                    Snackbar.make(findViewById(R.id.register), getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }



    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // Si el usuario tiene un codigo de invitacion, se pide el codigo con otro alertdialog
        if (i == DialogInterface.BUTTON_POSITIVE){
            AlertDialog.Builder builderCodigo = new AlertDialog.Builder(this);
            builderCodigo.setTitle(R.string.input_code);
            EditText etCodigo = new EditText(getApplicationContext());
            etCodigo.setTextColor(Color.WHITE);
            etCodigo.setInputType(InputType.TYPE_CLASS_TEXT);
            builderCodigo.setView(etCodigo);
            builderCodigo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    codInvitacion = etCodigo.getText().toString();
                    AsyncRegister asyncRegister = new AsyncRegister();
                    asyncRegister.execute();
                }
            }).show();
        }
        else if (i == DialogInterface.BUTTON_NEGATIVE){
            // Si el usuario no tiene un codigo de invitacion, se pide el nombre de la casa con otro alertdialog
            AlertDialog.Builder builderNombre = new AlertDialog.Builder(this);
            builderNombre.setTitle(R.string.input_name);
            EditText etNombre = new EditText(getApplicationContext());
            etNombre.setTextColor(Color.WHITE);
            etNombre.setInputType(InputType.TYPE_CLASS_TEXT);
            builderNombre.setView(etNombre);
            builderNombre.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    nombreCasa = etNombre.getText().toString();
                    AsyncRegister asyncRegister = new AsyncRegister();
                    asyncRegister.execute();
                }
            }).show();
        }

    }

    private class AsyncRegister extends AsyncTask<Void, Boolean, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RegisterParams registerParams = null;
            if (codInvitacion != null && !codInvitacion.isEmpty() && !etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty() && !etNombre.getText().toString().isEmpty()){
                registerParams = new RegisterParams(etUsername.getText().toString().trim(), etPasswd.getText().toString().trim(), etNombre.getText().toString(), null, codInvitacion);
            }
            else if (nombreCasa != null && !nombreCasa.isEmpty() && !etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty() && !etNombre.getText().toString().isEmpty()) {
                registerParams = new RegisterParams(etUsername.getText().toString().trim(), etPasswd.getText().toString().trim(), etNombre.getText().toString(), nombreCasa, null);
            }
            else if ((nombreCasa == null || nombreCasa.isEmpty()) || (codInvitacion == null || codInvitacion.isEmpty())){
                Snackbar.make(findViewById(R.id.register), getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
            }

            Boolean correcto = false;

            if (registerParams != null) {
                if (dao.registraUsuario(registerParams, Registro.this.getApplicationContext())){
                    correcto = true;
                }

                publishProgress(correcto);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            // Si el registro ha sido correcto, se redirige a la pantalla principal
            if (values[0]){
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
            else {
                Snackbar.make(findViewById(R.id.register), getString(R.string.error_register), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}