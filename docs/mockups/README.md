# Mockups SafeRoute

Prototipos HTML locales para revisar navegación y arquitectura de información. Los datos visibles son ejemplos ilustrativos, no datos persistidos ni reglas de negocio confirmadas.

## Abrir

Abra `html/index.html` en un navegador moderno. Use el menú lateral para cambiar de pantalla. Los formularios muestran validación local y los controles de demostración cambian estados solo en el navegador; no llaman a una API ni guardan información.

## Capturas

Para generar capturas PNG con Playwright:

1. Ejecute `npm install` dentro de `docs/mockups`.
2. Ejecute `npx playwright install chromium` si Chromium no está disponible.
3. Ejecute `npm run capturar`.

Las capturas se guardan en `img/`. Si el entorno no permite instalar navegador o dependencias, las capturas no se consideran verificadas.

## Alcance

El plano es una representación esquemática de infraestructura educativa, sin geolocalización ni seguimiento de personas. Las pantallas reflejan módulos de los diagramas y asociaciones con HU. No simulan operaciones reales de la aplicación.
