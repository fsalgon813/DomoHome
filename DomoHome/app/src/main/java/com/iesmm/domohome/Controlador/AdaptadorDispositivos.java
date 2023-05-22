package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // El onclick lo ponemos en una clase anonima para que se pueda acceder a la posicion
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
    }

    @Override
    public int getItemCount() {
        return listaDispositivos.size();
    }

    private class AsyncEstadoBombillaTpLink extends AsyncTask<Integer, Boolean, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            Controlador controlador = new Controlador();
            Boolean estado = controlador.getEstadoBombillaTpLink(listaDispositivos.get(integers[0]));
            listaDispositivos.get(integers[0]).setEstado(estado);
            publishProgress(estado);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            // Si el estado es true, se llama al metodo notifyDataSetChanged() para que se
            // actualice el adaptador y asi se ponen los iconos encendidos o apagados
            if (values[0]){
                notifyDataSetChanged();
            }
        }
    }

    private class AsyncOnOffTvSamsung extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            Controlador controlador = new Controlador();
            controlador.onoffTvSamsung(listaDispositivos.get(integers[0]));
            return null;
        }
    }

    private class AsyncOnOffBombillaTpLink extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            Controlador controlador = new Controlador();
            controlador.onoffBombillaTpLink(listaDispositivos.get(integers[0]));
            publishProgress(integers[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(listaDispositivos.get(values[0]).getEstado()) {
                Toast.makeText(context, context.getText(R.string.bulb_off), Toast.LENGTH_SHORT).show();
                listaDispositivos.get(values[0]).setEstado(false);
            }
            else {
                Toast.makeText(context, context.getText(R.string.bulb_on), Toast.LENGTH_SHORT).show();
                listaDispositivos.get(values[0]).setEstado(true);
            }
            notifyDataSetChanged();
        }
    }
}
