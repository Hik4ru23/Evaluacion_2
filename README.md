# Tienda de Juegos Digitales - Arquitectura de Microservicios

## Descripción del Proyecto
Plataforma distribuida para la gestión integral de una tienda de videojuegos digitales (estilo Steam o PlayStation Store). El sistema resuelve la necesidad de administrar usuarios, compras, catálogo y aspectos sociales de la comunidad gamer. Todo el ecosistema está construido bajo una estricta arquitectura de microservicios, garantizando alta cohesión, bajo acoplamiento y persistencia de datos independiente para cada dominio.

## Equipo de Desarrollo
* Enrique Ignacio Gutierrez Benites
* Gonzalo Yáñez Arenas

## Arquitectura y Tecnologías
El sistema cumple con el estándar de desacoplamiento total, estructurado bajo el patrón CSR (Controller - Service - Repository) e integrando las siguientes tecnologías:
* **Framework Backend:** Spring Boot (Java)
* **Persistencia:** Spring Data JPA / Hibernate
* **Seguridad:** Spring Security, Tokens JJWT y BCrypt
* **Service Discovery:** Eureka
* **Enrutamiento:** API Gateway
* **Comunicación:** Feign Client (sincrónica)

##  Microservicios Implementados
El sistema se compone de un mínimo de 10 microservicios autónomos:
1. **MS-Usuarios:** Gestión de cuentas y control de billetera virtual.
2. **MS-Catalogo:** Vitrina de videojuegos, desarrolladores y precios base.
3. **MS-Biblioteca:** Registro de juegos adquiridos y horas jugadas.
4. **MS-Carrito:** Gestión temporal de la intención de compra.
5. **MS-Pagos:** Procesamiento de transacciones y validación de saldo.
6. **MS-Resenas:** Sistema de calificaciones y comentarios de usuarios.
7. **MS-Ofertas:** Gestión de descuentos temporales aplicados al catálogo.
8. **MS-Comunidad:** Sistema de interacción social y lista de amistades.
9. **MS-Logros:** Registro de trofeos e hitos desbloqueados por los jugadores.
10. **MS-Notificaciones:** Alertas del sistema sobre compras y eventos.

## Modelo de Datos (DER)
<img width="2041" height="1562" alt="DER" src="https://github.com/user-attachments/assets/f4e275b6-904f-4bac-a02c-4f33595a2a3f" />

*(Nota: El sistema garantiza autonomía de datos. Cada microservicio gestiona su propia base de datos persistente e independiente, sin compartir tablas físicamente).*

## Pasos para Ejecutar
1. Clonar el repositorio: `git clone https://github.com/Hik4ru23/Evaluacion_2.git`
2. Configurar las credenciales del motor de base de datos en los archivos `application.properties` de cada microservicio.
3. Iniciar el servidor **Eureka** (Service Discovery).
4. Levantar de forma secuencial los microservicios restantes.
