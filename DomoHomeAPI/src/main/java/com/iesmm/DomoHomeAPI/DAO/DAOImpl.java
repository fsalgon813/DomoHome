package com.iesmm.DomoHomeAPI.DAO;

import com.iesmm.DomoHomeAPI.Model.UsuarioModel;
import com.iesmm.DomoHomeAPI.Utils.Conexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            logger.info(username);

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


    private void rollback(Connection c){
        try{
            c.rollback();
        }
        catch (SQLException e){
            logger.severe("SQLState: " + e.getSQLState() + " | Error Code: " + e.getErrorCode() + " | Message: " + e.getMessage());
        }
    }
}
