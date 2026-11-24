export type Role = 'ADMINISTRADOR' | 'RESPONSABLE_EMERGENCIAS' | 'USUARIO_EVACUADO';
export type NivelRiesgo = 'BAJO' | 'MEDIO' | 'ALTO' | 'CRITICO';
export type TipoEmergencia = 'INCENDIO'|'SISMO'|'FUGA_GAS'|'AMENAZA_SEGURIDAD'|'INUNDACION'|'FALLA_ELECTRICA'|'OTRA';
export type GravedadEmergencia = 'BAJA'|'MEDIA'|'ALTA'|'CRITICA';
export type EstadoEspacio = 'DISPONIBLE' | 'AFECTADO' | 'BLOQUEADO';
export type EstadoDisponibilidad = 'DISPONIBLE'|'NO_DISPONIBLE';
export type TipoCaracteristicaRiesgo = 'SUSTANCIAS_QUIMICAS'|'GAS'|'ELECTRICIDAD'|'MATERIALES_INFLAMABLES'|'RIESGO_BIOLOGICO'|'MAQUINARIA_EQUIPOS'|'RIESGO_ESTRUCTURAL'|'ALTA_OCUPACION'|'OTRO';

export interface Usuario { id: string; nombre: string; correo: string; rol: Role; }
export interface AuthSession extends Usuario { token: string; }
export interface Institucion { id: string; nombre: string; descripcion?: string; }
export interface Edificio { id: string; institucionId: string; nombre: string; descripcion?: string; }
export interface Piso { id: string; edificioId: string; nombre: string; nivel?: number; }
export interface Espacio { id: string; pisoId: string; nombre: string; tipo: string; capacidad: number; nivelRiesgo: NivelRiesgo; estado: EstadoEspacio; }
export interface Conexion { id: string; origenEspacioId: string; destinoEspacioId: string; distanciaMetros: number; bloqueada: boolean; }
export interface SalidaEmergencia { id: string; espacioId: string; nombre: string; ubicacion: string; estado: EstadoDisponibilidad; }
export interface PuntoEncuentro { id: string; espacioId: string; nombre: string; ubicacion: string; capacidad: number; estado: EstadoDisponibilidad; }
export interface Emergencia { id: string; tipo: TipoEmergencia; gravedad: GravedadEmergencia; descripcion: string; estado: string; fechaHora: string; zonasAfectadas: { id: string; espacioId: string; estado: EstadoEspacio; descripcion?: string }[]; }
export interface CaracteristicaRiesgo { id: string; espacioId: string; tipo: TipoCaracteristicaRiesgo; descripcion: string; }
export interface PrioridadEspacio { espacioId: string; prioridad: string; determinable: boolean; motivo?: string; }
export interface PasoRuta { espacioId: string; nombreEspacio: string; orden: number; distanciaAcumuladaMetros: number; }
export interface Ruta { id?: string; origenEspacioId: string; destinoEspacioId: string; destinoNombre: string; tipoDestino: 'SALIDA'|'PUNTO_ENCUENTRO'; distanciaTotal: number; cantidadEspacios: number; tiempoEstimado: string; pasos: PasoRuta[]; }

export interface InfraestructuraResumen { instituciones: Institucion[]; edificios: Edificio[]; pisos: Piso[]; espacios: Espacio[]; conexiones: Conexion[]; salidas: SalidaEmergencia[]; puntosEncuentro: PuntoEncuentro[]; }
