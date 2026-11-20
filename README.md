# SafeRoute

Repositorio académico de SafeRoute, un sistema de apoyo a la gestión y evacuación ante emergencias en equipamientos educativos.

## Estado del repositorio

Se inició una primera implementación con React + TypeScript y Java/Spring Boot + Spring Data MongoDB. Consulta [docs/implementacion/README.md](docs/implementacion/README.md) para instalación, configuración y alcance actual. La conexión se configura por variables de entorno y no se incluyen credenciales.

## Documentación

- Historias de usuario, trazabilidad HU–RF y pendientes: [`docs/historias-usuario/00-resumen.md`](docs/historias-usuario/00-resumen.md).
- Prototipos HTML navegables y guía de apertura: [`docs/mockups/README.md`](docs/mockups/README.md).
- Guía de identidad visual: [`docs/mockups/00-guia-de-estilo.html`](docs/mockups/00-guia-de-estilo.html).
- Matriz de trazabilidad de implementación: [`docs/implementacion/trazabilidad.md`](docs/implementacion/trazabilidad.md).

## Requisitos para completar la aplicación

La fórmula de prioridad y velocidad base permanecen pendientes: la aplicación no inventa su resultado. Los detalles faltantes de RF, estados y validaciones también están identificados en la matriz nueva y deben completarse antes de declarar cobertura funcional total. La ruta usa Dijkstra, bloqueos explícitos y los desempates aprobados; el tiempo aparece como «No disponible».

No se ha agregado configuración o secreto de MongoDB ni una base de datos simulada. No se implementan GPS, seguimiento, sensores o detección automática.
