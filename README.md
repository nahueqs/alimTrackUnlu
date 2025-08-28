# 📡 AlimTrack – Sistema de Monitoreo en Tiempo Real  | PROYECTO EN CURSO
**Planta Piloto – Universidad Nacional de Luján (CIDETA https://www.cideta.unlu.edu.ar/)**  

AlimTrack es un sistema de **monitoreo en tiempo real** desarrollado con **Spring Boot** y **MySQL**, que permite la gestión, seguimiento y visualización de producciones en curso en la planta piloto de la UNLu.  

El proyecto sigue una **arquitectura en capas**, utilizando **DTOs específicos para cada tipo de petición**.
En proceso: Autenticación con Spring Security, monitoreo en tiempo real con Websockets, Front-End a definir.

---

## 🏗️ Arquitectura

- **Capa de Controladores (API REST):** Endpoints RESTful.  
- **Capa de Servicios:** Lógica de negocio y validaciones.  
- **Capa de DTOs:** Objetos de transferencia de datos dedicados.  
- **Capa de Repositorios:** Persistencia con Spring Data JPA / Hibernate.  
- **Capa de Seguridad:** En proceso, implementada con Spring Security.  

---

## ⚙️ Funcionalidades principales

✅ Ingreso y gestión de producciones en curso.  
✅ Modificación de datos durante la producción.  
✅ Administración de recetas, versiones, secciones, tablas y campos.  
✅ Exportación de producciones a PDF.  
✅ Gestión de usuarios (altas, bajas, permisos).  
✅ Compartir enlace público de producción en curso.  
---

