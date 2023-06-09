package com.iesmm.domohome.Vista;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.Controlador.AdaptadorDispositivos;
import com.iesmm.domohome.Controlador.AdaptadorSpinnerDispositivos;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.Modelo.SensorModel;
import com.iesmm.domohome.Modelo.UsuarioModel;
import com.iesmm.domohome.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class AnyadirRutina extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    private Spinner spDispositivo, spType;
    private EditText etFecha, etHora;
    private Button btnAnyadeRutina;
    private List<DispositivoModel> dispositivos;
    private UsuarioModel usuario;
    private AdaptadorSpinnerDispositivos adaptador;
    private Logger logger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el logger
        logger = Logger.getLogger("anyadir_rutina");

        // Cargamos el usuario logueado
        usuario = cargaUsuario();

        // Inicializamos la lista de dispositivos
        dispositivos = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anyadir_rutina, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos los elementos del layout
        spDispositivo = view.findViewById(R.id.spDispositivo);
        spType = view.findViewById(R.id.spType);
        etFecha = view.findViewById(R.id.etFecha);
        etHora = view.findViewById(R.id.etHora);
        btnAnyadeRutina = view.findViewById(R.id.btnAnyadeRutina);

        // Añadimos los listeners
        etFecha.setOnClickListener(this);
        etHora.setOnClickListener(this);
        btnAnyadeRutina.setOnClickListener(this);
        spType.setOnItemSelectedListener(this);

        // Deshabilitamos el teclado para que no se abra en los edittext de fecha y hora
        // Hacemos esto debido a que la fecha y hora la seleccionaremos con el DatePicker y el TimePicker
        etFecha.setShowSoftInputOnFocus(false);
        etHora.setShowSoftInputOnFocus(false);

        // Le asignamos los tipos de rutinas al spinner
        spType.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_spinner, RutinaModel.Tipo.values()));

        // Cargamos los dispositivos
        AsyncCargarDispositivos asyncCargarDispositivos = new AsyncCargarDispositivos();
        asyncCargarDispositivos.execute();
    }

    public UsuarioModel cargaUsuario() {
        // Cargamos el usuario logueado
        UsuarioModel userTemp = null;
        Bundle b = this.getActivity().getIntent().getExtras();
        if (b != null){
            userTemp = (UsuarioModel) b.getSerializable("user");
        }
        return userTemp;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.etFecha:
                // Si pulsamos en el edittext de fecha, creamos un dialogo para obtener la fecha
                creaDialogoFecha();
                break;
            case R.id.etHora:
                // Si pulsamos en el edittext de hora, creamos un dialogo para obtener la hora
                creaDialogoHora();
                break;
            case R.id.btnAnyadeRutina:
                // Si los campos no estan vacios, ejecutamos la tarea asincrona que registra la rutina
                // Sino, muestra un mensaje de error indicando que tiene que rellenar todos los campos
                if (!etFecha.getText().toString().isEmpty() && !etHora.getText().toString().isEmpty()) {
                    AsyncRegistraRutina asyncRegistraRutina = new AsyncRegistraRutina();
                    asyncRegistraRutina.execute();
                }
                else {
                    Snackbar.make(getView().findViewById(R.id.anyadirRutina), getText(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void creaDialogoFecha() {
        // Obtenemos la fecha actual
        Calendar calendar = Calendar.getInstance();
        int anyo = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Creamos el DatePickerDialog
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), this, anyo, mes, dia);

        // Ponemos de fecha mínima la de hoy, debido a que si la pone antes, nunca se ejecutara la rutina
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());

        // Mostramos el DatePickerDialog
        datePicker.show();
    }

    private void creaDialogoHora() {
        // Obtenemos la hora actual
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        // Creamos el TimePickerDialog
        TimePickerDialog timePicker = new TimePickerDialog(getContext(), this, hora, minuto, true);

        // Mostramos el TimePickerDialog
        timePicker.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int anyo, int mes, int dia) {
        // Obtenemos la fecha seleccionada y la ponemos en el edittext correspondiente
        String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + anyo;
        etFecha.setText(fechaSeleccionada);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hora, int minutos) {
        // Obtenemos la hora seleccionada y la ponemos en el edittext correspondiente
        String horaSeleccionada = hora + ":" + minutos;
        etHora.setText(horaSeleccionada);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Si el tipo de rutina seleccionado es guardar medida, ponemos el spinner de dispositivo invisible, sino, lo ponemos visible
        if (spType.getSelectedItem().equals(RutinaModel.Tipo.GUARDAR_MEDIDA)) {
            spDispositivo.setVisibility(View.INVISIBLE);
        }
        else {
            spDispositivo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Si no seleccionamos nada y esta invisible, lo ponemos visible
        if (spDispositivo.getVisibility() == View.INVISIBLE) {
            spDispositivo.setVisibility(View.VISIBLE);
        }
    }

    private class AsyncCargarDispositivos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DAOImpl dao = new DAOImpl();
            // Sacamos la lista de dispositivos del usuario
            dispositivos = dao.getDispositivosUsuario(usuario, getContext());
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Cuando se termine la tarea asincrona, se cargan los datos en el Spinner
            adaptador = new AdaptadorSpinnerDispositivos(dispositivos);
            spDispositivo.setAdapter(adaptador);
        }
    }

    private class AsyncRegistraRutina extends AsyncTask<Void, Boolean, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Boolean correcto = false;
            RutinaModel rutina = null;
            try {
                DAO dao = new DAOImpl();
                // Creamos los SimpleDateFormat para posteriormente parsear la fecha
                SimpleDateFormat sdtFecha = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdtHora = new SimpleDateFormat("HH:mm");

                // Sumamos la fecha mas la hora y la parseamos a timestamp
                Date fecha_hora = new Date(sdtFecha.parse(etFecha.getText().toString()).getTime() + sdtHora.parse(etHora.getText().toString()).getTime());
                Timestamp timestamp = new Timestamp(fecha_hora.getTime());

                if (spType.getSelectedItem().equals(RutinaModel.Tipo.GUARDAR_MEDIDA)) {
                    // Si el tipo es guardar medida, enviamos -1 para no guardar id del dispositivo en la bd debido a que la medida la guarda el sensor
                    // Sacamos el sensor del usuario y creamos una nueva rutina
                    SensorModel sensor = dao.getSensorUsuario(usuario, getContext());
                    rutina = new RutinaModel(0, timestamp.toString(), spType.getSelectedItem().toString(), -1, sensor.getIdSensor());
                }
                else {
                    // Sacamos el dispositivo seleccionado y creamos una nueva rutina
                    DispositivoModel dispositivo = (DispositivoModel) spDispositivo.getSelectedItem();
                    if(dispositivo != null) {
                        // Si el tipo es otro que o sea guardar medida, en el id_sensor guardamos -1 debido a que no necesitamos guardar el sensor
                        rutina = new RutinaModel(0, timestamp.toString(), spType.getSelectedItem().toString(), dispositivo.getIdDispositivo(), -1);
                    }
                    else {
                        Snackbar.make(getView().findViewById(R.id.anyadirRutina), getText(R.string.required_fields), Snackbar.LENGTH_LONG).show();
                    }
                }

                // Registramos la rutina
                correcto = dao.registraRutina(rutina, getContext());
            }
            catch (ParseException e) {
                logger.severe("Error al parsear la fecha/hora");
            }
            catch (Exception e) {
                logger.severe("Error: " + e);
            }

            if (rutina != null) {
                publishProgress(correcto);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            // Si la rutina se ha añadido correctamente, lo mostramos en un mensaje y redirigimos al apartado de rutinas
            // Sino, mostramos un mensaje de error
            if (values[0]) {
                Snackbar.make(getView().findViewById(R.id.anyadirRutina), getText(R.string.routine_registered), Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Rutinas()).commit();
            } else {
                Snackbar.make(getView().findViewById(R.id.anyadirRutina), getText(R.string.routine_not_registered), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}