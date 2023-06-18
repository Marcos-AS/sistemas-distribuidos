create table Tarea (
    id INTEGER NOT NULL,
    estado VARCHAR(20),
    tiempo_inicio TIME,
    tiempo_fin TIME,
    cantidad_partes INTEGER,
    PRIMARY KEY (id)   
);

-- [client-server]
-- socket = /run/mysqld/mysqld.sock
-- !includedir /etc/mysql/mariadb.conf.d/
-- !includedir /etc/mysql/conf.d/