-- V1 Crear la tabla de juegos del catalogo

CREATE TABLE IF NOT EXISTS juegos_catalogo (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    precio DOUBLE PRECISION NOT NULL,
    genero VARCHAR(50) NOT NULL,
    desarrollador VARCHAR(100) NOT NULL,
    imagen_url VARCHAR(255),
    stock INTEGER NOT NULL,
    disponible BOOLEAN NOT NULL
);
