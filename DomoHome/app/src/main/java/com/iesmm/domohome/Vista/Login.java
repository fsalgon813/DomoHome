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
    private OkHttpClient client;

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
        etUsername.setOnClickListener(this);
        etPasswd.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Inicializamos el logger
        logger = Logger.getLogger("Login");

        // Inicializamos el cliente HTTP que nos permitira hacer peticiones a la API
        client = new OkHttpClient();
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
            // Preparamos la peticion
            String url = "http://192.168.0.89:8081/usuario/filtrarUsername";
            MediaType tipo = MediaType.parse("text/plain; charset=utf-8");
            RequestBody cuerpo = RequestBody.create(strings[0], tipo);
            Request request = new Request.Builder().url(url).post(cuerpo).build();

            // Ejecutamos la peticion y obtenemos la respuesta
            try {
                Response response = client.newCall(request).execute();
                // Si la respuesta es correcta, comprobamos la contraseña
                if (response.isSuccessful()){
                    String respuestaJson = response.body().string();
                    JSONObject usuario = new JSONObject(respuestaJson);
                    String username = usuario.getString("username");
                    String passwd = usuario.getString("password");

                    if (username.equals(strings[0]) && passwd.equals(strings[1])){
                        correcto = true;
                    }
                    else {
                        Snackbar.make(findViewById(R.id.login), "Usuario o contraseña incorrectos", Snackbar.LENGTH_LONG).show();
                    }
                }
            } catch (IOException e) {
                logger.severe("Error en la E/S al hacer la peticion HTTP");
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0] == true){
                Intent i = new Intent(getApplicationContext(), Main.class);
                startActivity(i);
            }
        }
    }
}