# STAEM - Plataforma de Videojuegos (Microservicios)

STAEM es una plataforma de distribución de videojuegos basada en una arquitectura de **10 microservicios** (Usuarios, Catálogo, Pagos, Logros, Biblioteca, etc.) coordinados a través de un **API Gateway** y registrados dinámicamente con **Netflix Eureka**.

## 🚀 Requisitos Previos

Asegúrate de tener instalados los siguientes programas en tu sistema antes de comenzar:
- **Git** (para clonar el repositorio)
- **Java 21** o superior (si deseas compilar/probar localmente sin Docker)
- **Docker Desktop** (para levantar toda la infraestructura y base de datos)

---

## 📥 1. Cómo Descargar el Proyecto

Abre tu terminal (PowerShell, CMD o Bash) y ejecuta el siguiente comando para clonar el proyecto en tu máquina local:

```bash
git clone https://github.com/Hik4ru23/Evaluacion_2.git
cd Evaluacion_2/staem
```

---

## 🐳 2. Cómo Compilar y Levantar el Proyecto con Docker

El proyecto está dockerizado para que todos los microservicios y la red interna se configuren automáticamente. 

Desde la carpeta raíz del proyecto (donde se encuentra el archivo `docker-compose.yml`), ejecuta:

```bash
# 1. Compilar las imágenes de todos los microservicios
docker compose build

# 2. Levantar los contenedores en segundo plano
docker compose up -d
```

> **Nota:** La primera vez que ejecutes esto, puede tardar varios minutos mientras descarga las imágenes base de Java y compila los `.jar`.

Para verificar que todo levantó correctamente, puedes abrir en tu navegador:
- **Eureka (Registro de Servicios):** `http://localhost:8761`
- Si todos los servicios están en verde en Eureka, el ecosistema está listo.

---

## 🧪 3. Cómo Ejecutar las Pruebas Unitarias (Tests)

Cada microservicio cuenta con su propia suite de pruebas unitarias (**JUnit 5 + Mockito**) con una cobertura total de la capa de servicios (Lógica de Negocio), y que utilizan la estructura *Given-When-Then*.

Para ejecutar las pruebas y comprobar que el sistema es robusto, ingresa a la carpeta de cualquier microservicio y usa el **Maven Wrapper** incluido.

**En Windows (PowerShell/CMD):**
```cmd
cd pagos
.\mvnw.cmd test
```

**En Linux/Mac:**
```bash
cd pagos
./mvnw test
```

> Obtendrás un mensaje de **`BUILD SUCCESS`** indicando que `Failures: 0, Errors: 0`, demostrando que el código es 100% confiable y tolerante a fallos.

---

## 📖 4. Documentación de la API (Swagger)

Todos los endpoints han sido documentados interactiva y semánticamente. Una vez que el sistema esté levantado mediante Docker, puedes explorar la interfaz gráfica de Swagger de cualquier microservicio añadiendo `/swagger-ui.html` a su puerto.

Por ejemplo:
- **Usuarios:** `http://localhost:8081/swagger-ui.html`
- **Catálogo:** `http://localhost:8082/swagger-ui.html`
- **Pagos:** `http://localhost:8083/swagger-ui.html`

> Allí encontrarás ejemplos en formato JSON (`@ExampleObject`) para peticiones y respuestas HTTP.
