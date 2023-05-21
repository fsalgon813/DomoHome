DROP TABLE IF EXISTS rutinas;
DROP TABLE IF EXISTS dispositivos_inteligentes;
DROP TABLE IF EXISTS medidas_temperatura_humedad;
DROP TABLE IF EXISTS sensores;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS casas;

CREATE TABLE casas(
    id_casa INT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    cod_invitacion VARCHAR(10) NOT NULL UNIQUE
);
CREATE TABLE usuarios(
    id_usuario INT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    passwd VARCHAR(80) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    id_casa INT NOT NULL,
    rol VARCHAR(7) NOT NULL DEFAULT 'usuario',
    CONSTRAINT FK_usuarios_casa FOREIGN KEY (id_casa) REFERENCES casas(id_casa),
    CONSTRAINT CHK_usuarios_rol CHECK (rol LIKE 'usuario' OR rol LIKE 'admin')
);
CREATE TABLE sensores(
    id_sensor INT PRIMARY KEY,
    pin INT NOT NULL DEFAULT 4,
    tipo VARCHAR(30) NOT NULL,
    id_casa INT NOT NULL,
    CONSTRAINT FK_sensores_casa FOREIGN KEY (id_casa) REFERENCES casas(id_casa),
    CONSTRAINT CHK_sensores_tipo CHECK (tipo LIKE 'temperatura_humedad')
);
CREATE TABLE medidas_temperatura_humedad(
    id_medida INT PRIMARY KEY,
    temperatura DOUBLE NOT NULL,
    humedad DOUBLE NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    id_sensor INT NOT NULL,
    CONSTRAINT FK_medidas_sensor FOREIGN KEY (id_sensor) REFERENCES sensores(id_sensor)
);
CREATE TABLE dispositivos_inteligentes(
    id_dispositivo INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    ip VARCHAR(15) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    usuario_servicio VARCHAR(50),
    passwd_servicio VARCHAR(50),
    id_usuario INT NOT NULL,
    CONSTRAINT FK_dispositivos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT CHK_tipo CHECK (tipo LIKE 'Bombilla' OR tipo LIKE 'tv'),
    CONSTRAINT CHK_marca CHECK (marca LIKE 'tp_link' OR marca LIKE 'samsung')
);
CREATE TABLE rutinas(
    id_rutina INT PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL,
    id_dispositivo INT NOT NULL,
    CONSTRAINT FK_rutinas_dispositivo FOREIGN KEY (id_dispositivo) REFERENCES dispositivos_inteligentes(id_dispositivo)
);
