package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.CasaModel;
import com.iesmm.DomoHomeAPI.Model.RegisterParams;
import com.iesmm.DomoHomeAPI.Model.SensorModel;
import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import com.iesmm.DomoHomeAPI.Utils.Conexion;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class DAOImpl implements DAO {

    private static Logger logger = Logger.getLogger("dao");

    /**
     * Filtra un usuario por su username
     *
     * @return username Usuario filtrado(si no existe devuelve null)
     */
    @Override
    public UsuarioModel filtrarPorUsername(String username) {
        Connection conexion = null;
        UsuarioModel usuario = null;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta del usuario
            String sql = "SELECT * FROM usuarios WHERE username LIKE ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, username);

            // Ejecutamos la consulta
            ResultSet rs = statement.executeQuery();

            // Si existe el usuario, lo guardamos en el objeto usuario
            if (rs.next()) {
                usuario = new UsuarioModel();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("passwd"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setId_casa(rs.getInt("id_casa"));
                logger.info("Inicio de sesion correcto para el usuario " + username);
            }
            else {
                logger.info("Inicio de sesion fallido para el usuario " + username);
            }
        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar un usuario por username. Mensaje: " + e.getMessage());
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
        }
        catch (Exception e){
            logger.severe("Error: " + e.getMessage());
        }
        finally {
            try {
                conexion.close();
            }
            catch (SQLException e){
                logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            }
        }
        return usuario;
    }

    /**
     * Filtra una casa segun el username del usuario perteneciente a la casa
     *
     * @return casa Casa a la que pertenece el usuario con el username especificado(si no existe devuelve null)
     */
    public CasaModel filtrarCasaPorUsername(String username){
        Connection conexion = null;
        CasaModel casa = null;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de la casa
            String sql = "SELECT * FROM casas WHERE id_casa = (SELECT id_casa FROM usuarios WHERE username LIKE ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, username);

            // Ejecutamos la consulta
            ResultSet rs = statement.executeQuery();

            // Si existe la casa, lo guardamos en el objeto casa
            if (rs.next()){
                casa = new CasaModel();
                casa.setIdCasa(rs.getInt("id_casa"));
                casa.setNombre(rs.getString("nombre"));
                casa.setCodInvitacion(rs.getString("cod_invitacion"));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar un usuario por username. Mensaje: " + e.getMessage());
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
        }
        catch (Exception e){
            logger.severe("Error: " + e.getMessage());
        }
        finally {
            try {
                conexion.close();
            }
            catch (SQLException e){
                logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            }
        }
        return casa;
    }

    /**
     * Inserta un usuario en la base de datos
     * Si el codigo de invitacion no es null y es correcto, se inserta el usuario en la casa correspondiente
     * Sino, se inserta el usuario en una casa nueva
     *
     * @return correcto True si se ha insertado correctamente, False si no
     */
    public Boolean registrarUsuario(RegisterParams params){
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            CasaModel casa = null;
            // Generamos la consulta de la casa (si existe) a partir del codigo de invitacion
            if (params.getCodInvitacion() != null || params.getCodInvitacion() != ""){
                String sql = "SELECT * FROM casas WHERE cod_invitacion LIKE ?";
                PreparedStatement statement = conexion.prepareStatement(sql);
                statement.setString(1, params.getCodInvitacion());
                ResultSet rs = statement.executeQuery();
                if (rs.next()){
                    casa = new CasaModel();
                    casa.setIdCasa(rs.getInt("id_casa"));
                    casa.setNombre(rs.getString("nombre"));
                    casa.setCodInvitacion(rs.getString("cod_invitacion"));
                }
            }
            // Si no existe la casa, creamos una nueva
            if (casa == null){
                casa = new CasaModel();
                casa.setIdCasa(generarIdCasa(conexion));
                casa.setNombre(params.getNombreCasa());
                casa.setCodInvitacion(generarCodigoInvitacion(conexion));
                // Insertamos la casa en la base de datos
                String sql = "INSERT INTO casas (id_casa, nombre, cod_invitacion) VALUES (?, ?, ?)";
                PreparedStatement statementCasa = conexion.prepareStatement(sql);
                statementCasa.setInt(1, casa.getIdCasa());
                statementCasa.setString(2, casa.getNombre());
                statementCasa.setString(3, casa.getCodInvitacion());
                statementCasa.executeUpdate();

                // Cuando se crea una casa, tambien se crea el sensor de temperatura y humedad para esa casa(con el pin por defecto)
                SensorModel sensor = new SensorModel();
                sensor.setIdSensor(generarIdSensor(conexion));
                sensor.setTipo("temperatura_humedad");
                sensor.setIdCasa(casa.getIdCasa());
                sql = "INSERT INTO sensores (id_sensor, pin, tipo, id_casa) VALUES (?, ?, ?, ?)";
                PreparedStatement statementSensor = conexion.prepareStatement(sql);
                statementSensor.setInt(1, sensor.getIdSensor());
                statementSensor.setInt(2, sensor.getPin());
                statementSensor.setString(3, sensor.getTipo());
                statementSensor.setInt(4, sensor.getIdCasa());
                statementSensor.executeUpdate();
            }

            // Insertamos al nuevo usuario en la base de datos
            String sql = "INSERT INTO usuarios (id_usuario, username, passwd, nombre, id_casa) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, generarIdUsuario(conexion));
            statement.setString(2, params.getUsername());
            statement.setString(3, params.getPasswd());
            statement.setString(4, params.getNombre());
            statement.setInt(5, casa.getIdCasa());
            statement.executeUpdate();
            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar un usuario por username. Mensaje: " + e.getMessage());
            rollback(conexion);
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            rollback(conexion);
        }
        catch (Exception e){
            logger.severe("Error: " + e.getMessage());
            rollback(conexion);
        }
        finally {
            try {
                conexion.close();
            }
            catch (SQLException e){
                logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            }
        }
        return correcto;
    }

    /**
     * Genera un codigo de invitacion para una casa controlando que no este duplicado
     * @return codigo Codigo de invitacion generado
     * @throws IOException
     * @throws SQLException
     * @return codigo Codigo de invitacion generado
     *
     */
    private String generarCodigoInvitacion(Connection c) throws IOException, SQLException {
        String codigo = "";
        final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random r = new Random();
        PreparedStatement statement = c.prepareStatement("SELECT * FROM casas");
        ResultSet rs = statement.executeQuery();
        ArrayList<CasaModel> listaCasas = new ArrayList<>();

        while (rs.next()){
            CasaModel casa = new CasaModel();
            casa.setIdCasa(rs.getInt("id_casa"));
            casa.setNombre(rs.getString("nombre"));
            casa.setCodInvitacion(rs.getString("cod_invitacion"));
            listaCasas.add(casa);
        }


        // Generamos el codigo de invitacion
        // Si esta duplicado genera otro codigo
        boolean codigoDuplicado = false;
        do {
            for (int i = 0; i < 10; i++) {
                codigo += caracteres.charAt(r.nextInt(caracteres.length()));
            }
            for (CasaModel casa : listaCasas){
                if (casa.getCodInvitacion().equals(codigo)){
                    codigoDuplicado = true;
                    break;
                }
            }
        } while (codigoDuplicado);

        return codigo;
    }

    /**
     * Saca el id de la casa maximo existente en la Base de datos + 1
     *
     * @return id_casa Devuelve el id de la casa maximo + 1
     */
    private int generarIdCasa(Connection c) throws IOException, SQLException {
        int id_casa = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_casa) FROM casas");
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            id_casa = rs.getInt(1);
        }
        return id_casa + 1;
    }

    /**
     * Saca el id del sensor maximo existente en la Base de datos + 1
     *
     * @return id_casa Devuelve el id de la casa maximo + 1
     */
    private int generarIdSensor(Connection c) throws IOException, SQLException {
        int id_sensor = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_sensor) FROM sensores");
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            id_sensor = rs.getInt(1);
        }

        return id_sensor + 1;
    }

    /**
     * Saca el id del usuario maximo existente en la Base de datos + 1
     *
     * @return id_usuario Devuelve el id del usuario maximo + 1
     */
    private int generarIdUsuario(Connection c) throws IOException, SQLException {
        int id_usuario = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_usuario) FROM usuarios");
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            id_usuario = rs.getInt(1);
        }
        return id_usuario + 1;
    }

    private void rollback(Connection c){
        try{
            c.rollback();
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
        }
    }
}
