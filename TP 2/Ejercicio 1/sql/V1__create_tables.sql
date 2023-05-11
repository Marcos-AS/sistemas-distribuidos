create table Extremo (
    direccionIp VARCHAR(20),
    puerto INTEGER NOT NULL,
    PRIMARY KEY (direccionIp, puerto)
);

create table Archivo (
    nombre VARCHAR(20),
    PRIMARY KEY (nombre)
);