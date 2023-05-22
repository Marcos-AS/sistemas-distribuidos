create table Extremo (
    direccionIp VARCHAR(20),
    puerto INTEGER NOT NULL,
    PRIMARY KEY (direccionIp, puerto)   
);

create table Archivo (
    direccionIp VARCHAR(20),
    puerto INTEGER NOT NULL,
    nombre VARCHAR(20),
    PRIMARY KEY (direccionIp, puerto, nombre),
    FOREIGN KEY (direccionIp, puerto) REFERENCES Extremo(direccionIp, puerto)
);



-- [client-server]
-- socket = /run/mysqld/mysqld.sock
-- !includedir /etc/mysql/mariadb.conf.d/
-- !includedir /etc/mysql/conf.d/