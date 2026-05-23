# Tienda de Juegos Digitales - Arquitectura de Microservicios 🎮

## 📝 Descripción del Proyecto
Plataforma distribuida para la gestión integral de una tienda de videojuegos digitales (estilo Steam o PlayStation Store). El sistema resuelve la necesidad de administrar usuarios, compras, catálogo y aspectos sociales de la comunidad gamer. Todo el ecosistema está construido bajo una arquitectura de microservicios, garantizando alta cohesión, bajo acoplamiento y persistencia de datos independiente para cada dominio.

---

## 👥 Equipo de Desarrollo
* **Enrique Ignacio Gutierrez Benites**
* **Gonzalo Yáñez Arenas**

---

## 🛠️ Arquitectura y Tecnologías
El sistema cumple con el estándar de desacoplamiento total, estructurado bajo el patrón **CSR (Controller - Service - Repository)** e integrando las siguientes tecnologías:

* **Framework Backend:** Spring Boot 4.0.6 (Java 21/25)
* **Persistencia:** Spring Data JPA / Hibernate
* **Motor de Base de Datos:** PostgreSQL (alojado de forma independiente en Supabase para cada servicio)
* **Migraciones de Base de Datos:** Flyway / SQL
* **Validaciones:** JSR 380 (Bean Validation) con control centralizado de excepciones (`@ControllerAdvice`)
* **Comunicación Inter-servicio:** WebClient (sincrónica mediante llamadas bloqueantes `.block()`)

---

## 🔌 Microservicios y Puertos
El sistema se compone de **10 microservicios autónomos**, cada uno gestionando su propia base de datos persistente:

| # | Microservicio | Directorio | Puerto | Descripción |
|---|---|---|---|---|
| 1 | **MS-Usuarios** | `/usuarios` | `8082` | Gestión de cuentas y control de billetera virtual (saldo). |
| 2 | **MS-Catalogo** | `/catalogo` | `8083` | Vitrina de videojuegos, desarrolladores, stock y precios base. |
| 3 | **MS-Biblioteca** | `/biblioteca` | `8084` | Registro de juegos adquiridos por los usuarios y horas jugadas. |
| 4 | **MS-Pagos** | `/pagos` | `8086` | Procesamiento de compras, validación de stock/saldo y transacciones. |
| 5 | **MS-Logros** | `/logros` | `8087` | Registro de trofeos e hitos desbloqueados por los jugadores. |
| 6 | **MS-Comunidad** | `/amigos` | `8088` | Sistema de interacción social y lista de amistades. |
| 7 | **MS-Carrito** | `/carrito` | `8089` | Gestión temporal de la intención de compra del usuario. |
| 8 | **MS-Reseñas** | `/resenas` | `8090` | Sistema de calificaciones y comentarios de usuarios sobre juegos. |
| 9 | **MS-Ofertas** | `/promociones` | `8091` | Gestión de descuentos temporales aplicados al catálogo. |
| 10 | **MS-Soporte** | `/soporte` | `8092` | Creación y gestión de tickets de asistencia técnica para usuarios. |

---

## 📊 Modelo de Datos (DER)
El sistema garantiza la **autonomía de datos**. Cada microservicio cuenta con su propio esquema persistente e independiente, comunicándose únicamente a través de APIs REST.

<img width="2041" height="1562" alt="DER" src="https://github.com/user-attachments/assets/f4e275b6-904f-4bac-a02c-4f33595a2a3f" />

---

## 🚀 Pasos para Ejecutar

### 1. Requisitos Previos
* Java 21 o superior instalado.
* Maven instalado (o usar el wrapper `./mvnw` provisto).

### 2. Clonar el repositorio
```bash
git clone https://github.com/Hik4ru23/Evaluacion_2.git
cd Evaluacion_2-main
```

### 3. Configurar la Base de Datos
Cada microservicio cuenta con su archivo `application.properties` en `src/main/resources/`. Por defecto, están configurados para conectarse a instancias PostgreSQL en Supabase. Si deseas usar bases de datos locales, asegúrate de actualizar las siguientes propiedades en cada servicio:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tu_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 4. Levantar los Microservicios
Levanta cada microservicio ejecutando el siguiente comando de Maven dentro del directorio de cada uno de ellos (o ejecutándolos desde tu IDE favorito):
```bash
# Ejemplo para levantar MS-Usuarios:
cd staem/usuarios
./mvnw spring-boot:run
```
*(Repite el proceso para los 10 microservicios de manera secuencial).*
