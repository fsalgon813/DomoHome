package com.iesmm.DomoHomeAPI.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class Conexion {
    private static Connection jdbcConnection = null;
    //private final String fileProperties = "res/config.properties";
    //private String uri, port, bd, user, pass;
    private final String uri= "jdbc:mysql://localhost";
    private final String port= "3306";
    private final String bd= "domohome";
    private final String user = "fsalgon";
    private final String pass = "1234";
    Logger logger = Logger.getLogger("conexion");

    private Conexion() throws IOException, SQLException {
        // Cargamos la configuraciond el fichero de propiedades en la ruta por defecto
        /*Properties props = new Properties();
        props.load(new FileReader(fileProperties));

        // Cargamos los valores de propiedades del fichero
        this.uri = props.getProperty("uri");
        this.port = props.getProperty("port");
        this.bd = props.getProperty("bd");
        this.user = props.getProperty("user");
        this.pass = props.getProperty("pass");*/

        // Realizamos la conexion a la BD
        jdbcConnection = java.sql.DriverManager.getConnection(uri + ":" + port + "/" + bd, user, pass);
    }

    /**
     * Conecta y/o devuelve una conexión a la base de datos previamente establecida.
     *
     * @return la conexión de la base de datos.
     * @throws SQLException devuelve una excepción si no se puede obtener la conexión.
     */
    public static Connection getConnection() throws SQLException, IOException {
        if (jdbcConnection == null || jdbcConnection.isClosed())
            new Conexion();

        return jdbcConnection;
    }

    /**
     * Cierra la conexión a la base de datos.
     *
     * @return devuelve cierto si la conexión ha sido cerrada, falso en caso
     * contrario
     * @throws SQLException devuelve una excepción si no puede cerrar la conexión.
     */
    public static boolean close() throws SQLException {
        boolean closed = false;

        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
            closed = true;
        }

        return closed;
    }
}
