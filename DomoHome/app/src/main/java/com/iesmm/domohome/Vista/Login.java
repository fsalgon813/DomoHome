package com.iesmm.domohome.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.Controlador.Controlador;
import com.iesmm.domohome.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPasswd;
    private TextView tvRegister;
    private Button btnLogin;
    private Logger logger;
    private Controlador controlador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Asigno las variables a los elementos del layout
        etUsername = findViewById(R.id.etUsername);
        etPasswd = findViewById(R.id.etPasswd);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Asigno el listener a los elementos del layout
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Inicializamos el logger
        logger = Logger.getLogger("Login");

        // Inicializamos el controlador
        controlador = new Controlador();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tvRegister:
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
                break;
            case R.id.btnLogin:
                comprobarUsernameAsync comprobarUsernameAsync = new comprobarUsernameAsync();
                comprobarUsernameAsync.execute(etUsername.getText().toString(), etPasswd.getText().toString());
                break;
        }
    }

    private class comprobarUsernameAsync extends AsyncTask<String, Boolean, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            Boolean correcto = false;
            if (controlador.verificaUsuario(strings[0], strings[1])){
                correcto = true;
            } else {
                Snackbar.make(findViewById(R.id.login), getString(R.string.error_login), Snackbar.LENGTH_LONG).show();
            }
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0] == true){
                Bundle b = new Bundle();
                b.putString("username", etUsername.getText().toString());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        }
    }
}