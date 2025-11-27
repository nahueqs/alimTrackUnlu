CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rol VARCHAR(20) NOT NULL DEFAULT 'OPERADOR',
    esta_activo BOOLEAN DEFAULT TRUE,
    password VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS receta (
    id_receta BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_receta VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    creado_por BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS version_receta (
    id_version BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    codigo_version_receta VARCHAR(255) NOT NULL UNIQUE,
    id_receta_padre BIGINT NOT NULL,
    creado_por BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_receta_padre) REFERENCES receta(id_receta),
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS produccion (
    id_produccion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_version BIGINT NOT NULL,
    creado_por BIGINT NOT NULL,
    codigo_produccion VARCHAR(100) UNIQUE,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    lote VARCHAR(100),
    encargado VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'EN_PROCESO',
    observaciones VARCHAR(255),
    fecha_modificacion TIMESTAMP,
    FOREIGN KEY (id_version) REFERENCES version_receta(id_version),
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS seccion (
    id_seccion BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    id_version BIGINT NOT NULL,
    FOREIGN KEY (id_version) REFERENCES version_receta(id_version)
);

CREATE TABLE IF NOT EXISTS campo_simple (
    id_campo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo_dato VARCHAR(50) NOT NULL,
    id_seccion BIGINT NOT NULL,
    FOREIGN KEY (id_seccion) REFERENCES seccion(id_seccion)
);

CREATE TABLE IF NOT EXISTS respuesta_campo (
    id_respuesta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_produccion BIGINT NOT NULL,
    id_campo BIGINT NOT NULL,
    valor VARCHAR(255),
    timestamp TIMESTAMP,
    creado_por BIGINT,
    FOREIGN KEY (id_produccion) REFERENCES produccion(id_produccion),
    FOREIGN KEY (id_campo) REFERENCES campo_simple(id_campo),
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS tabla (
    id_tabla BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    id_seccion BIGINT NOT NULL,
    FOREIGN KEY (id_seccion) REFERENCES seccion(id_seccion)
);

CREATE TABLE IF NOT EXISTS columna_tabla (
    id_columna BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo_dato VARCHAR(50) NOT NULL,
    id_tabla BIGINT NOT NULL,
    FOREIGN KEY (id_tabla) REFERENCES tabla(id_tabla)
);

CREATE TABLE IF NOT EXISTS fila_tabla (
    id_fila BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    id_tabla BIGINT NOT NULL,
    FOREIGN KEY (id_tabla) REFERENCES tabla(id_tabla)
);

CREATE TABLE IF NOT EXISTS respuesta_tabla (
    id_respuesta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_produccion BIGINT NOT NULL,
    id_tabla BIGINT NOT NULL,
    id_fila BIGINT NOT NULL,
    id_columna BIGINT NOT NULL,
    valor VARCHAR(255),
    timestamp TIMESTAMP,
    creado_por BIGINT,
    FOREIGN KEY (id_produccion) REFERENCES produccion(id_produccion),
    FOREIGN KEY (id_tabla) REFERENCES tabla(id_tabla),
    FOREIGN KEY (id_fila) REFERENCES fila_tabla(id_fila),
    FOREIGN KEY (id_columna) REFERENCES columna_tabla(id_columna),
    FOREIGN KEY (creado_por) REFERENCES usuario(id_usuario)
);
