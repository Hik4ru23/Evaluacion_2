-- Crear la tabla logros disponibles
CREATE TABLE IF NOT EXISTS logros_disponibles (
    id BIGSERIAL PRIMARY KEY,
    id_juego BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(1000),
    puntos_xp INTEGER NOT NULL
);

-- Crear la tabla progreso logros
CREATE TABLE IF NOT EXISTS progreso_logros (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_logro BIGINT NOT NULL,
    desbloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_progreso_logros_disponibles FOREIGN KEY (id_logro) REFERENCES logros_disponibles(id) ON DELETE CASCADE
);