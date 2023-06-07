package com.iesmm.DomoHomeAPI.Utils;

import com.iesmm.DomoHomeAPI.Controller.TempHumedadController;
import com.iesmm.DomoHomeAPI.DAO.DAO;
import com.iesmm.DomoHomeAPI.DAO.DAOImpl;
import com.iesmm.DomoHomeAPI.Model.RutinaModel;
import com.iesmm.DomoHomeAPI.Model.TempHumedadModel;

import java.util.Date;

public class ThreadGuardaMedida implements Runnable {

    private int idSensor;
    DAO dao;

    public ThreadGuardaMedida (int idSensor) {
        dao = new DAOImpl();
        this.idSensor = idSensor;
    }

    @Override
    public void run() {
        TempHumedadModel thModel = new TempHumedadModel();
        thModel.setIdSensor(idSensor);
        thModel.setTemp(Double.parseDouble(TempHumedadController.getTemp()));
        thModel.setHumedad(Double.parseDouble(TempHumedadController.getHumedad()));
        thModel.setFecha_hora(String.valueOf(new Date().getTime()));
        dao.insertarMedida(thModel);
    }
}
