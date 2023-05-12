CREATE USER 'repluser'@'%' IDENTIFIED BY 'replsecret';
GRANT REPLICATION SLAVE ON *.* TO 'repluser'@'%';

create table Extremo (
    direccionIp VARCHAR(20),
    puerto INTEGER NOT NULL,
    PRIMARY KEY (direccionIp, puerto)
);

create table Archivo (
    nombre VARCHAR(20),
    PRIMARY KEY (nombre)
);

-- [client-server]
-- socket = /run/mysqld/mysqld.sock
-- !includedir /etc/mysql/mariadb.conf.d/
-- !includedir /etc/mysql/conf.d/