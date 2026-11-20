# SafeRoute — trazabilidad de implementación

Esta matriz es adicional; no reemplaza ni edita la matriz aprobada de `docs/historias-usuario/00-resumen.md`. Los RF recibidos son IDs y agrupaciones/títulos, no especificación completa. “Parcial” indica que existe una estructura o flujo asociado, pero falta detalle para demostrar conformidad completa.

| RF | HU | Componentes UML | Módulo/código | Estado |
|---|---|---|---|---|
| RF-01 | HU-01 | Gestión usuarios; Usuario; UsuarioService; UsuarioRepository | POST `/api/auth/login`, JWT | Parcial: alta inicial de Administrador por entorno; detalles RF pendientes |
| RF-02 | HU-02 | Gestión usuarios; Rol; UsuarioController/Service/Repository | alta, listado y cambio de rol | Parcial: edición de datos/desactivación no definidas por RF disponibles |
| RF-03–05 | HU-03 | Institucion, Edificio, Piso, Espacio; InfraestructuraController/Service | altas y consulta | Parcial: edición/desactivación y validaciones RF pendientes |
| RF-06 | HU-04 | Conexion; InfraestructuraController/Service/Repository | alta, bloqueo y desbloqueo explícitos de conexiones | Parcial: registro es direccional por origen→destino; bidireccionalidad pendiente de contrato RF |
| RF-07 | HU-05 | SalidaEmergencia, PuntoEncuentro | alta, consulta y disponibilidad DISPONIBLE/NO_DISPONIBLE | Parcial: transición posterior de disponibilidad pendiente |
| RF-08–09 | HU-06 | EmergenciaController/Service/Repository; Emergencia | registro manual, consulta y finalización | Parcial: transiciones permitidas y edición de emergencia pendiente |
| RF-10 | HU-07 | ZonaAfectada, Espacio, Emergencia | lista embebida de espacios afectados por emergencia | Parcial: ZonaAfectada está embebida, no es una colección Mongo independiente; el bloqueo permanece separado y explícito |
| RF-11 | HU-08 | Emergencia; panel responsable | consulta de emergencias | Parcial: contrato RF pendiente |
| RF-12 | HU-09 | NivelRiesgo enum fijo | GET `/api/riesgos/niveles` y asignación al riesgo | Catálogo fijo implementado según aprobación |
| RF-13 | HU-10 | CaracteristicaRiesgo; RiesgoService/Repository | alta, listado y consulta por espacio; tipos del enum UML | Parcial: contrato de captura de características pendiente |
| RF-14 | HU-11 | GestorRiesgos; Prioridad | devuelve `determinable=false` | Fórmula pendiente, intencionalmente sin cálculo |
| RF-15 | HU-12 | RiesgoController; pantalla de riesgos | consulta de características | Parcial: plano geométrico no respaldado por modelo confirmado |
| RF-16 | HU-13 | PantallaEvacuacion; Espacio | selector manual de espacio origen | Parcial: presentación de edificio/piso requiere definición de RF |
| RF-17–18 | HU-14 | RutaController/Service/Repository; RutaEvacuacion | Dijkstra, dirección origen→destino explícita, bloqueo explícito de espacio y conexión | Regla aprobada; bidireccionalidad/unidad por confirmar en RF completos. `Conexion.bloqueada` es el mínimo atributo para persistir el bloqueo aprobado, ausente como atributo en el dibujo UML |
| RF-19 | HU-15 | RutaDTO; PantallaEvacuacion | pasos de ruta como lista | Parcial: geometría cartográfica no modelada |
| RF-20 | HU-16 | RutaEvacuacion/PasosRuta | destino (salida/punto de encuentro), distancia, cantidad de espacios y tiempo `No disponible`; pasos con distancia acumulada | Parcial: la unidad de distancia debe confirmarse contra RF y convención institucional |

## Trazabilidad actor/capas

| Actor | Frontend | `Presentacion_Controller` | `Negocio_Service` | `Datos_Repository` / `Modelo_de_Dominio` |
|---|---|---|---|---|
| Administrador | usuarios, infraestructura, conexiones, destinos, riesgos | UsuarioController, InfraestructuraController, RiesgoController | UsuarioService, InfraestructuraService, RiesgoService | repositorios de usuario/infraestructura/riesgo y entidades respectivas |
| Responsable de emergencias | emergencias, riesgos, prioridades | EmergenciaController, RiesgoController, InfraestructuraController | EmergenciaService, PrioridadService, RiesgoService | EmergenciaRepository, EspacioRepository, CaracteristicaRiesgoRepository |
| Usuario evacuado | selección manual y resultado | RutaController | RutaService | RutaRepository, EspacioRepository, ConexionRepository y repositorios de destinos |

Las capas siguen la vista de desarrollo. `Modelo_de_Dominio` es el paquete de entidades, no una cuarta capa. No se incluye geometría de plano porque no hay atributos espaciales confirmados. No se implementan GPS, sensores, cámaras ni seguimiento.

## Límites de correspondencia UML

- La vista de desarrollo no asigna todas las acciones a roles en detalle; los permisos actuales son una configuración inicial pendiente de validación contra RF completos.
- El bloqueo explícito de una conexión se aprobó en la decisión de implementación, pero el atributo no se dibuja en `Conexion`; se agregó el booleano mínimo `bloqueada` para representarlo. No se deriva el bloqueo de una zona afectada.
- Los destinos de ruta se resuelven mediante el espacio asociado a una salida o punto de encuentro. El dominio dibujado muestra esas asociaciones, y el resultado incluye el tipo/nombre del destino para que se pueda identificar.
- No se implementaron planos con coordenadas: UML contiene relaciones entre espacios, no datos de geometría.
- Las características de riesgo usan únicamente los tipos enumerados en el dominio UML. Los niveles son catálogo fijo y se asignan al espacio, según la aprobación recibida.
- No se dispone en el repositorio de contratos detallados completos de RF aparte de los IDs, agrupaciones y HU existentes. Las capacidades etiquetadas “Parcial” no se consideran conformidad verificada.
