-- V1 Crear la tabla de elementos del carrito de compras

CREATE TABLE IF NOT EXISTS items_carrito (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    juego_id BIGINT NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
