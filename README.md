# Toño's Motors — Taller Mecánico 🔧

Sistema de gestión para un taller mecánico. Backend **Spring Boot + PostgreSQL** con
seguridad **JWT por roles** (ADMIN / USER) y frontend **React + Vite + Bootstrap**.

- **Backend:** `taller-mecanico-back` (Java 17, Spring Boot, Spring Security, JPA)
- **Frontend:** `taller-mecanico-front` (React 18, Vite, Bootstrap 5, Chart.js)
- **Base de datos:** PostgreSQL 15

---

## Credenciales de prueba (sembradas al iniciar)

| Usuario | Contraseña | Rol           |
|---------|------------|---------------|
| `admin` | `admin123` | ADMIN + USER  |
| `user`  | `user123`  | USER          |

---

## Puertos

| Servicio  | URL                       |
|-----------|---------------------------|
| Frontend  | http://localhost:3000     |
| Backend   | http://localhost:8080     |
| Postgres  | localhost:5432            |

---

## Opción A — Todo con Docker (recomendado)

Requiere Docker Desktop. Levanta base de datos, backend y frontend de una sola vez:

```bash
docker compose up --build
```

Luego abre **http://localhost:3000**. Para detener: `docker compose down`
(agrega `-v` para borrar también los datos de la base).

---

## Opción B — Desarrollo local (paso a paso)

### 1. Base de datos
Levanta solo Postgres con Docker:

```bash
docker compose up -d db-taller
```

### 2. Backend
```bash
cd taller-mecanico-back
./mvnw spring-boot:run        # en Windows: mvnw.cmd spring-boot:run
```
Queda escuchando en **http://localhost:8080**. Al arrancar siembra los usuarios de
prueba y datos de ejemplo (clientes, vehículos, mecánicos, órdenes).

### 3. Frontend
```bash
cd taller-mecanico-front
npm install
npm run dev
```
Abre **http://localhost:3000**.

> La URL del backend que consume el frontend está en
> `taller-mecanico-front/src/api/api.js` (`BASE_URL = http://localhost:8080`).

---

## Endpoints principales

| Método | Ruta                        | Rol requerido      |
|--------|-----------------------------|--------------------|
| POST   | `/auth/login`               | público            |
| POST   | `/auth/register`            | público            |
| GET    | `/api/clientes`             | USER o ADMIN       |
| POST/PUT/DELETE | `/api/clientes/**` | ADMIN              |
| GET    | `/api/vehiculos`            | USER o ADMIN       |
| POST/PUT/DELETE | `/api/vehiculos/**`| ADMIN              |
| GET/POST | `/api/reservas`           | USER o ADMIN       |
| GET/PUT | `/api/users/**`            | ADMIN              |
| GET    | `/api/dashboard/metrics`    | USER o ADMIN       |

**Seguridad esperada:** sin token → `401`; token USER en ruta ADMIN → `403`;
token ADMIN → `200/201/204`.
