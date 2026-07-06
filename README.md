# Banco DevSu - Documentación de Validaciones y Arquitectura

## 1. Introducción

Este documento describe las validaciones, mecanismos de resiliencia y recomendaciones de arquitectura para el proyecto **BankApplication** (microservicios `account` y `client`).

El objetivo es ofrecer un marco seguro y de alto nivel para un sistema bancario distribuido, enfocado en consistencia, escalabilidad y seguridad.

---

## 2. Arquitectura general

El proyecto está diseñado como una arquitectura de microservicios:

- `account`: microservicio responsable de la gestión de cuentas y transacciones.
- `client`: microservicio responsable de la gestión de clientes.
- `postgres`: base de datos PostgreSQL compartida por ambos servicios.

Todos los servicios están desarrollados con programación reactiva usando Spring WebFlux.
`account` y `client` exponen endpoints reactivos que devuelven `Mono` y `Flux`.

Cada servicio se ejecuta de forma independiente y se despliega con Docker Compose.

---

## 3. Reglas de validación de negocio

### 3.1. Transacciones
- No se debe crear una transacción si la cuenta no existe.
- No se debe crear una transacción si el cliente no existe.
- No se debe crear una transacción si la cuenta está inactiva.
- No se debe crear una transacción si el cliente está inactivo.

### 3.2. Cuentas
- No se debe crear una cuenta si el cliente no existe.
- No se debe crear una cuenta si el cliente está inactivo.

### 3.3. Clientes
- El registro o modificación de clientes debe validar estado: activo/inactivo.
- El campo `password` debe codificarse con `PasswordEncoder` de Spring Security para garantizar almacenamiento cifrado.

---

## 4. Integración entre microservicios

### 4.1. Comunicación `account` ↔ `client`
- El microservicio `account` consume información de clientes desde `client` mediante llamadas HTTP.
- En el contenedor Docker, la URL de `client` debe resolverse usando el nombre del servicio `client` dentro de la red de Compose (`http://client:8082/api/v1/clients`).

### 4.2. Resiliencia en `client`
- Se utiliza resiliencia en el microservicio `client` para manejar fallas en llamadas y garantizar estabilidad.
- Se recomienda aplicar patrones como:
  - Circuit Breaker
  - Timeouts
  - Retries
  - Fallbacks

---

## 5. Consistencia y bloqueo en transacciones

### 5.1. Condición de carrera e idempotencia
- Para evitar condiciones de carrera y asegurar la consistencia al registrar transacciones y actualizar saldos, se aplica bloqueo de registro en la carga de la cuenta a modificar.
- En JPA/R2DBC se utilizó la query "SELECT * FROM accounts WHERE id = :id FOR UPDATE"

Esto garantiza que una cuenta se bloquee durante el procesamiento de un movimiento, evitando pérdidas y sobreescrituras.

---

## 6. Ejecución con Docker Compose

### 6.1. Crear archivo `.env`

Antes de ejecutar Docker Compose, crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
SPRING_R2DBC_USERNAME=tu_username
SPRING_R2DBC_PASSWORD=tu_password
```

### 6.2. Ejecutar Docker Compose

Desde el directorio raíz del proyecto (`BankApplication`), ejecuta:

```bash
docker compose -f compose.yml up
```

Esto levantará los servicios:
- `postgres`
- `account`
- `client`

### 6.3. Puertos expuestos
- `account`: `http://localhost:8081`
- `client`: `http://localhost:8082`

---

## 7. Notas de configuración

- `account` y `client` utilizan la misma base de datos PostgreSQL definida en `compose.yml`.
- Los servicios `account` y `client` reciben sus credenciales de R2DBC desde el archivo `.env`.
- El servicio `account` usa la variable `CLIENT_SERVICE_URL` para comunicarse con `client` dentro de Docker.- Se integra Spring Boot Actuator para métricas y endpoints de salud.
- URL de Actuator en `account`: `http://localhost:8081/actuator`
- URL de Actuator en `client`: `http://localhost:8082/actuator`
---

## 8. Recomendaciones de escalabilidad y seguridad

### 8.1. Balanceo de peticiones y gateway
- Se sugiere implementar un **Spring Cloud Gateway** o un API Gateway similar para:
  - Balanceo de peticiones
  - Enrutamiento dinámico
  - Autenticación/autorización centralizada
  - Rate limiting

### 8.2. Seguridad y autenticación
- Implementar una solución de **OAuth2** y **JWT** para:
  - Autenticar usuarios y servicios
  - Emitir tokens con permisos/roles
  - Validar acceso a recursos según roles
  - Soportar auditoría y controles de acceso granulares

---

## 9. Observabilidad

- Implementar **Micrometer** y **Spring Boot Actuator** para exponer métricas y endpoints de salud.
- Integrar con **Prometheus** para la recolección de métricas y con **Grafana** para visualización.
- Registrar métricas clave:
  - latencia de endpoints
  - tasa de errores (4xx/5xx)
  - uso de CPU/memoria
  - tiempos de respuesta de llamadas entre microservicios
- Usar trazabilidad distribuida (por ejemplo, Zipkin/Jaeger) para correlacionar solicitudes entre servicios.

---

## 10. Conclusión

Este documento establece un conjunto de validaciones y prácticas para un entorno bancario confiable. Su adopción mejora la estabilidad, seguridad y mantenibilidad del sistema de microservicios.

---

## 11. Pruebas de API

- El proyecto puede probarse mediante Postman o cualquier cliente HTTP.
- Por ejemplo, el endpoint para crear cuentas es:
  - `POST http://localhost:8081/api/v1/accounts`

---

## 12. Archivos importantes

- `compose.yml`: definición de servicios Docker Compose.
- `account/src/main/java/...`: código del microservicio `account`.
- `client/src/main/java/...`: código del microservicio `client`.
- `postgres/`: datos persistentes de la base de datos PostgreSQL.

---

## 13. Referencias rápidas

- Arrancar todo: `docker compose -f compose.yml up`
- Variables de entorno: crear `.env` con `SPRING_R2DBC_USERNAME` y `SPRING_R2DBC_PASSWORD`
- URL del servicio de cuentas: `http://localhost:8081`
- URL del servicio de clientes: `http://localhost:8082`
