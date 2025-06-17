# 🩺 CitaSaludFeature3 — Backend

Backend del módulo de gestión de horarios y médicos para el sistema **CitaSaludFeature3**, desarrollado con **Spring Boot**. Este backend soporta las funcionalidades definidas en las historias de usuario **HU001** y **HU002**.

## 🚀 Tecnologías utilizadas

- **Java 19** – Lenguaje principal del proyecto
- **Spring Boot** – Framework para desarrollo rápido de microservicios
- **Spring Web** – Para creación de APIs REST
- **Spring Data JPA** – Acceso a base de datos
- **PostgreSQL** – Motor de base de datos
- **MapStruct** – Mapeo entre entidades y DTOs
- **Swagger / OpenAPI** – Documentación de endpoints
- **Maven** – Gestión de dependencias y compilación

## 📁 Estructura del proyecto

├── controller/ # Controladores REST

├── domain/ # Entidades del modelo de dominio

├── dto/ # Data Transfer Objects

├── mapper/ # Clases de mapeo entre entidades y DTOs

├── repository/ # Interfaces JPA para acceso a datos

├── service/ # Interfaces y clases de lógica de negocio

├── config/ # Configuración de seguridad y otras

└── application/ # Clase principal para ejecución del backend


## 🎯 Endpoints principales

![Capturaendpoints1](https://github.com/user-attachments/assets/3764a71b-96d5-41b7-a6b0-359daebcab4f)
![Capturaendpoints2](https://github.com/user-attachments/assets/fae3c139-b654-44f9-a180-946635c33d5a)

### HU001 - Franjas horarias
- `GET /api/franjas/listarfranjas`
- `POST /api/franjas/{id}`
- `PUT /api/franjas/{idFranja}`
- `DELETE /api/franjas/{idFranja}`

### HU002 - Médicos
- `GET /api/medicos/obtenermedicos`
- `POST /api/medicos/crearmedico`
- `GET /api/medicos/confranjas`

[📁 Ver informe en Google Drive](https://drive.google.com/drive/folders/1XUitjg92WoS88TUI2N9mPXSZcm5BNcuW?usp=sharing)

## 🌐 Despliegue Automatizado

Este proyecto utiliza **Railway** para el despliegue de la aplicación backend y **GitHub Actions** para automatizar el proceso de integración continua y despliegue continuo (CI/CD).

### ¿Cómo funciona el despliegue?

1.  **Railway como Plataforma de Despliegue:**
    * La aplicación está desplegada en Railway, una plataforma que facilita el despliegue de servicios.
    * Puedes acceder al servicio backend en la siguiente URL:[https://citasaludfeature3-backend-production.up.railway.app](https://citasaludfeature3-backend-production.up.railway.app/hola).
    * Los logs de la aplicación y el estado del servicio pueden ser monitoreados desde el dashboard de Railway.

2.  **GitHub Actions para CI/CD:**
    * Cada vez que se realiza un `push` a la rama `main` del repositorio de GitHub, se activa automáticamente un workflow de GitHub Actions.
    * Este workflow (`.github/workflows/build.yml`) se encarga de:
        * Obtener el código fuente del repositorio.
        * Configurar el entorno Java (JDK 19) necesario para la aplicación.
        * Construir el proyecto Spring Boot utilizando Maven, generando el paquete `.jar` ejecutable.
        * Instalar la interfaz de línea de comandos (CLI) de Railway.
        * Autenticarse con Railway utilizando un token de API seguro (`RAILWAY_TOKEN`) almacenado como un secreto en GitHub.
        * Desplegar la aplicación en el servicio `CitaSaludFeature3---Backend` dentro del proyecto de Railway con ID `84fc06b3-5d60-4cf6-a0ab-db526a11ce80`.



