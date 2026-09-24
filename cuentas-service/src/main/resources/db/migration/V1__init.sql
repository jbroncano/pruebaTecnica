CREATE TABLE cliente_ref (
    cliente_id  BIGINT PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    estado      BOOLEAN      NOT NULL
);

CREATE TABLE cuenta (
    id                BIGSERIAL PRIMARY KEY,
    numero_cuenta     VARCHAR(20)    NOT NULL UNIQUE,
    tipo_cuenta       VARCHAR(20)    NOT NULL,
    saldo_inicial     NUMERIC(15,2)  NOT NULL,
    saldo_disponible  NUMERIC(15,2)  NOT NULL,
    estado            BOOLEAN        NOT NULL DEFAULT TRUE,
    cliente_id        BIGINT         NOT NULL
);

CREATE TABLE movimiento (
    id               BIGSERIAL PRIMARY KEY,
    fecha            TIMESTAMP      NOT NULL,
    tipo_movimiento  VARCHAR(20)    NOT NULL,
    valor            NUMERIC(15,2)  NOT NULL,
    saldo            NUMERIC(15,2)  NOT NULL,
    cuenta_id        BIGINT         NOT NULL REFERENCES cuenta (id) ON DELETE CASCADE
);

CREATE INDEX idx_cuenta_cliente_id ON cuenta (cliente_id);
CREATE INDEX idx_movimiento_cuenta_id_fecha ON movimiento (cuenta_id, fecha);
