package com.iesmm.domohome.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos los elementos del layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView= findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Si el bundle es null, nos manda al apartado de Home y lo marcamos como seleccionado
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Segun el item seleccionado, nos envia a su apartado correspondiente
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                break;
            case R.id.nav_dispositivos:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DispositivosInteligentes()).commit();
                break;
            case R.id.nav_routines:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Rutinas()).commit();
                break;
            case R.id.nav_medidas:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Medidas()).commit();
                break;
            case R.id.nav_edit_user:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditarUsuario()).commit();
                break;
            case R.id.nav_logout:
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Si pulsamos la tecla back y el menu esta abierto, lo cierra, sino hace su funcion normal
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }
}