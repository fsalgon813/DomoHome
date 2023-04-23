DROP TABLE IF EXISTS rutinas;
DROP TABLE IF EXISTS dispositivos_inteligentes;
DROP TABLE IF EXISTS casa_realiza_medida;
DROP TABLE IF EXISTS medidas_temperatura_humedad;
DROP TABLE IF EXISTS sensores;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS casas;

CREATE TABLE casas(
    id_casa INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL
);
CREATE TABLE usuarios(
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    passwd VARCHAR(80) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    id_casa INT NOT NULL,
    CONSTRAINT FK_usuarios_casa FOREIGN KEY (id_casa) REFERENCES casas(id_casa)
);
CREATE TABLE sensores(
    id_sensor INT PRIMARY KEY AUTO_INCREMENT,
    pin INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    id_casa INT NOT NULL,
    CONSTRAINT FK_sensores_casa FOREIGN KEY (id_casa) REFERENCES casa(id_casa),
    CONSTRAINT CHK_sensores_tipo CHECK (tipo LIKE 'temperatura_humedad')
);
CREATE TABLE medidas_temperatura_humedad(
    id_medida INT PRIMARY KEY AUTO_INCREMENT,
    temperatura DOUBLE NOT NULL,
    humedad DOUBLE NOT NULL,
    fecha_hora TIMESTAMP,
    id_sensor INT NOT NULL,
    CONSTRAINT FK_medidas_sensor FOREIGN KEY (id_sensor) REFERENCES sensores(id_sensor)
);
CREATE TABLE dispositivos_inteligentes(
    id_dispositivo INT PRIMARY KEY AUTO_INCREMENT,
    ip VARCHAR(15) NOT NULL,
    puerto INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT FK_dispositivos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
CREATE TABLE rutinas(
    id_rutina INT PRIMARY KEY AUTO_INCREMENT,
    fecha_hora TIMESTAMP NOT NULL,
    id_dispositivo INT NOT NULL,
    CONSTRAINT FK_rutinas_dispositivo FOREIGN KEY (id_dispositivo) REFERENCES dispositivos_inteligentes(id_dispositivo)
);

/* Añadir trigger si no se ha medido la temperatura o la  humedad, ponga 0 */
