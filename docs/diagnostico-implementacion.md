# SafeRoute — diagnóstico para iniciar implementación

**Estado:** revisión previa; no se modifica la documentación aprobada ni se implementan decisiones de negocio en este diagnóstico.  
**Fuentes revisadas:** 16 HU actuales, matriz HU–RF contenida en ellas, cinco imágenes UML originales adjuntas y prototipos HTML existentes.

## 1. Resumen ejecutivo

La dirección arquitectónica sí está suficientemente respaldada para preparar un esqueleto sin lógica funcional: frontend React + TypeScript, API REST Java/Spring Boot, Spring Data MongoDB, Atlas y tres capas `Presentacion_Controller`, `Negocio_Service`, `Datos_Repository`, con `Modelo_de_Dominio` como paquete de entidades. Los módulos se distribuyen en usuarios, infraestructura, emergencias, riesgos y rutas.

No se debe implementar todavía la lógica de prioridades, bloqueos, selección de destino, desempates o tiempo estimado. Los RF disponibles aquí son IDs y títulos/mapeos, sin texto normativo completo, y las vistas UML no contienen fórmulas ni reglas de transición. Implementar esos puntos requeriría decisiones nuevas.

## 2. Correspondencia entre las cinco vistas, HU, RF y mockups

| Vista | Evidencia observada | Correspondencia actual | Evaluación |
|---|---|---|---|
| Contexto | Administrador, Responsable de emergencias y Usuario evacuado; el responsable introduce emergencias y espacios afectados/bloqueados; el evacuado selecciona manualmente edificio/piso/espacio y consulta ruta, salida, punto de encuentro, distancia, cantidad de espacios y tiempo estimado. | HU-01–02, HU-06–08, HU-13–16; RF-01–20 por agrupación declarada; mockups de acceso, administración, emergencias y evacuación. | Alineación general buena. RF completos no están presentes para comprobar el significado detallado. |
| Funcional/componentes | Gestión de usuarios, infraestructura, emergencias, riesgos y cálculo/visualización de rutas; interfaces de autenticación, autorización, infraestructura, estado de emergencia, prioridades y solicitud de ruta. | Módulos indicados en matriz; paneles correspondientes en prototipos. | Alineación de módulos buena. No especifica algoritmos ni reglas de permisos. |
| Lógica/clases | Institución→Edificio→Piso→Espacio; Conexión, SalidaEmergencia, PuntoEncuentro, Usuario/Rol, Emergencia/ZonaAfectada, CaracteristicaRiesgo/NivelRiesgo, GestorRiesgos/Prioridad, RutaEvacuacion/PasosRuta. DTOs de ruta/solicitud. | HU-02–16 reflejan estas entidades y casos de uso. | Correspondencia conceptual buena. Atributos, enumeraciones y varias operaciones de la imagen son demasiado pequeños para validar carácter por carácter; no inferir campos no legibles. |
| Desarrollo | Paquetes `Frontend_React`, `Presentacion_Controller`, `Negocio_Service`, `Datos_Repository`, `Modelo_de_Dominio`; controladores/servicios/repositorios nombrados; `SecurityConfig`, `JwtFilter`, gestor global de errores, registro de auditoría. | Las HU vinculan componentes principales; prototipo cubre pantallas propuestas. | Alineación arquitectónica parcial. Las HU no trazan consistentemente todos los servicios/repositorios transversales y el prototipo no muestra sesiones/auditoría operativas. |
| Despliegue | Navegador con React + TypeScript; JVM con API REST Spring Boot/Spring Data MongoDB y frontend estático; MongoDB Atlas; HTTPS REST/JSON y conexión MongoDB TLS con restricción de IP en el dibujo. | README reconoce el stack y el alcance sin GPS. | Alineación tecnológica. No define proveedor/hosting, configuración de red, secretos ni valores de Atlas; estos no deben codificarse en el repositorio. |

### Trazabilidad nominal declarada

La matriz actual asigna todos los IDs RF-01…RF-20 a HU-01…HU-16 sin huecos ni RF repetidos: RF-01→HU-01, RF-02→HU-02, RF-03…05→HU-03, RF-06→HU-04, RF-07→HU-05, RF-08…09→HU-06, RF-10→HU-07, RF-11→HU-08, RF-12→HU-09, RF-13→HU-10, RF-14→HU-11, RF-15→HU-12, RF-16→HU-13, RF-17…18→HU-14, RF-19→HU-15, RF-20→HU-16. Esto comprueba únicamente la cobertura de IDs y los títulos entregados, no el cumplimiento semántico de los RF, porque faltan sus textos completos.

### Desajustes documentales/de interfaz identificados

