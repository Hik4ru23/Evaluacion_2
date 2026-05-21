-- V1 Crear las tablas de pagos y detalle de pagos

CREATE TABLE IF NOT EXISTS pagos (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    total_pagado DOUBLE PRECISION NOT NULL,
    fecha_transaccion TIMESTAMP NOT NULL,
    estado VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_pagos (
    id BIGSERIAL PRIMARY KEY,
    id_pago BIGINT NOT NULL,
    id_juego BIGINT NOT NULL,
    subtotal DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_detalle_pagos_pagos FOREIGN KEY (id_pago) REFERENCES pagos(id) ON DELETE CASCADE
);
