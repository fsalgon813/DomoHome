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
import com.iesmm.domohome.Modelo.TempHumedadModel;
import com.iesmm.domohome.R;

import java.util.List;

public class AdaptadorMedidas extends RecyclerView.Adapter<AdaptadorMedidas.ViewHolder> {
    private List<TempHumedadModel> medidas;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTemp, tvHumedad, tvFechaHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializamos los elementos de la vista
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvHumedad = itemView.findViewById(R.id.tvHumedad);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
        }
    }

    public AdaptadorMedidas(List<TempHumedadModel> medidas, Context context) {
        this.medidas = medidas;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medida, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TempHumedadModel thModel = medidas.get(position);

        holder.tvTemp.setText(thModel.getTemp().toString());
        holder.tvHumedad.setText(thModel.getHumedad().toString());
        holder.tvFechaHora.setText(thModel.getFecha_hora());

    }

    @Override
    public int getItemCount() {
        return medidas.size();
    }
}
