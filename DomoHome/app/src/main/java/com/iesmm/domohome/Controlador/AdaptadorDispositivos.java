package com.iesmm.domohome.Controlador;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iesmm.domohome.Modelo.DispositivoModel;
import com.iesmm.domohome.R;

import org.w3c.dom.Text;

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
        switch (dispositivo.getTipo().toUpperCase()){
            case "TV":
                holder.imgDispositivo.setImageResource(R.drawable.tv);
                break;
            case "BOMBILLA":
                AsyncEstadoBombilla asyncEstadoBombilla = new AsyncEstadoBombilla();
                asyncEstadoBombilla.execute(position);
                if (dispositivo.getEstado()){
                    holder.imgDispositivo.setImageResource(R.drawable.bombilla_encendida);
                } else {
                    holder.imgDispositivo.setImageResource(R.drawable.bombilla_apagada);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaDispositivos.size();
    }

    private class AsyncEstadoBombilla extends AsyncTask<Integer, Boolean, Void> {

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
}
