-- V1 Crear la tabla de ofertas activas

CREATE TABLE IF NOT EXISTS ofertas_activas (
    id BIGSERIAL PRIMARY KEY,
    juego_id BIGINT NOT NULL,
    porcentaje_descuento DOUBLE PRECISION NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL
);
