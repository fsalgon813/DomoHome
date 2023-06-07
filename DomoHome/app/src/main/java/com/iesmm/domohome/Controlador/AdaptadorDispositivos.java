package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.iesmm.domohome.DAO.DAO;
import com.iesmm.domohome.DAO.DAOImpl;
import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

import java.util.List;

public class AdaptadorDispositivos extends RecyclerView.Adapter<AdaptadorDispositivos.ViewHolder> {
    private List<DispositivoModel> listaDispositivos;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombreDispositivo;
        private ImageView imgDispositivo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializamos los elementos de la vista
            txtNombreDispositivo = itemView.findViewById(R.id.txtNombreDispositivo);
            imgDispositivo = itemView.findViewById(R.id.imgDispositivo);
        }
    }

    public AdaptadorDispositivos(List<DispositivoModel> listaDispositivos, Context context) {
        this.listaDispositivos = listaDispositivos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dispositivo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DispositivoModel dispositivo = listaDispositivos.get(position);

        holder.txtNombreDispositivo.setText(dispositivo.getNombre());
        if (dispositivo.getTipo().equals(DispositivoModel.Tipo.TV)) {
            holder.imgDispositivo.setImageResource(R.drawable.tv);
        }
        else if (dispositivo.getTipo().equals(DispositivoModel.Tipo.BOMBILLA)) {
            if (dispositivo.getMarca().equals(DispositivoModel.Marca.TP_LINK)){
                AsyncEstadoBombillaTpLink asyncEstadoBombillaTpLink = new AsyncEstadoBombillaTpLink();
                asyncEstadoBombillaTpLink.execute(position);
            }
            if (dispositivo.getEstado()){
                holder.imgDispositivo.setImageResource(R.drawable.bombilla_encendida);
            } else {
                holder.imgDispositivo.setImageResource(R.drawable.bombilla_apagada);
            }
        }

        // Los listener los ponemos en clases anonima para que se pueda acceder a la posicion del dispositivo pulsado
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dispositivo.getTipo().equals(DispositivoModel.Tipo.TV)) {
                    if (dispositivo.getMarca().equals(DispositivoModel.Marca.SAMSUNG)){
                        AsyncOnOffTvSamsung asyncOnOffTvSamsung = new AsyncOnOffTvSamsung();
                        asyncOnOffTvSamsung.execute(position);
                    }
                }
                else if (dispositivo.getTipo().equals(DispositivoModel.Tipo.BOMBILLA)) {
                    if (dispositivo.getMarca().equals(DispositivoModel.Marca.TP_LINK)){
                        AsyncOnOffBombillaTpLink asyncOnOffBombillaTpLink = new AsyncOnOffBombillaTpLink();
                        asyncOnOffBombillaTpLink.execute(position);
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle(context.getText(R.string.delete_device));
                builder1.setMessage(context.getText(R.string.message_delete_device));
                builder1.setPositiveButton(context.getText(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setTitle(context.getText(R.string.delete_device));
                        builder2.setMessage(context.getText(R.string.message_delete_2));
                        builder2.setPositiveButton(context.getText(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Ejecutamos la tarea asincrona que elimina el dispositivo
                                AsyncEliminarDispositivo asyncEliminarDispositivo = new AsyncEliminarDispositivo(view);
                                asyncEliminarDispositivo.execute(position);
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
        return listaDispositivos.size();
    }

    private class AsyncEstadoBombillaTpLink extends AsyncTask<Integer, Boolean, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            DAOImpl dao = new DAOImpl();
            Boolean estado = dao.getEstadoBombillaTpLink(listaDispositivos.get(integers[0]), context);
            listaDispositivos.get(integers[0]).setEstado(estado);
            publishProgress(estado);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            // Si el estado es true, se llama al metodo notifyDataSetChanged() para que se
            // actualice el adaptador y asi se ponen los iconos encendidos o apagados
            notifyDataSetChanged();
        }
    }

    private class AsyncOnOffTvSamsung extends AsyncTask<Integer, Boolean, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            try{
                Thread.sleep(1500);
            }
            catch (InterruptedException e) {
                System.out.println("asd");
            }
            DAOImpl dao = new DAOImpl();
            Boolean correcto = dao.onoffTvSamsung(listaDispositivos.get(integers[0]), context);
            publishProgress(correcto);
            return null;
        }
    }

    private class AsyncOnOffBombillaTpLink extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            DAO dao = new DAOImpl();
            dao.onoffBombillaTpLink(listaDispositivos.get(integers[0]), context);
            publishProgress(integers[0]);
            return null;
        }
    }

    private class AsyncEliminarDispositivo extends AsyncTask<Integer, Boolean, Void> {

        View view;

        public AsyncEliminarDispositivo(View view) {
            this.view = view;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            DAO dao = new DAOImpl();
            Boolean correcto = dao.eliminarDispositivo(listaDispositivos.get(integers[0]), context);
            // Si se ha borrado correctamente, tambien lo quitamos de la lista
            if (correcto) {
                listaDispositivos.remove(listaDispositivos.get(integers[0]));
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
                Snackbar.make(view, context.getString(R.string.device_successfully_delete), Snackbar.LENGTH_LONG).show();
            }
            else {
                // Mostramos un mensaje indicando que ha ocurrido un error al eliminar el dispositivo
                Snackbar.make(view, context.getString(R.string.device_error_delete), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
