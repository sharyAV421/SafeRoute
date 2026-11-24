# SafeRoute

<p align="center">
  <strong>Sistema web de apoyo a la gestión y evacuación ante emergencias en equipamientos educativos</strong>
</p>

<p align="center">
  <a href="https://saferoute-blush.vercel.app/"><img src="https://img.shields.io/badge/Aplicaci%C3%B3n-Web-0F766E?style=for-the-badge" alt="Aplicación web"/></a>
  <img src="https://img.shields.io/badge/Frontend-React%20%7C%20TypeScript-3178C6?style=for-the-badge" alt="React y TypeScript"/>
  <img src="https://img.shields.io/badge/Backend-Java%2021%20%7C%20Spring%20Boot-6DB33F?style=for-the-badge" alt="Java y Spring Boot"/>
  <img src="https://img.shields.io/badge/Database-MongoDB%20Atlas-47A248?style=for-the-badge" alt="MongoDB Atlas"/>
</p>

## Descripción

**SafeRoute** es una aplicación web orientada a apoyar la preparación, organización y gestión de procesos de evacuación ante situaciones de emergencia en instituciones educativas. Centraliza información institucional y de infraestructura, y reúne herramientas para administrar los elementos que intervienen en la coordinación de una evacuación.

La plataforma ofrece acceso según roles y una interfaz que integra la gestión de usuarios, infraestructura, riesgos, emergencias y rutas de evacuación.

## Acceso a la aplicación

<p align="center">
  <a href="https://saferoute-blush.vercel.app/"><strong>INGRESAR A SAFEROUTE</strong></a>
</p>

1. Abre la aplicación desde el enlace de acceso.
2. Ingresa con el correo institucional y la contraseña de la cuenta asignada.
3. Selecciona las opciones disponibles en el panel según el rol de acceso.

**Perfiles del sistema**

- **Administrador:** administración de usuarios, roles e información institucional.
- **Responsable de emergencias:** acceso a las herramientas de gestión relacionadas con infraestructura, riesgos y emergencias.
- **Usuario evacuado:** acceso a las funcionalidades destinadas al proceso de evacuación.

## Funcionalidades principales

| Módulo | Descripción |
|---|---|
| **Autenticación y usuarios** | Inicio de sesión, gestión de usuarios y asignación de roles. |
| **Infraestructura institucional** | Registro y administración de instituciones, edificios, pisos y espacios. |
| **Conexiones** | Organización de las conexiones entre espacios de la infraestructura. |
| **Salidas y puntos de encuentro** | Administración de salidas de emergencia y puntos de encuentro. |
| **Emergencias** | Registro y gestión de información relacionada con eventos de emergencia. |
| **Riesgos** | Gestión de niveles y características asociadas a los riesgos. |
| **Rutas de evacuación** | Gestión y consulta de información relacionada con recorridos de evacuación. |

## Arquitectura y tecnologías

SafeRoute utiliza una arquitectura cliente-servidor con separación entre presentación, lógica de negocio y acceso a datos.

| Componente | Tecnologías |
|---|---|
| **Frontend** | React, TypeScript y Vite |
| **Backend** | Java 21, Spring Boot y Spring Data MongoDB |
| **Seguridad** | Spring Security y JSON Web Token (JWT) |
| **Base de datos** | MongoDB Atlas |
| **Despliegue del frontend** | Vercel |
| **Despliegue del backend** | Render |

### Organización del backend

El backend se estructura en capas que separan las responsabilidades del sistema:

- `Presentacion_Controller`: recepción y atención de solicitudes HTTP.
- `Negocio_Service`: implementación de las operaciones y reglas de negocio.
- `Datos_Repository`: comunicación con MongoDB.
- `Modelo_de_Dominio`: representación de las entidades del sistema.

## Presentación de diagramas y diseño

La presentación reúne los diagramas de arquitectura y diseño de software elaborados para SafeRoute.

<p align="center">
  <a href="PEGAR_AQUI_EL_ENLACE_DE_CANVA">
    <img src="https://img.shields.io/badge/Canva-Ver%20presentaci%C3%B3n%20de%20diagramas-7B2FF7?style=for-the-badge&logo=canva&logoColor=white" alt="Ver presentación de diagramas en Canva"/>
  </a>
</p>

> **Enlace de Canva:** reemplazar `PEGAR_AQUI_EL_ENLACE_DE_CANVA` por el enlace de visualización de la presentación.

## Repositorio

El código fuente del proyecto está organizado en los siguientes directorios:

```text
SafeRoute/
├── backend/     # API REST y lógica del servidor
├── frontend/    # Aplicación web React + TypeScript
└── docs/        # Historias de usuario y documentación del proyecto
```

- [Repositorio de GitHub](https://github.com/sharyAV421/SafeRoute)
- [Aplicación web](https://saferoute-blush.vercel.app/)
- [API de SafeRoute](https://saferoute-gede.onrender.com)

---

<p align="center">
  <strong>SafeRoute</strong><br/>
  Gestión y apoyo a la evacuación en equipamientos educativos.
</p>
