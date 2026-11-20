# SafeRoute — historias de usuario

## Estado y fuentes

Este documento se elaboró a partir de las cinco vistas UML entregadas: contexto, componentes, clases, desarrollo y despliegue. La vista de contexto identifica Administrador, Responsable de emergencias y Usuario evacuado. La vista funcional incluye Gestión de usuarios, Gestión de infraestructura, Gestión de emergencias, Gestión de riesgos y Cálculo y visualización de rutas. La vista de desarrollo confirma pantallas y controladores separados, servicios, repositorios y entidades del dominio. Despliegue indica React + TypeScript en navegador, API REST Java/Spring Boot y MongoDB Atlas.

La tabla de RF recibida en la solicitud incluye IDs y títulos, pero no descripciones completas ni reglas de aceptación. Por ello, la correspondencia HU–RF siguiente es provisional y reproduce únicamente los títulos proporcionados. Criterios concretos se expresan como supuestos a validar, no como reglas confirmadas. El documento “Requisitos Funcionales y No Funcionales.docx” localizado en Descargas pertenece a un sistema de inversiones y no se usa como fuente.

## Índice

| HU | Historia | RF | Componentes UML principales | Mockups |
|---|---|---|---|---|
| [HU-01](HU-01.md) | Inicio de sesión | RF-01 | Gestión de usuarios, UsuarioController/Service/Repository | M01 |
| [HU-02](HU-02.md) | Gestión de roles y permisos | RF-02 | Gestión de usuarios, Usuario, Rol | M02 |
| [HU-03](HU-03.md) | Gestión de edificios, pisos y espacios | RF-03, RF-04, RF-05 | Gestión de infraestructura, Institucion, Edificio, Piso, Espacio | M03 |
| [HU-04](HU-04.md) | Registro de conexiones entre espacios | RF-06 | Gestión de infraestructura, Conexion | M04 |
| [HU-05](HU-05.md) | Gestión de salidas y puntos de encuentro | RF-07 | Gestión de infraestructura, SalidaEmergencia, PuntoEncuentro | M05 |
| [HU-06](HU-06.md) | Registro y administración de emergencias | RF-08, RF-09 | Gestión de emergencias, Emergencia | M06 |
| [HU-07](HU-07.md) | Identificación de zonas afectadas | RF-10 | Gestión de emergencias, ZonaAfectada | M07 |
| [HU-08](HU-08.md) | Visualización de emergencias | RF-11 | Gestión de emergencias, Emergencia, PanelResponsable | M08 |
| [HU-09](HU-09.md) | Configuración de niveles de riesgo | RF-12 | Gestión de riesgos, NivelRiesgo | M09 |
| [HU-10](HU-10.md) | Registro de características de riesgo | RF-13 | Gestión de riesgos, CaracteristicaRiesgo | M10 |
| [HU-11](HU-11.md) | Determinación de prioridades de evacuación | RF-14 | Gestión de riesgos, GestorRiesgos, Prioridad | M11 |
| [HU-12](HU-12.md) | Visualización de riesgos y prioridades | RF-15 | Gestión de riesgos, PanelResponsable | M12 |
| [HU-13](HU-13.md) | Selección de ubicación del evacuado | RF-16 | Cálculo de rutas, PantallaEvacuacion | M13 |
| [HU-14](HU-14.md) | Cálculo de rutas de evacuación | RF-17, RF-18 | RutaController/Service/Repository, RutaEvacuacion | M14 |
| [HU-15](HU-15.md) | Visualización de rutas | RF-19 | Cálculo y visualización de rutas, PantallaEvacuacion | M15 |
| [HU-16](HU-16.md) | Consulta de información de la ruta | RF-20 | RutaDTO, RutaEvacuacion, PasosRuta | M16 |

## Matriz de trazabilidad HU–RF

| RF | HU | Título indicado por la solicitud |
|---|---|---|
| RF-01 | HU-01 | Inicio de sesión |
| RF-02 | HU-02 | Gestión de roles y permisos |
| RF-03, RF-04, RF-05 | HU-03 | Gestión de edificios, pisos y espacios |
| RF-06 | HU-04 | Registro de conexiones entre espacios |
| RF-07 | HU-05 | Gestión de salidas y puntos de encuentro |
| RF-08, RF-09 | HU-06 | Registro y administración de emergencias |
| RF-10 | HU-07 | Identificación de zonas afectadas |
| RF-11 | HU-08 | Visualización de emergencias |
| RF-12 | HU-09 | Configuración de niveles de riesgo |
| RF-13 | HU-10 | Registro de características de riesgo |
| RF-14 | HU-11 | Determinación de prioridades de evacuación |
| RF-15 | HU-12 | Visualización de riesgos y prioridades |
| RF-16 | HU-13 | Selección de ubicación del evacuado |
| RF-17, RF-18 | HU-14 | Cálculo de rutas de evacuación |
| RF-19 | HU-15 | Visualización de rutas |
| RF-20 | HU-16 | Consulta de información de la ruta |

Cobertura por IDs: RF-01 a RF-20, sin IDs omitidos. Esta cobertura nominal no sustituye la validación contra la especificación completa de cada RF.

## Trazabilidad adicional a componentes y pantallas

Los nombres de capas y componentes siguen la vista de desarrollo: `Presentacion_Controller`, `Negocio_Service`, `Datos_Repository` y `Modelo_de_Dominio` (paquete de entidades). Las pantallas M01–M16 se implementan en prototipos HTML bajo `../mockups/html/`; para IDs con funciones relacionadas se pueden compartir pantallas o estados. Consulte en cada HU su componente y archivo HTML.

## Supuestos y contradicciones por resolver

- Los diagramas no contienen los textos completos de RF-01…RF-20; criterios de aceptación y reglas de negocio de las historias son una propuesta de validación, no una especificación aprobada.
- La vista de contexto y el modelo confirman ubicación seleccionada manualmente; no se contempla GPS, rastreo, sensores ni detección automática.
- Las entidades muestran estados de emergencia, espacio, disponibilidad y ruta; la semántica exacta y transiciones válidas requieren definición funcional.
- La entidad `GestorRiesgos` muestra operaciones de consulta/cálculo de prioridad, pero el UML no detalla fórmula ni entradas completas. No se define un algoritmo de priorización en esta entrega.
- El cálculo de rutas requiere infraestructura y estados de bloqueo según la solicitud, pero el UML no especifica algoritmo de búsqueda, desempate, unidades de distancia o fórmula de tiempo estimado. No se inventan esos valores.
- La vista lógica contiene enumeraciones de rol, tipo/gravedad/estado de emergencia, nivel y característica de riesgo, estado de espacio/disponibilidad/ruta y prioridad. La lista legible completa y reglas de transición deben confirmarse con los responsables.
- No se recibió una fuente aprobada con los 20 RF detallados. Antes de construir operaciones persistentes o reglas de decisión, incorporar la especificación validada.