1. **Prioridad:** `docs/mockups/html/app.js` presenta “P1/P2” y “prioridad demostrativa”; el UML muestra la entidad/enumeración `Prioridad`, pero no respalda esa escala ni valores. Son rótulos de ejemplo y no deben pasar al producto real sin aprobación.
2. **Configuración de niveles:** HU-09 y el mockup sugieren que Administrador configura/edita niveles. El UML confirma un enum `NivelRiesgo` (legible como BAJO, MEDIO, ALTO, CRITICO), no una operación de edición o configuración. RF-12 se denomina “Configuración de niveles de riesgo”, por lo que hay una discrepancia que debe aclararse: catálogo fijo frente a catálogo editable.
3. **Bloqueo:** el alcance pide excluir espacios y conexiones bloqueados; el contexto menciona selección de espacios afectados/bloqueados. Las HU/mockup alternan “afectado”, “bloqueado” y disponibilidad, pero el modelo separa ZonaAfectada y EstadoEspacio y también representa disponibilidad de Conexión. No está aprobada la equivalencia ni cómo se registra el bloqueo de conexiones.
4. **Riesgo y prioridad:** mockup muestra una característica asociada a nivel y prioridad P1/P2 por espacio. El diagrama sugiere relaciones/cálculo de prioridad con emergencia y espacio, pero no establece que esa asociación directa ni esos rótulos sean definitivos.
5. **Geometría del plano:** el UML define espacios y conexiones, pero no se ha verificado un modelo de coordenadas/contornos suficiente para producir un plano fiel de forma automática. Los mapas del prototipo son ilustrativos.
6. **RF:** la matriz actual la generó la solicitud inicial, no una especificación RF completa. Historias y mockups son útiles como borrador de flujo; sus criterios Gherkin siguen pendientes de validación contra los requisitos formales.

## 3. Reglas de negocio no definidas y propuestas para decisión

Las propuestas siguientes son alternativas de diseño. Ninguna está implementada ni se presenta como regla existente. Las recomendaciones indicadas son valores por defecto sugeridos para aprobación, no hechos inferidos del UML.

### A. Prioridad de evacuación (HU-11 / RF-14)

**Falta definir:** qué se prioriza (espacios, grupos o recorridos), entradas (gravedad, tipo de emergencia, nivel/características de riesgo, distancia/ocupación u otros), escala permitida por enum, fórmula, frecuencia de recálculo y respuesta ante datos faltantes/conflictivos.

**Propuesta conservadora:** calcular prioridad solo para espacios vinculados a una emergencia usando únicamente atributos explícitos y validados de Emergencia, ZonaAfectada, Espacio y CaracteristicaRiesgo. Mantener como salida los valores del enum `Prioridad` exactamente como se aprueben. No inferir ocupantes, discapacidad, proximidad al fuego ni otros datos que no aparecen en el modelo. Si faltan entradas requeridas, devolver “no determinable” como estado de resultado, sin asignar un nivel ficticio.

**Aprobación necesaria:** fórmula/tabla de decisión y relación entre niveles de riesgo y Prioridad; si la prioridad es por espacio, recorrido u otro elemento; catálogo exacto del enum; efecto de emergencia activa y dato faltante.

### B. Grafo y algoritmo de rutas (HU-14 / RF-17, RF-18)

**Falta definir:** si Conexion es dirigida o bidireccional; si puede cruzar pisos/edificios; ponderación del camino; objetivo (menor distancia, menor tiempo u otro); destino prioritario; si retornar una ruta o alternativas.

**Propuesta conservadora:** modelar la infraestructura como grafo ponderado: espacios como nodos y conexiones explícitas como aristas. No crear conexiones implícitas. Si Conexion contiene distancia validada y unidad común, usar Dijkstra para hallar el recorrido con menor suma de distancia. Dijkstra es adecuado si las ponderaciones son no negativas. Devolver una sola ruta y su destino; marcar ausencia si ningún destino válido es alcanzable. Esta recomendación necesita aprobación y depende de resolver los puntos siguientes.

### C. Exclusión de bloqueos

**Falta definir:** estados que significan cierre; quién puede cambiar cada estado; alcance temporal de la marca; si “afectado” bloquea; si el bloqueo de espacio invalida sus conexiones incidentes.

**Propuesta conservadora:** excluir un nodo si el responsable lo marca explícitamente como bloqueado/no disponible según un único enum aprobado; excluir una arista si la conexión está explícitamente bloqueada/no disponible. No inferir bloqueo desde nivel de riesgo, categoría de emergencia o estado “afectado”. Al bloquear un nodo, no hace falta persistir cambios derivados en aristas: el planificador omite tanto ese nodo como todas las aristas incidentes para esa solicitud. Persistir la acción manual y su emergencia asociada según la semántica acordada. No ofrecer salida que esté bloqueada/no disponible.

