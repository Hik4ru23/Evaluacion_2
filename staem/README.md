# 🎮 STAEM - Plataforma Distribuida de Videojuegos (Arquitectura de Microservicios)

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger)
![JUnit5](https://img.shields.io/badge/JUnit5-Mockito-25A162?style=for-the-badge)

## 📖 Resumen Ejecutivo
**STAEM** es una plataforma escalable para la gestión integral de una tienda de videojuegos digitales (inspirada en ecosistemas como Steam o PlayStation Store). El sistema resuelve de manera eficiente la administración de cuentas de usuario, transacciones comerciales complejas, gestión de catálogo dinámico y módulos de interacción social para la comunidad gamer.

Este proyecto fue desarrollado bajo una estricta **Arquitectura de Microservicios**, garantizando los principios de **Alta Cohesión**, **Bajo Acoplamiento** y **Autonomía de Datos** (Database-per-service).

---

## 👨‍💻 Equipo de Desarrollo (Ingeniería de Software)
* **Enrique Ignacio Gutierrez Benites**
* **Gonzalo Yáñez Arenas**

---

## 🏗️ Arquitectura y Tecnologías Core

El sistema implementa el patrón de desacoplamiento absoluto, estructurando cada microservicio interno bajo el patrón de diseño arquitectónico **CSR (Controller - Service - Repository)** y aplicando principios de **Clean Code** (código autodocumentado, sin comentarios innecesarios, responsabilidades únicas).

### Stack Tecnológico
* **Lenguaje:** Java 21 (Aprovechando Virtual Threads y Records).
* **Framework Backend:** Spring Boot 3.x / Spring Cloud.
* **Persistencia:** Spring Data JPA / Hibernate.
* **Motor de Base de Datos:** PostgreSQL (Alojado en **Supabase**, 10 bases de datos independientes).
* **Service Discovery & Registry:** Spring Cloud Netflix Eureka.
* **Edge Server / Enrutador:** Spring Cloud Gateway.
* **Comunicación Inter-Servicio:** Spring Cloud OpenFeign (Llamadas REST Sincrónicas).
* **Testing:** JUnit 5 + Mockito (Metodología *Given-When-Then*).
* **Documentación:** Springdoc OpenAPI (Swagger UI interactivo).
* **Contenedores:** Docker y Docker Compose.

---

## 🧩 Ecosistema de Infraestructura y Puertos

El sistema se compone de **10 microservicios de dominio**, coordinados por un servidor de descubrimiento y un API Gateway que actúa como única puerta de entrada al ecosistema.

| Componente / Microservicio | Puerto | Rol Técnico / Descripción |
| :--- | :---: | :--- |
| **`eureka-server`** | `8761` | **Registro Central:** Descubrimiento dinámico de instancias vivas. |
| **`api-gateway`** | `8080` | **Edge Router:** Enrutamiento dinámico hacia los microservicios, ocultando los puertos internos. |
| **`usuarios`** | `8081` | **Dominio:** Gestión de identidades, perfiles y control de la billetera virtual (saldo). |
| **`catalogo`** | `8082` | **Dominio:** Vitrina global de videojuegos, gestión de stock y precios base. |
| **`biblioteca`** | `8083` | **Dominio:** Registro inmutable de los juegos adquiridos por cada jugador. |
| **`pagos`** | `8085` | **Dominio (Core):** Orquestador de transacciones. Valida saldos, descuenta stock y autoriza la compra. |
| **`logros`** | `8087` | **Dominio:** Sistema de trofeos y recompensas por hitos desbloqueados. |
| **`amigos`** | `8088` | **Dominio:** Red social interna, estados de actividad y listas de amistades. |
| **`carrito`** | `8089` | **Dominio:** Memoria temporal de la intención de compra del cliente. |
| **`resenas`** | `8090` | **Dominio:** Feedback de la comunidad (calificaciones por estrellas y comentarios). |
| **`promociones`** | `8091` | **Dominio:** Lógica de inyección de descuentos temporales sobre el catálogo. |
| **`soporte`** | `8092` | **Dominio:** Gestión de tickets de asistencia técnica para usuarios. |

---

## 🗄️ Modelo de Datos (DER) y Autonomía

El sistema garantiza la **Autonomía Absoluta a nivel de almacenamiento**. Cada microservicio gestiona su propio esquema lógico y sus propias credenciales en la nube. **Están estrictamente prohibidos los JOINs directos entre tablas de distintos contextos acotados.** Si el microservicio de Pagos necesita datos de Usuarios, lo solicita exclusivamente a través de la red (OpenFeign).

<img width="2041" height="1562" alt="Modelo de Datos" src="https://github.com/user-attachments/assets/f4e275b6-904f-4bac-a02c-4f33595a2a3f" />

---

## 🚀 Guía Rápida de Instalación y Despliegue (Docker)

Toda la complejidad de la infraestructura ha sido automatizada mediante contenedores.

### 1. Clonar el repositorio
Abre tu terminal (Bash, PowerShell) y ejecuta:
```bash
git clone https://github.com/Hik4ru23/Evaluacion_2.git
cd Evaluacion_2/staem
```

### 2. Levantar el Ecosistema Completo
Asegúrate de tener el demonio de **Docker** en ejecución. Desde la carpeta raíz, ejecuta:
```bash
# Compila todas las imágenes Java y empaqueta los microservicios
docker compose build

# Levanta toda la infraestructura en segundo plano
docker compose up -d
```

### 3. Verificar la Salud del Sistema
Abre tu navegador y entra a **Eureka Server**:
👉 `http://localhost:8761`
*Allí verás listados todos los microservicios (USUARIOS, PAGOS, CATALOGO, etc.) en estado "UP".*

---

## 🧪 Ingeniería de Calidad: Pruebas Unitarias

La calidad del código ha sido garantizada mediante un exhaustivo blindaje de Pruebas Unitarias. 
* Se utilizó **JUnit 5 y Mockito**.
* Las pruebas cubren el 100% de la capa de Servicios (Reglas de Negocio).
* Se evalúan tanto los escenarios de éxito (Happy Path) como el manejo de excepciones (Negative Cases).
* Se redactaron utilizando el estándar internacional **Given-When-Then** para máxima legibilidad.

**Cómo ejecutar los tests localmente:**
Para validar la integridad de la lógica, puedes usar el *Maven Wrapper* dentro de cualquier microservicio:

```bash
# Ejemplo para probar el sistema de Pagos
cd pagos
.\mvnw.cmd test    # En Windows
./mvnw test        # En Linux/Mac
```
*Si la arquitectura no ha sido comprometida, el resultado será un `BUILD SUCCESS`.*

---

## 📖 Documentación Semántica (Swagger OpenAPI)

Cada uno de los 10 microservicios expone su propia documentación interactiva y estandarizada bajo la especificación OpenAPI 3.0. 
Se incluyeron descripciones ricas (`@Operation`), tipado de respuestas (`@ApiResponses`) y ejemplos JSON exactos (`@ExampleObject`) de qué enviar y qué se recibirá, incluyendo ejemplos de errores estructurales.

Una vez levantado el proyecto, puedes probar las APIs visualmente accediendo a las rutas:

* 👤 **Usuarios:** `http://localhost:8081/swagger-ui.html`
* 🎮 **Catálogo:** `http://localhost:8082/swagger-ui.html`
* 💳 **Pagos:** `http://localhost:8085/swagger-ui.html`
* 📚 **Biblioteca:** `http://localhost:8083/swagger-ui.html`
* 🛒 **Carrito:** `http://localhost:8089/swagger-ui.html`
* *(y así sucesivamente con cada puerto).*
