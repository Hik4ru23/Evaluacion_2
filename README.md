# Tienda de Juegos Digitales - Arquitectura de Microservicios

## Descripción del Proyecto
Plataforma distribuida y altamente escalable para la gestión integral de una tienda de videojuegos digitales (inspirada en ecosistemas como Steam). El sistema resuelve de manera eficiente la administración de usuarios, transacciones comerciales, catálogo dinámico y módulos de interacción social para la comunidad gamer. 

Todo el ecosistema está diseñado bajo una estricta arquitectura de microservicios distribuidos. Cada módulo cumple con el principio de alta cohesión y bajo acoplamiento, implementando una persistencia de datos totalmente aislada e independiente por dominio para garantizar la resiliencia y la autonomía del sistema.

## Equipo de Desarrollo
* Enrique Ignacio Gutierrez Benites
* Gonzalo Yáñez Arenas

## Arquitectura y Tecnologías Core
El sistema implementa el patrón de desacoplamiento absoluto, estructurando cada microservicio interno bajo el patrón de diseño arquitectónico **CSR (Controller - Service - Repository)**.

* **Lenguaje de Programación:** Java 21
* **Framework Backend:** Spring Boot 3.x
* **Gestor de Dependencias y Construcción:** Maven
* **Persistencia de Datos:** Spring Data JPA / Hibernate
* **Bases de Datos:** Persistencia políglota/aislada (Conexión remota optimizada a PostgreSQL en entornos Cloud mediante Supabase)
* **Service Discovery & Registry:** Spring Cloud Netflix Eureka
* **Enrutamiento y Edge Server:** Spring Cloud Gateway
* **Comunicación Inter-servicio:** Spring Cloud OpenFeign (Declarativa / Sincrónica)
* **Manejo de Boilerplate:** Proyecto Lombok

---

## Ecosistema de Infraestructura y Puertos

Para evitar colisiones en entornos de desarrollo local y garantizar el correcto enrutamiento dinámico, se ha definido la siguiente matriz de puertos estáticos:

| Componente / Microservicio | Puerto Core | Tipo / Rol Técnico | Descripción |
| :--- | :---: | :--- | :--- |
| **`eureka-server`** | `8761` | Servidor de Descubrimiento | Registro central de instancias activas. |
| **`api-gateway`** | `8080` | Edge Server / Enrutador | Puerta de entrada única para clientes externos. |
| **`auth`** | `8093` | Microservicio de Dominio | Gestión de tokens, inicio de sesión y validación de credenciales. |
| **`usuarios`** | `8081` | Microservicio de Dominio | Cuentas de usuario, perfiles y billetera virtual. |
| **`catalogo`** | `8082` | Microservicio de Dominio | Vitrina de videojuegos, desarrolladores y precios base. |
| **`biblioteca`** | `8083` | Microservicio de Dominio | Registro de juegos adquiridos y métricas de horas jugadas. |
| **`carrito`** | `8089` | Microservicio de Dominio | Gestión temporal del estado de la intención de compra. |
| **`pagos`** | `8085` | Microservicio de Dominio | Procesamiento de transacciones y validación de saldos en cuenta. |
| **`resenas`** | `8090` | Microservicio de Dominio | Calificaciones por estrellas y comentarios escritos de usuarios. |
| **`promociones`** | `8091` | Microservicio de Dominio | Lógica de descuentos temporales e inyección de ofertas al catálogo. |
| **`amigos`** | `8088` | Microservicio de Dominio | Red social interna, estados de actividad y listas de amistades. |
| **`soporte`** | `8092` | Microservicio de Dominio | Sistema de tickets de atención al cliente y flujos de estados. |

---

## Comportamiento y Flujo de Comunicación

El sistema opera bajo un flujo descentralizado de resolución de peticiones externas e internas:

1. **Punto de Entrada Único (Edge Routing):** Toda solicitud realizada por una aplicación cliente (Frontend) impacta directamente en el **API Gateway** (`8080`). Ningún cliente externo interactúa directamente con los puertos internos de los microservicios de dominio.
2. **Descubrimiento Dinámico:** Al arrancar, cada microservicio se registra automáticamente con su propiedad `spring.application.name` ante el servidor **Eureka** (`8761`), enviando un *heartbeat* constante que notifica su estado de salud (Up/Down).
3. **Resolución de Rutas:** El API Gateway utiliza la propiedad `spring.cloud.gateway.discovery.locator` para mapear los endpoints dinámicamente consultando a Eureka. Por ejemplo, una petición a `http://localhost:8080/api/carrito/` se redirige automáticamente hacia la instancia disponible en el puerto `8089`.
4. **Comunicación Inter-servicio (OpenFeign):** Cuando un servicio requiere datos de otro dominio (por ejemplo, cuando `auth` necesita validar credenciales en `usuarios`, o `pagos` requiere verificar el saldo de una cuenta), se invoca una interfaz declarativa anotada con `@FeignClient`. El cliente Feign intercepta la llamada, consulta la ubicación exacta a Eureka y ejecuta una petición HTTP sincrónica interna transparente para el desarrollador.

---

## Modelo de Datos (DER)
<img width="2041" height="1562" alt="DER" src="https://github.com/user-attachments/assets/f4e275b6-904f-4bac-a02c-4f33595a2a3f" />

*(Nota de Integridad de Datos: Se garantiza autonomía absoluta a nivel de almacenamiento. Cada microservicio gestiona esquemas lógicos y credenciales independientes en la nube, prohibiendo terminantemente los JOINs directos entre tablas de distintos contextos acotados).*

---

## Guía de Compilación, Instalación y Despliegue

### Requisitos Previos Necesarios
* **Java Development Kit (JDK):** Versión 21 instalada y configurada en las variables de entorno (`JAVA_HOME`).
* **Apache Maven:** Versión 3.9 o superior.
* **Conexión a Red:** Requerida para la resolución de dependencias desde Maven Central y conexión activa a los servidores remotos de base de datos PostgreSQL.

### 1. Clonación del Repositorio
Navega a tu directorio local de trabajo y descarga el código fuente:
```bash
git clone [https://github.com/Hik4ru23/Evaluacion_2.git](https://github.com/Hik4ru23/Evaluacion_2.git)
cd Evaluacion_2
