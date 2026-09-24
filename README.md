# Prueba Tecnica - Arquitectura de Microservicios (Perfil Senior)

Sistema bancario compuesto por 2 microservicios independientes, comunicados de
forma asincrona, que implementan la gestion de clientes, cuentas y movimientos
descrita en el enunciado (F1-F7).

## 1. Descripcion del proyecto

- **personas-service**: gestiona `Persona` y `Cliente` (endpoint `/clientes`).
- **cuentas-service**: gestiona `Cuenta` y `Movimiento` (endpoints `/cuentas`,
  `/movimientos`, `/reportes`).
- Ambos servicios estan desacoplados: `cuentas-service` no llama sincronamente
  a `personas-service`. En su lugar, `personas-service` publica eventos de
  dominio (`cliente.creado`, `cliente.actualizado`, `cliente.eliminado`) hacia
  RabbitMQ; `cuentas-service` los consume y mantiene una proyeccion local de
  solo lectura (`cliente_ref`) para validar clientes al crear cuentas o
  generar reportes. Esto evita acoplar la disponibilidad de un servicio a la
  del otro (resiliencia) y permite escalarlos de forma independiente.

## 2. Tecnologias utilizadas

- Java 21 + Spring Boot 3.3 (Web, Data JPA, Validation, AMQP)
- PostgreSQL 16 (una base de datos por microservicio)
- RabbitMQ 3.13 (comunicacion asincrona)
- Flyway (versionamiento de esquema)
- Lombok, MapStruct
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, AssertJ, Testcontainers
- Docker / docker-compose

## 3. Requisitos

- Docker y Docker Compose
- (Opcional, para desarrollo local sin Docker) JDK 21 y Maven 3.9+

## 4. Instalacion y ejecucion

```bash
docker compose up --build
```

Esto levanta: `postgres-personas` (5433), `postgres-cuentas` (5434),
`rabbitmq` (5672 / consola de administracion en 15672, usuario `guest`/`guest`),
`personas-service` (8081) y `cuentas-service` (8082). Cada microservicio
ejecuta sus propias migraciones Flyway al iniciar; no es necesario correr
`BaseDatos.sql` manualmente contra los contenedores (se entrega como
documentacion del esquema final, segun lo solicitado).

Para detener y limpiar:

```bash
docker compose down -v
```

## 5. Configuracion / variables de entorno

| Variable | Servicio | Descripcion | Default |
|---|---|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | ambos | Conexion a Postgres | ver `application.yml` |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD` | ambos | Conexion a RabbitMQ | `localhost` / `5672` / `guest` / `guest` |

## 6. Ejecutar los tests

Cada microservicio se prueba de forma independiente:

```bash
cd personas-service && mvn test
cd cuentas-service && mvn test
```

Incluyen:
- Pruebas unitarias de dominio (`ClienteTest` - F5).
- Pruebas unitarias de servicio (reglas de negocio, con Mockito).
- Pruebas unitarias de los endpoints con `@WebMvcTest` (indicacion general:
  minimo 2 por servicio).
- Pruebas de integracion end-to-end con Testcontainers + Postgres real (F6).

## 7. Endpoints / API

### personas-service (`:8081`)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/clientes` | Crea un cliente (con sus datos de persona) |
| GET | `/clientes/{clienteId}` | Obtiene un cliente por id |
| GET | `/clientes?estado=true` | Lista clientes (paginado, filtro opcional por estado) |
| PUT | `/clientes/{clienteId}` | Actualiza un cliente |
| DELETE | `/clientes/{clienteId}` | Elimina un cliente |

### cuentas-service (`:8082`)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/cuentas` | Crea una cuenta (valida que el cliente exista y este activo) |
| GET | `/cuentas/{id}` | Obtiene una cuenta |
| GET | `/cuentas` | Lista cuentas (paginado) |
| PUT | `/cuentas/{id}` | Actualiza tipo/estado de una cuenta |
| DELETE | `/cuentas/{id}` | Elimina una cuenta |
| POST | `/movimientos` | Registra un movimiento (F2) y valida saldo (F3) |
| GET | `/movimientos/{id}` | Obtiene un movimiento |
| PUT | `/movimientos/{id}` | Actualiza un movimiento (recalcula el saldo de la cuenta) |
| DELETE | `/movimientos/{id}` | Elimina un movimiento (revierte su efecto en el saldo) |
| GET | `/reportes?clienteId=&fechaInicio=&fechaFin=` | Estado de cuenta (F4) |

Documentacion interactiva (Swagger UI): `http://localhost:8081/swagger-ui.html`
y `http://localhost:8082/swagger-ui.html`.

Formato de error estandar en toda la API:

```json
{
  "status": 422,
  "message": "Saldo no disponible",
  "errors": [],
  "timestamp": "2026-09-24T10:00:00Z"
}
```

Colección de Postman: `postman/PruebaTecnica.postman_collection.json` (+
entorno `postman/PruebaTecnica.postman_environment.json`) con los casos de
uso del enunciado, incluyendo la validacion de "Saldo no disponible".

## 8. Decisiones tecnicas relevantes

- **2 microservicios + comunicacion asincrona (RabbitMQ)**:
  Se opto por un modelo de eventos de dominio + proyeccion
  local (`cliente_ref`) en lugar de RPC sincrono sobre la cola, para que
  `cuentas-service` siga funcionando (con datos eventualmente consistentes)
  aunque `personas-service` este caido.
- **Herencia JPA (`JOINED`)** para modelar que `Cliente` es una `Persona`,
  tal como lo pide el enunciado.
- **Bloqueo pesimista (`PESSIMISTIC_WRITE`)** al registrar movimientos, para
  evitar condiciones de carrera cuando dos movimientos concurrentes afectan
  la misma cuenta (correctitud del saldo por encima de throughput bruto).
- **Contrasena con hash BCrypt**: nunca se persiste ni se retorna en texto
  plano.
- **Database-per-service**: cada microservicio es dueno de su propio esquema;
  no hay llaves foraneas cruzando bases de datos (`cuenta.cliente_id` es un
  valor de referencia, no un FK real).
- **Manejo de excepciones centralizado** (`@RestControllerAdvice`) con
  formato de error consistente en ambos servicios.

## 9. Mejoras que se pueden realiar

- Autenticacion/autorizacion (OAuth2/JWT) en los endpoints.
- Outbox pattern para garantizar entrega exactly-once de los eventos hacia
  RabbitMQ (actualmente se publica post-commit, pero un fallo de red tras el
  commit podria perder el evento).
- Circuit breaker (Resilience4j) si en el futuro se agregan llamadas
  sincronas entre servicios.
- Cache (Redis) para el reporte de estado de cuenta si el volumen de
  movimientos crece significativamente.
- Observabilidad: métricas (Micrometer/Prometheus) y trazas distribuidas
  (OpenTelemetry) entre ambos servicios.