**Aprobación necesaria:** catálogo canónico de estados y semántica de ZonaAfectada; si el bloqueo se elimina al cambiar estado/cerrar emergencia; permisos del responsable.

### D. Desempate entre rutas

**Falta definir:** comportamiento cuando varias rutas tienen el mismo coste; orden de destinos; preferencia por menor número de cambios de piso u otros criterios.

**Propuesta conservadora:** desempatar determinísticamente por (1) menor distancia total; (2) menor número de conexiones; (3) identificadores estables lexicográficamente de secuencia de nodos/aristas. No añadir preferencias por escalera, accesibilidad, seguridad o congestión sin datos y regla explícitos. Si se aprueba Dijkstra de distancia mínima, el primer criterio ya queda determinado y los criterios 2–3 hacen reproducible el resultado.

### E. Tiempo estimado

**Falta definir:** velocidad de marcha, unidad de distancia, inclusión de escaleras/puertas, pausas, tamaño de grupo y redondeo.

**Propuesta conservadora:** reportar distancia en unidad configurada y tiempo como `distanciaTotal / velocidadBase`, con velocidad y unidad administrables/configurables por institución solo si se aprueba esa operación. Sin una velocidad validada, devolver tiempo “no disponible” en vez de inventar minutos. No incluir ocupación, congestión, movilidad individual ni duración de respuesta de emergencias pues no existen entradas en los diagramas.

### F. Otras decisiones funcionales para poder completar implementación

- Matriz de permisos por rol y política de autenticación/sesión. El diagrama de desarrollo sugiere JWT/SecurityConfig pero no especifica emisión, expiración, alta inicial de Administrador ni recuperación de credenciales.
- RF completos, campos obligatorios, unicidad, validaciones y operaciones CRUD para cada entidad.
- Enumeraciones y transiciones permitidas para emergencia, espacio, disponibilidad y ruta.
- Si RF-12 permite CRUD del catálogo NivelRiesgo o solo selección de enum fijo.
- Si salida y punto de encuentro se consideran destinos alternativos o se debe elegir punto asociado a una salida.
- Modelo de plano/coordenadas y cómo representar conexiones entre pisos para visualización fiel.
- Auditoría: eventos registrados, retención y quién puede consultar; el diagrama de desarrollo incluye RegistroAuditoría, pero las HU no lo describen como requisito.
- Contrato API y tratamiento de errores para ausencia de destino, infraestructura incompleta y datos obsoletos durante emergencia.
- Despliegue real: entorno destino, configuración CORS/HTTPS, IP permitidas en Atlas y gestión de secretos.

## 4. Estructura técnica propuesta (no creada)

Una vez aprobadas las reglas, el monorepo puede organizarse así, conservando las tres capas del profesor. `Modelo_de_Dominio` es un paquete de entidades dentro del backend, no una cuarta capa.

```text
SafeRoute/
├── frontend/                         # React + TypeScript
│   └── src/
│       ├── app/                      # rutas y composición por rol
│       ├── components/               # componentes de interfaz
│       ├── features/
│       │   ├── usuarios/
│       │   ├── infraestructura/
│       │   ├── emergencias/
│       │   ├── riesgos/
│       │   └── rutas/
│       ├── services/                 # cliente HTTP y contratos DTO
│       └── styles/
├── backend/                          # Spring Boot / Java
│   └── src/main/java/.../saferoute/
│       ├── Presentacion_Controller/  # controllers + DTOs
│       ├── Negocio_Service/          # casos de uso y reglas aprobadas
│       ├── Datos_Repository/         # interfaces Spring Data MongoDB
│       ├── Modelo_de_Dominio/        # entidades y enums UML
│       └── configuracion/            # seguridad, errores y auditoría UML
│   └── src/test/java/.../saferoute/  # pruebas de capas y reglas aprobadas
├── .env.example                      # solo nombres/valores ficticios
└── README.md
```

No se generó esta estructura porque la instrucción es presentar el diagnóstico antes de programar y las decisiones de negocio continúan pendientes. Los directorios `docs/` y los mockups existentes no se han modificado.

## 5. Aprobaciones solicitadas

Para avanzar a un esqueleto sin reglas funcionales basta aprobar la estructura propuesta. Para implementar evacuación, confirmar o corregir cada propuesta A–E (prioridad, algoritmo/objetivo de ruta, semántica de bloqueo, desempate y tiempo). También hace falta una fuente aprobada de RF-01…RF-20 completos y resolver la discrepancia sobre catálogo de niveles de riesgo. Hasta entonces, el diagnóstico no crea código ejecutable ni configura conexión a Atlas.
