-- V1 Crear la tabla de resenas de juegos

CREATE TABLE IF NOT EXISTS resenas_juegos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    juego_id BIGINT NOT NULL,
    calificacion INTEGER NOT NULL,
    comentario VARCHAR(1000),
    fecha_resena TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
