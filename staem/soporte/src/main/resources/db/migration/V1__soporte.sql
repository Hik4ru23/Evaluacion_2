-- V1 Crear la tabla de alertas y notificaciones

CREATE TABLE IF NOT EXISTS alertas_notificaciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    asunto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
