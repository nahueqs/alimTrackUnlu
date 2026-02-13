# 📡 AlimTrack – Sistema de Monitoreo en Tiempo Real
> Planta Piloto – Universidad Nacional de Luján (CIDETA)
> [www.cideta.unlu.edu.ar](https://www.cideta.unlu.edu.ar/)

Página de estado para ver si la app está funcionando 
https://stats.uptimerobot.com/QGOCuP1tVl

Documentación técnica detallada de la aplicación 
https://docs.google.com/document/d/1yaOenWdKgp4qgxN-N-T2VKPGYcVe_pqCkQ6jJry0hLM/edit?usp=drivesdk

AlimTrack es una plataforma integral para la gestión, seguimiento y visualización de producciones alimenticias en tiempo real. Desarrollado para el CIDETA-UNLu, el sistema digitaliza la captura de datos en planta, permitiendo una trazabilidad total y eliminando la dependencia de registros físicos mediante una arquitectura moderna y robusta.

## 🎯 Objetivo del Proyecto
Modernizar el ecosistema productivo de la planta piloto, permitiendo:
* Estandarización: Registro basado en recetas con control de versiones dinámicas.
* Monitoreo Live: Seguimiento continuo de producciones activas desde múltiples dispositivos, en tiempo real.
* Auditoría: Logs detallados de cada cambio y generación automática de reportes PDF.

# ⚙️ Estado del Desarrollo

#✅ Versión 1.0 (En Producción)
* Gestión de Recetas: Estructura flexible para registrar cualquier tipo de receta deseada por la planta, con estructuras anidadas de secciones, campos agrupados, tablas y campos simples.
* Gestión de producciones: El sistema permite iniciar producciones en base a las recetas, completarlas y visualizarlas en páginas públicas en tiempo real. Se notifican los cambios de valores y de estado.
* Seguridad Avanzada: Autenticación y autorización mediante Spring Security 6 y JWT.
* Comunicación Real-Time: Sincronización de datos entre planta y oficina vía WebSockets (STOMP).
* Sistema de Notificaciones: Alertas push integradas para notificar cambios en las producciones en curso.
* Auditoría y Trazabilidad: Registro histórico de cambios.
  
# 🟠 En Desarrollo 
* Dashboard de Gestión de Stock: Control de insumos y materias primas vinculadas a producción.
* Módulo de KPIs: Visualización de indicadores clave de rendimiento y eficiencia de planta.

Stack tecnológico utilizado:
# 💻 Backend (Java Stack)
Construido bajo un patrón de Arquitectura en Capas para garantizar mantenibilidad y escalabilidad.

* Core: Java 17 con Spring Boot 3.
* Seguridad: Spring Security 6 con JWT.
* Persistencia: Spring Data JPA con Hibernate como ORM, gestionando una base de datos MySQL 8.0.
* Mensajería Real-Time: pring WebSocket** con protocolo STOMP sobre SockJS para garantizar compatibilidad.
* Validación: JSR-303 (Hibernate Validator) para integridad de datos en el ingreso de producciones.
* Documentación: OpenAPI 3 / Swagger UI para la exposición y prueba de endpoints REST.
* Logging: SLF4J con Logback para el rastreo de eventos de auditoría.

## 🛠️ Ingeniería y Calidad de Software
El proyecto sigue estándares de la industria para asegurar un código limpio, mantenible y escalable:

* **Testing:** Cobertura exhaustiva con **JUnit 5** y **Mockito**. Tests de integración con **MockMvc** para validar seguridad y contratos de API.
* **Arquitectura Orientada a Eventos:** Desacoplamiento de lógica mediante `Spring Events` para manejar notificaciones WebSocket sin impactar el rendimiento de las transacciones principales.
* **Patrones de Diseño:** Uso extensivo de DTOs, Mappers (MapStruct) y Servicios Base para reutilización de código.
* **Rendimiento:** Procesamiento asíncrono (`@Async`) de tareas no bloqueantes y gestión eficiente de transacciones (`@Transactional`).
* **Manejo de Errores:** Respuestas HTTP estandarizadas y descriptivas mediante un `GlobalExceptionHandler` centralizado.

### 🎨 Frontend (React Stack) Respositorio: https://github.com/nahueqs/alimtrack-front
Desarrollado como una **Single Page Application (SPA)** enfocada en la experiencia de usuario en entornos industriales.

* **Core:** **React 18** utilizando **TypeScript** para un desarrollo robusto y tipado.
* **Gestión de Estado:** Arquitectura basada en **Hooks** y **Context API** para el manejo global de la sesión y estados de producción.
* **Routing:** **React Router Dom 6** para la navegación protegida por roles.
* **Comunicación:** * **Axios** para el consumo de la API REST con interceptores para el token JWT.
    * **StompJS** y **SockJS-client** para la escucha activa de sockets.
* **UI/UX:** Componentes modulares reutilizables con UI de Ant design
* **Herramienta de Construcción:** **Vite** para un entorno de desarrollo rápido y builds optimizados.
  

Desarrollador: Nahuel Quiñones https://www.linkedin.com/in/nahueqs/
