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
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.util.logging.Logger;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPasswd;
    private TextView tvRegister;
    private Button btnLogin;
    private Logger logger;
    private UsuarioModel usuario = null;


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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tvRegister:
                Intent i = new Intent(getApplicationContext(), Registro.class);
                startActivity(i);
                break;
            case R.id.btnLogin:
                // Comprobamos que no esten vacios los campos de usuario y contraseña
                if (!etUsername.getText().toString().isEmpty() && !etPasswd.getText().toString().isEmpty()) {
                    comprobarUsernameAsync comprobarUsernameAsync = new comprobarUsernameAsync();
                    comprobarUsernameAsync.execute(etUsername.getText().toString(), etPasswd.getText().toString());
                }
                else {
                    Snackbar.make(findViewById(R.id.login), getString(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private class comprobarUsernameAsync extends AsyncTask<String, Boolean, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            Boolean correcto = false;
            DAOImpl dao = new DAOImpl();
            // Comprobamos el usuario(le quitamos los espacios del principio y del final)
            usuario = dao.getUsuarioUsername(strings[0].trim());
            // Si el usuario no es null quiere decir que el username es correcto
            if (usuario != null){
                // Comprobamos que la contraseña es igual
                if (usuario.getPassword().equals(strings[1])){
                    correcto = true;
                }
            }
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0] == true){
                Bundle b = new Bundle();
                b.putSerializable("user", usuario);
                if (usuario.getRol().equals(UsuarioModel.Rol.ADMIN)) {
                    Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                    i.putExtras(b);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtras(b);
                    startActivity(i);
                }
            }
            else {
                Snackbar.make(findViewById(R.id.login), getString(R.string.error_login), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}