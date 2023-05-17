package com.iesmm.domohome.Vista;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iesmm.domohome.Modelo.RegisterParams;
import com.iesmm.domohome.R;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private EditText etUsername, etPasswd, etNombre;
    private String codInvitacion = null, nombreCasa = null;
    private TextView tvLogin;
    private Button btnRegister;
    private Logger logger;
    private OkHttpClient client;

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

        // Inicializamos el cliente HTTP que nos permitira hacer peticiones a la API
        client = new OkHttpClient();


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
                if (!etNombre.getText().toString().isEmpty() || !etUsername.getText().toString().isEmpty() || !etPasswd.getText().toString().isEmpty()){
                    // Preguntamos al usuario mediante AlertDialog si tiene un codigo de invitacion o quiere crear una casa nueva
                    AlertDialog.Builder builderOpcion = new AlertDialog.Builder(this);
                    builderOpcion.setTitle(R.string.select_option);
                    builderOpcion.setMessage(R.string.code_house);
                    builderOpcion.setPositiveButton(R.string.code, this);
                    builderOpcion.setNegativeButton(R.string.new_house, this);
                    builderOpcion.show();
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
            // Si el usuario ha introducido todos los datos, se envian los datos a la API para hacer el registro
            String url = "http://192.168.0.89:8081/usuario/registrarUsuario";
            MediaType tipo = MediaType.parse("application/json; charset=utf-8");
            RegisterParams registerParams = null;
            if (codInvitacion != null && !codInvitacion.isEmpty() && !etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty() && !etNombre.getText().toString().isEmpty()){
                registerParams = new RegisterParams(etUsername.getText().toString(), etPasswd.getText().toString(), etNombre.getText().toString(), null, codInvitacion);
            }
            else if (nombreCasa != null && !nombreCasa.isEmpty() && !etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty() && !etNombre.getText().toString().isEmpty()) {
                registerParams = new RegisterParams(etUsername.getText().toString(), etPasswd.getText().toString(), etNombre.getText().toString(), nombreCasa, null);
            }

            // Convertimos el objeto RegisterParams a JSON usando GSON
            Gson gson = new GsonBuilder().create();
            String registerParamsJSON = gson.toJson(registerParams);
            RequestBody cuerpo = RequestBody.create(registerParamsJSON, tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            try {
                // Ejecutamos la peticion y obtenemos la respuesta
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    Boolean correcto = false;
                    if (response.body().string().equals("true")){
                        correcto = true;
                    }
                    publishProgress(correcto);
                }
            }
            catch (IOException e) {
                logger.severe("Error en la E/S al hacer la peticion HTTP");
            }
            catch (Exception e) {
                logger.severe("Error: " + e.getMessage());
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