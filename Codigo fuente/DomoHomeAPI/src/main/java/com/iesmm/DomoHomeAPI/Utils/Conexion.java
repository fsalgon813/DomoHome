package com.iesmm.DomoHomeAPI.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class Conexion {
    private static Connection jdbcConnection = null;
    //private final String fileProperties = "res/config.properties";
    private String uri, port, bd, user, pass;
    Logger logger = Logger.getLogger("conexion");

    private final String fileProperties = "conexion.properties";

    private Conexion() throws IOException, SQLException {
        File f = new File(fileProperties);

            // Si el archivo de propiedades no existe, lo creamos
            if (!f.exists()) {
                FileWriter fw = new FileWriter(f);
                fw.write("uri=jdbc:mysql://localhost\n");
                fw.write("port=3306\n");
                fw.write("bd=domohome\n");
                fw.write("user=fsalgon\n");
                fw.write("pass=1234");
                fw.close();
            }

        // Si el archivo de propiedades existe, lo leemos y creamos la conexion con la BD
        if (f.exists()){
           // Cargamos la configuraciond el fichero de propiedades en la ruta por defecto(la actual)
           Properties props = new Properties();
           props.load(new FileReader(f));

           // Cargamos los valores de propiedades del fichero
           this.uri = props.getProperty("uri");
           this.port = props.getProperty("port");
           this.bd = props.getProperty("bd");
           this.user = props.getProperty("user");
           this.pass = props.getProperty("pass");

           // Realizamos la conexion a la BD
           jdbcConnection = java.sql.DriverManager.getConnection(uri + ":" + port + "/" + bd, user, pass);
       }
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
