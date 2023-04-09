CREATE TABLE casas(
    id_casa INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR NOT NULL,
);
CREATE TABLE usuarios(
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    username NOT NULL UNIQUE,
    passwd NOT NULL,
    nombre VARCHAR NOT NULL,
    id_casa INT NOT NULL,
    CONSTRAINT FK_usuarios_casa FOREIGN KEY (id_casa) REFERENCES casas(id_casa)
);
CREATE TABLE sensores(
    id_sensor INT PRIMARY KEY AUTO_INCREMENT,
    pin INT,
    tipo VARCHAR,
    id_usuario NOT NULL,
    CONSTRAINT FK_sensores_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT CHK_sensores_tipo CHECK (tipo LIKE 'temperatura_humedad')
);
CREATE TABLE medidas_temperatura_humedad(
    id_medida INT PRIMARY KEY AUTO_INCREMENT,
    temperatura DOUBLE,
    humedad DOUBLE,
    fecha_hora TIMESTAMP,
    id_sensor INT NOT NULL,
    CONSTRAINT FK_medidas_sensor FOREIGN KEY (id_sensor) REFERENCES sensores(id_sensor)
);
CREATE TABLE casa_realiza_medida(
    id_medida INT NOT NULL,
    id_casa INT NOT NULL,
    CONSTRAINT FK_medida_realiza FOREIGN KEY (id_medida) REFERENCES medidas_temperatura_humedad(id_medida),
    CONSTRAINT FK_casa_realiza FOREIGN KEY (id_casa) REFERENCES casas(id_casa)
);
CREATE TABLE dispositivos_inteligentes(
    id_dispositivo INT PRIMARY KEY AUTO_INCREMENT,
    ip VARCHAR NOT NULL,
    puerto INT NOT NULL,
    tipo VARCHAR NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT FK_dispositivos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
CREATE TABLE rutinas(
    id_rutina INT PRIMARY KEY AUTO_INCREMENT,
    fecha_hora TIMESTAMP NOT NULL,
    id_dispositivo INT NOT NULL,
    CONSTRAINT FK_rutinas_dispositivo FOREIGN KEY (id_dispositivo) REFERENCES dispositivos_inteligentes(id_dispositivo)
);