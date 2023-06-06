package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.*;
import com.iesmm.DomoHomeAPI.Utils.Conexion;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
                usuario = generaUsuario(rs);
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
                casa = generaCasa(rs);
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar una casa por username. Mensaje: " + e.getMessage());
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
     * @return correcto True si se ha insertado correctamente, False si no se ha insertado
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
                    casa = generaCasa(rs);
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
            logger.severe("Error en la E/S al registrar un nuevo usuario. Mensaje: " + e.getMessage());
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
     * Saca una lista de todos los dispositivos inteligentes de la base de datos
     * @return lista Lista de dispositivos inteligentes
     */
    public List<DispositivosModel> listaDispositivosUsuario(int idUsuario) {
        Connection conexion = null;
        List<DispositivosModel> lista = new ArrayList<>();
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de los dispositivos inteligentes
            String sql = "SELECT * FROM dispositivos_inteligentes WHERE id_usuario = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, idUsuario);

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                lista.add(generaDispositivo(rs));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al listar los dispositivos. Mensaje: " + e.getMessage());
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
        return lista;
    }

    public DispositivosModel getDispositivoId(int id_dispositivo) {
        Connection conexion = null;
        DispositivosModel dispositivo = null;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de los dispositivos inteligentes
            String sql = "SELECT * FROM dispositivos_inteligentes WHERE id_dispositivo = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, id_dispositivo);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                dispositivo = generaDispositivo(rs);
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar el dispositivo por su id. Mensaje: " + e.getMessage());
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
        return dispositivo;
    }

    /**
     * Inserta un dispositivo en la base de datos
     *
     * @return correcto True si se ha insertado correctamente, False si no se ha insertado
     */
    public Boolean registrarDispositivo(DispositivosModel dispositivo) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            String sql = "INSERT INTO dispositivos_inteligentes (id_dispositivo, nombre, ip, tipo, marca, usuario_servicio, passwd_servicio, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, generarIdDispositivo(conexion));
            statement.setString(2, dispositivo.getNombre());
            statement.setString(3, dispositivo.getIp());
            statement.setString(4, dispositivo.getTipo().toString().toLowerCase());
            statement.setString(5, dispositivo.getMarca().toString().toLowerCase());
            statement.setString(6, dispositivo.getUsuarioServicio());
            statement.setString(7, dispositivo.getPasswdServicio());
            statement.setInt(8, dispositivo.getIdUsuario());

            statement.executeUpdate();

            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al insertar un nuevo dispositivo inteligente. Mensaje: " + e.getMessage());
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
     * Saca una lista de todas las rutinas que tiene los dispositivos de un usuario de la base de datos
     * @return lista Lista de rutinas
     */
    public List<RutinaModel> listaRutinasUsuario(int idUsuario) {
        Connection conexion = null;
        List<RutinaModel> lista = new ArrayList<>();
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de los dispositivos inteligentes
            String sql = "SELECT r.* FROM rutinas r JOIN dispositivos_inteligentes di ON r.id_dispositivo = di.id_dispositivo WHERE di.id_usuario = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, idUsuario);

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                lista.add(generaRutina(rs));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al listar los dispositivos. Mensaje: " + e.getMessage());
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
        return lista;
    }

    /**
     * Saca una lista de todas las rutinas de la base de datos
     * @return lista Lista de rutinas
     */
    public List<RutinaModel> listaRutinas() {
        Connection conexion = null;
        List<RutinaModel> lista = new ArrayList<>();
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de las rutinas
            String sql = "SELECT * FROM rutinas";
            PreparedStatement statement = conexion.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                lista.add(generaRutina(rs));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al listar las rutinas. Mensaje: " + e.getMessage());
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
        return lista;
    }

    /**
     * Inserta una rutina en la base de datos
     *
     * @return correcto True si se ha insertado correctamente, False si no se ha insertado
     */
    public Boolean registraRutina(RutinaModel rutina) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            // Creamos un simpedateformat para posteriormente parsear la fecha y hora a un timestamp
            SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String sql = "INSERT INTO rutinas (id_rutina, fecha_hora, tipo, id_dispositivo) VALUES(?, ?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);

            statement.setInt(1, generarIdRutina(conexion));
            statement.setTimestamp(2, new Timestamp(sdt.parse(rutina.getFecha_hora()).getTime()));
            statement.setString(3, rutina.getTipo().toString().toLowerCase());
            statement.setInt(4, rutina.getIdDispositivo());

            statement.executeUpdate();

            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al insertar un nuevo dispositivo inteligente. Mensaje: " + e.getMessage());
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

    public List<UsuarioModel> listarUsuarios() {
        Connection conexion = null;
        List<UsuarioModel> usuarios = new ArrayList<>();
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de los usuarios
            String sql = "SELECT * FROM usuarios";
            PreparedStatement statement = conexion.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                usuarios.add(generaUsuario(rs));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al listar los usuarios. Mensaje: " + e.getMessage());
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
        return usuarios;
    }

    /**
     * Elimina un usuario de la Base de Datos
     *
     * @return correcto True si se ha eliminado correctamente, False si no se ha eliminado
     */
    public Boolean eliminaUsuario(UsuarioModel usuario) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            // Antes de todo, tenemos que borrar las rutinas
            // Obtenemos las rutinas
            List<Integer> idsRutinas = new ArrayList<>();
            String sqlSelectRutinas = "SELECT r.id_rutina FROM rutinas r JOIN dispositivos_inteligentes di ON r.id_dispositivo = di.id_dispositivo WHERE di.id_usuario = ?";
            PreparedStatement statement = conexion.prepareStatement(sqlSelectRutinas);
            statement.setInt(1, usuario.getId());

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                idsRutinas.add(rs.getInt(1));
            }

            // Borramos las rutinas del usuario
            if (idsRutinas.size() > 0) {
                String signo = "";
                for (int n = 0; n < idsRutinas.size(); n++) {
                    signo += "?,";
                }
                if (!signo.isEmpty()) {
                    signo = signo.substring(0, signo.length() - 1);
                    String sqlDeleteRutinas = "DELETE FROM rutinas WHERE id_rutina IN (" + signo + ")";
                    statement = conexion.prepareStatement(sqlDeleteRutinas);

                    for (int n = 0; n < idsRutinas.size(); n++) {
                        statement.setInt(n + 1, idsRutinas.get(n));
                    }

                    statement.executeUpdate();
                }
            }

            // Hacemos lo mismo para los dispositivos
            // Obtenemos los dispositivos
            List<Integer> idsDispotitivos = new ArrayList<>();

            // Creamos una consulta que saque todas las id de los dispositivos del usuario
            String sqlSelectDispositivos = "SELECT id_dispositivo FROM dispositivos_inteligentes WHERE id_usuario = ?";
            statement = conexion.prepareStatement(sqlSelectDispositivos);
            statement.setInt(1, usuario.getId());

            rs = statement.executeQuery();

            while (rs.next()){
                idsDispotitivos.add(rs.getInt(1));
            }

            if (idsDispotitivos.size() > 0) {
                // Borramos los dispositivos del usuario
                String signo = "";
                for (int n = 0; n < idsDispotitivos.size(); n++) {
                    signo += "?,";
                }
                if (!signo.isEmpty()) {
                    signo = signo.substring(0, signo.length() - 1);
                    System.out.println("|" + signo + "|");
                    String sqlDeleteDispositivos = "DELETE FROM dispositivos_inteligentes WHERE id_dispositivo IN (" + signo + ")";
                    statement = conexion.prepareStatement(sqlDeleteDispositivos);

                    for (int n = 0; n < idsDispotitivos.size(); n++) {
                        statement.setInt(n + 1, idsDispotitivos.get(n));
                    }

                    statement.executeUpdate();
                }
            }

            // Tras eso, ya podemos borrar el usuario
            String sqlDeleteUsuarios = "DELETE FROM usuarios WHERE id_usuario = ?";
            statement = conexion.prepareStatement(sqlDeleteUsuarios);
            statement.setInt(1, usuario.getId());

            statement.executeUpdate();

            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al eliminar un usuario. Mensaje: " + e.getMessage());
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
     *
     * @param usuario
     * @return
     */
    public Boolean actualizarUsuario(UsuarioModel usuario) {
        Connection conexion = null;
        Boolean correcto = false;

        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            // Generamos la consulta de los usuarios
            String sql = "UPDATE usuarios SET username = ?, passwd = ?, nombre = ?, rol = ? WHERE id_usuario = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);

            statement.setString(1, usuario.getUsername());
            statement.setString(2, usuario.getPassword());
            statement.setString(3, usuario.getNombre());
            statement.setString(4, usuario.getRol().toString().toLowerCase());
            statement.setInt(5, usuario.getId());

            statement.executeUpdate();

            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al actualizar el usuario. Mensaje: " + e.getMessage());
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
        return correcto;
    }

    public Boolean eliminarRutina(int idRutina) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            String sql = "DELETE FROM rutinas WHERE id_rutina = ?";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, idRutina);

            statement.executeUpdate();

            conexion.commit();
            correcto = true;

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al eliminar una rutina. Mensaje: " + e.getMessage());
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
     * Elimina un dispositivo de la Base de Datos
     *
     * @return correcto True si se ha eliminado correctamente, False si no se ha eliminado
     */
    public Boolean eliminarDispositivo(int idDispositivo) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            // Antes de todo, tenemos que borrar las rutinas
            // Obtenemos las rutinas
            List<Integer> idsRutinas = new ArrayList<>();
            String sqlSelectRutinas = "DELETE FROM rutinas WHERE id_dispositivo = ?";
            PreparedStatement statement = conexion.prepareStatement(sqlSelectRutinas);
            statement.setInt(1, idDispositivo);

            statement.executeUpdate();

            // Tras eso, ya podemos borrar el dispositivo
            String sqlDeleteUsuarios = "DELETE FROM dispositivos_inteligentes WHERE id_dispositivo = ?";
            statement = conexion.prepareStatement(sqlDeleteUsuarios);
            statement.setInt(1, idDispositivo);

            statement.executeUpdate();

            conexion.commit();
            correcto = true;
        }
        catch(IOException e) {
            logger.severe("Error en la E/S al eliminar un dispositivo. Mensaje: " + e.getMessage());
            rollback(conexion);
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            rollback(conexion);
        }
        catch (Exception e){
            e.printStackTrace();
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

    @Override
    public List<TempHumedadModel> listarMedidasUsuario(int idUsuario) {
        Connection conexion = null;
        List<TempHumedadModel> lista = new ArrayList<>();
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de las medidas de la casa del usuario
            String sql = "SELECT mth.* FROM medidas_temperatura_humedad mth JOIN sensores s ON mth.id_sensor = s.id_sensor JOIN usuarios u ON s.id_casa = u.id_casa";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, idUsuario);

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                lista.add(generaTempHumedad(rs));
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al listar las medidas. Mensaje: " + e.getMessage());
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
        return lista;
    }

    @Override
    public Boolean insertarMedida(TempHumedadModel thModel) {
        Connection conexion = null;
        Boolean correcto = false;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();
            conexion.setAutoCommit(false);

            // Insertamos la medida
            String sql = "INSERT INTO medidas_temperatura_humedad(id_medida, temperatura, humedad, fecha_hora, id_sensor) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);

            statement.executeUpdate();

            conexion.commit();
            correcto = true;
        }
        catch(IOException e) {
            logger.severe("Error en la E/S al insertar una medida. Mensaje: " + e.getMessage());
            rollback(conexion);
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
            rollback(conexion);
        }
        catch (Exception e){
            e.printStackTrace();
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

    @Override
    public SensorModel sensorUsuario(int idUsuario) {
        Connection conexion = null;
        SensorModel sensor = null;
        try {
            // Generamos la conexion
            conexion = Conexion.getConnection();

            // Generamos la consulta de los dispositivos inteligentes
            String sql = "SELECT * FROM sensores s JOIN usuarios u ON s.id_casa = u.id_casa";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setInt(1, idUsuario);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                sensor = generaSensor(rs);
            }

        }
        catch(IOException e) {
            logger.severe("Error en la E/S al filtrar el sensor de un usuario. Mensaje: " + e.getMessage());
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
        return sensor;
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

    /**
     * Saca el id del dispositivo inteligente maximo existente en la Base de datos + 1
     *
     * @return id_dispositivo Devuelve el id del dispositivo inteligente maximo + 1
     */
    private int generarIdDispositivo(Connection c) throws IOException, SQLException {
        int id_dispositivo = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_dispositivo) FROM dispositivos_inteligentes");
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            id_dispositivo = rs.getInt(1);
        }
        return id_dispositivo + 1;
    }

    /**
     * Saca el id de la rutina maximo existente en la Base de datos + 1
     *
     * @return id_rutina Devuelve el id de la rutina maximo + 1
     */
    private int generarIdRutina(Connection c) throws IOException, SQLException {
        int id_rutina = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_rutina) FROM rutinas");
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            id_rutina = rs.getInt(1);
        }
        return id_rutina + 1;
    }

    /**
     * Saca el id de la medida maximo existente en la Base de datos + 1
     *
     * @return id_medida Devuelve el id de la medida maximo + 1
     */
    private int generarIdMedida(Connection c) throws IOException, SQLException {
        int id_medida = 0;
        PreparedStatement statement = c.prepareStatement("SELECT MAX(id_medida) FROM medidas_temperatura_humedad");
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            id_medida = rs.getInt(1);
        }
        return id_medida + 1;
    }

    private UsuarioModel generaUsuario(ResultSet rs) throws SQLException {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("passwd"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setRol(rs.getString("rol"));
        usuario.setId_casa(rs.getInt("id_casa"));
        return usuario;
    }

    private CasaModel generaCasa(ResultSet rs) throws SQLException {
        CasaModel casa = new CasaModel();
        casa.setIdCasa(rs.getInt("id_casa"));
        casa.setNombre(rs.getString("nombre"));
        casa.setCodInvitacion(rs.getString("cod_invitacion"));
        return casa;
    }

    private RutinaModel generaRutina(ResultSet rs) throws SQLException {
        RutinaModel rutina = new RutinaModel();
        rutina.setIdRutina(rs.getInt("id_rutina"));
        rutina.setFecha_hora(rs.getTimestamp("fecha_hora").toString());
        rutina.setTipo(rs.getString("tipo"));
        rutina.setIdDispositivo(rs.getInt("id_dispositivo"));
        return rutina;
    }

    private DispositivosModel generaDispositivo(ResultSet rs) throws SQLException {
        DispositivosModel dispositivo = new DispositivosModel();
        dispositivo.setIdDispositivo(rs.getInt("id_dispositivo"));
        dispositivo.setNombre(rs.getString("nombre"));
        dispositivo.setIp(rs.getString("ip"));
        dispositivo.setTipo(rs.getString("tipo"));
        dispositivo.setMarca(rs.getString("marca"));
        String usuarioServicio = rs.getString("usuario_servicio");
        String passwdServicio = rs.getString("passwd_servicio");
        if (usuarioServicio != null && passwdServicio != null){
            dispositivo.setUsuarioServicio(usuarioServicio);
            dispositivo.setPasswdServicio(passwdServicio);
        }
        dispositivo.setIdUsuario(rs.getInt("id_usuario"));
        return dispositivo;
    }

    private TempHumedadModel generaTempHumedad(ResultSet rs) throws SQLException {
        TempHumedadModel thModel = new TempHumedadModel();
        thModel.setIdMedida(rs.getInt("id_medida"));
        thModel.setTemp(rs.getDouble("temperatura"));
        thModel.setHumedad(rs.getDouble("humedad"));
        thModel.setFecha_hora(rs.getTimestamp("fecha_hora").toString());
        thModel.setIdSensor(rs.getInt("id_sensor"));
        return thModel;
    }

    private SensorModel generaSensor(ResultSet rs) throws SQLException {
        SensorModel sensor = new SensorModel();
        sensor.setIdSensor(rs.getInt("id_sensor"));
        sensor.setPin(rs.getInt("pin"));
        sensor.setTipo(rs.getString("tipo"));
        sensor.setIdCasa(rs.getInt("id_casa"));
        return sensor;
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
