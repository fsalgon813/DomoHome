package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.Modelo.RutinaModel;
import com.iesmm.domohome.R;
import com.iesmm.domohome.Vista.Rutinas;

import java.util.List;
import java.util.logging.Logger;

public class AdaptadorRutinas extends RecyclerView.Adapter<AdaptadorRutinas.ViewHolder> {
    private List<RutinaModel> listaRutinas;
    private Context context;
    private Logger logger;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitulo, txtFecha_Hora;
        private ImageView imgDispositivo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializamos los elementos de la vista
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtFecha_Hora = itemView.findViewById(R.id.txtFecha_Hora);
            imgDispositivo = itemView.findViewById(R.id.imgDispositivo);
        }
    }

    public AdaptadorRutinas(List<RutinaModel> listaRutinas, Context context) {
        this.listaRutinas = listaRutinas;
        this.context = context;

        // Inicializamos el logger
        logger = Logger.getLogger("adaptador_rutinas");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asignamos el layout del item de rutina
        View view = LayoutInflater.from(context).inflate(R.layout.item_rutina, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Cargamos los datos de la rutina
        AsyncCargaDatosRutina asyncCargaDatosRutina = new AsyncCargaDatosRutina(holder);
        asyncCargaDatosRutina.execute(position);

        // Los listener los ponemos en clases anonima para que se pueda acceder a la posicion de la rutina pulsada
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Si hacemos una pulsacion larga sobre una rutina, nos salen 2 alertdialog indicando que si quiere eliminar la rutina
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle(context.getText(R.string.delete_routine));
                builder1.setMessage(context.getText(R.string.message_delete_routine));
                builder1.setPositiveButton(context.getText(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setTitle(context.getText(R.string.delete_routine));
                        builder2.setMessage(context.getText(R.string.message_delete_2));
                        builder2.setPositiveButton(context.getText(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Ejecutamos la tarea asincrona que elimina la rutina
                                AsyncEliminarRutina asyncEliminarRutina = new AsyncEliminarRutina(view);
                                asyncEliminarRutina.execute(position);
                            }
                        });
                        builder2.setNegativeButton(context.getText(R.string.deny), null);
                        builder2.show();
                    }
                });
                builder1.setNegativeButton(context.getText(R.string.deny), null);
                builder1.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    private class AsyncCargaDatosRutina extends AsyncTask<Integer, Void, Void> {
        ViewHolder holder;
        RutinaModel rutina;
        DispositivoModel dispositivo;

        public AsyncCargaDatosRutina(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            DAO dao = new DAOImpl();
            // Sacamos la lista de rutinas del usuario
            rutina = listaRutinas.get(integers[0]);
            // Sacamos el dispositivo para obtener su nombre
            dispositivo = dao.getDispositivoId(rutina.getIdDispositivo(), context);
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Asignamos los datos de la rutina a los elementos del layout
            holder.txtFecha_Hora.setText(rutina.getFecha_hora());
            if (dispositivo != null) {
                holder.txtTitulo.setText(rutina.getTipo().toString() + " " + dispositivo.getNombre());
                if (dispositivo.getTipo().equals(DispositivoModel.Tipo.TV)) {
                    holder.imgDispositivo.setImageResource(R.drawable.tv);
                }
                else if (dispositivo.getTipo().equals(DispositivoModel.Tipo.BOMBILLA)) {
                    if (rutina.getTipo().equals(RutinaModel.Tipo.ENCENDER)) {
                        holder.imgDispositivo.setImageResource(R.drawable.bombilla_encendida);
                    }
                    else {
                        holder.imgDispositivo.setImageResource(R.drawable.bombilla_apagada);
                    }
                }
            }
            else {
                holder.txtTitulo.setText(rutina.getTipo().toString());
            }
        }
    }

    private class AsyncEliminarRutina extends AsyncTask<Integer, Boolean, Void> {

        View view;

        public AsyncEliminarRutina(View view) {
            this.view = view;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            DAO dao = new DAOImpl();
            // Eliminamos la rutina
            Boolean correcto = dao.eliminarRutina(listaRutinas.get(integers[0]), context);

            // Si se ha borrado correctamente, tambien lo quitamos de la lista
            if (correcto) {
                listaRutinas.remove(listaRutinas.get(integers[0]));
            }
            publishProgress(correcto);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                // Actualizamos la lista
                notifyDataSetChanged();

                // Mostramos un mensaje indicando que se ha elimiando correctamente
                Snackbar.make(view, context.getString(R.string.routine_successfully_delete), Snackbar.LENGTH_LONG).show();
            }
            else {
                // Mostramos un mensaje indicando que ha ocurrido un error al eliminar la rutina
                Snackbar.make(view, context.getString(R.string.routine_error_delete), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
