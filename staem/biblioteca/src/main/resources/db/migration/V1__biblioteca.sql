-- V1 Crear la tabla de posesiones de la biblioteca

CREATE TABLE IF NOT EXISTS posee_biblioteca (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_juego BIGINT NOT NULL,
    fecha_adquisicion TIMESTAMP NOT NULL,
    horas_jugadas INTEGER NOT NULL DEFAULT 0
);
