-- ============================================================
-- BaseDatos.sql
-- Script de base de datos, entidades y esquema para la Prueba
-- Tecnica de Arquitectura de Microservicio.
--
-- El sistema esta compuesto por 2 bases de datos independientes
-- (una por microservicio, patron database-per-service):
--   1) personas_db  -> usada por personas-service (Persona, Cliente)
--   2) cuentas_db   -> usada por cuentas-service   (Cuenta, Movimiento, cliente_ref)
--
-- En ejecucion real, cada esquema se crea y versiona automaticamente
-- via Flyway al levantar cada microservicio (ver
-- personas-service/src/main/resources/db/migration y
-- cuentas-service/src/main/resources/db/migration). Este archivo se
-- entrega ademas como script consolidado, tal como lo exige el
-- enunciado.
-- ============================================================

-- ============================================================
-- 1) BASE DE DATOS: personas_db
-- ============================================================
-- CREATE DATABASE personas_db;
-- \c personas_db

CREATE TABLE IF NOT EXISTS persona (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120) NOT NULL,
    genero          VARCHAR(20)  NOT NULL,
    edad            INTEGER      NOT NULL CHECK (edad >= 0),
    identificacion  VARCHAR(20)  NOT NULL UNIQUE,
    direccion       VARCHAR(200),
    telefono        VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS cliente (
    cliente_id  BIGINT PRIMARY KEY REFERENCES persona (id) ON DELETE CASCADE,
    contrasena  VARCHAR(200) NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_persona_identificacion ON persona (identificacion);
CREATE INDEX IF NOT EXISTS idx_cliente_estado ON cliente (estado);

-- Datos de ejemplo (Casos de Uso del enunciado). La contrasena se
-- guarda con hash BCrypt; los valores de abajo son solo ilustrativos
-- de la forma, no representan un hash real de "1234"/"5678"/"1245".
-- INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono) VALUES
--   ('Jose Lema', 'M', 35, '0102030401', 'Otavalo sn y principal', '098254785'),
--   ('Marianela Montalvo', 'F', 32, '0102030402', 'Amazonas y NNUU', '097548965'),
--   ('Juan Osorio', 'M', 40, '0102030403', '13 junio y Equinoccial', '098874587');

-- ============================================================
-- 2) BASE DE DATOS: cuentas_db
-- ============================================================
-- CREATE DATABASE cuentas_db;
-- \c cuentas_db

CREATE TABLE IF NOT EXISTS cliente_ref (
    cliente_id  BIGINT PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    estado      BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS cuenta (
    id                BIGSERIAL PRIMARY KEY,
    numero_cuenta     VARCHAR(20)    NOT NULL UNIQUE,
    tipo_cuenta       VARCHAR(20)    NOT NULL,
    saldo_inicial     NUMERIC(15,2)  NOT NULL,
    saldo_disponible  NUMERIC(15,2)  NOT NULL,
    estado            BOOLEAN        NOT NULL DEFAULT TRUE,
    cliente_id        BIGINT         NOT NULL
);

CREATE TABLE IF NOT EXISTS movimiento (
    id               BIGSERIAL PRIMARY KEY,
    fecha            TIMESTAMP      NOT NULL,
    tipo_movimiento  VARCHAR(20)    NOT NULL,
    valor            NUMERIC(15,2)  NOT NULL,
    saldo            NUMERIC(15,2)  NOT NULL,
    cuenta_id        BIGINT         NOT NULL REFERENCES cuenta (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cuenta_cliente_id ON cuenta (cliente_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_cuenta_id_fecha ON movimiento (cuenta_id, fecha);

-- Nota: cliente_ref se puebla automaticamente en tiempo de ejecucion
-- mediante los eventos asincronos (RabbitMQ) publicados por
-- personas-service; no requiere carga manual.
