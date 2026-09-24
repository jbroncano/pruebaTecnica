CREATE TABLE persona (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120) NOT NULL,
    genero          VARCHAR(20)  NOT NULL,
    edad            INTEGER      NOT NULL CHECK (edad >= 0),
    identificacion  VARCHAR(20)  NOT NULL UNIQUE,
    direccion       VARCHAR(200),
    telefono        VARCHAR(20),
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE cliente (
    cliente_id  BIGINT PRIMARY KEY REFERENCES persona (id) ON DELETE CASCADE,
    contrasena  VARCHAR(200) NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_persona_identificacion ON persona (identificacion);
CREATE INDEX idx_cliente_estado ON cliente (estado);
