-- V1 Crear la tabla de amistades de la comunidad

CREATE TABLE IF NOT EXISTS amistades_comunidad (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    amigo_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
