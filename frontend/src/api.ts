import type { AuthSession, CaracteristicaRiesgo, Emergencia, InfraestructuraResumen, NivelRiesgo, PrioridadEspacio, Role, Ruta, Usuario } from './types';

const API_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api').replace(/\/$/, '');
const SESSION_KEY = 'saferoute.session';
const roles: Role[] = ['ADMINISTRADOR', 'RESPONSABLE_EMERGENCIAS', 'USUARIO_EVACUADO'];

export class ApiError extends Error {
  constructor(message: string, readonly status?: number, readonly kind: 'credentials'|'permission'|'session'|'network'|'server' = 'server') {
    super(message);
    this.name = 'ApiError';
  }
}

export function getSession(): AuthSession | null {
  try {
    const value: unknown = JSON.parse(sessionStorage.getItem(SESSION_KEY) ?? 'null');
    if (!value || typeof value !== 'object') return null;
    const candidate = value as Partial<AuthSession>;
    if (typeof candidate.id !== 'string' || typeof candidate.nombre !== 'string' ||
        typeof candidate.correo !== 'string' || typeof candidate.token !== 'string' ||
        !candidate.token || !roles.includes(candidate.rol as Role)) {
      sessionStorage.removeItem(SESSION_KEY);
      return null;
    }
    if (isJwtExpired(candidate.token)) {
      sessionStorage.removeItem(SESSION_KEY);
      window.dispatchEvent(new Event('saferoute:session-expired'));
      return null;
    }
    return candidate as AuthSession;
  } catch {
    sessionStorage.removeItem(SESSION_KEY);
    return null;
  }
}

function isJwtExpired(token: string): boolean {
  try {
    const payloadPart = token.split('.')[1];
    if (!payloadPart) return false;
    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '='))) as { exp?: unknown };
    return typeof payload.exp === 'number' && payload.exp * 1000 <= Date.now();
  } catch {
    return false;
  }
}

export function saveSession(session: AuthSession): void {
  if (!session.id || !session.nombre || !session.correo || !session.token || !roles.includes(session.rol)) {
    throw new ApiError('La API devolvió una sesión incompleta.', undefined, 'server');
  }
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession(): void {
  sessionStorage.removeItem(SESSION_KEY);
}

async function request<T>(path: string, init: RequestInit = {}, requireSession = true): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.body !== undefined && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (requireSession) {
    const session = getSession();
    if (session) headers.set('Authorization', `Bearer ${session.token}`);
  }

  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, { ...init, headers });
  } catch {
    throw new ApiError('No se pudo conectar con SafeRoute. Revisa que la API esté disponible.', undefined, 'network');
  }

  if (response.status === 401 && requireSession) {
    clearSession();
    window.dispatchEvent(new Event('saferoute:session-expired'));
  }

  const contentType = response.headers.get('content-type') ?? '';
  const payload: unknown = response.status === 204 ? null : contentType.includes('application/json')
    ? await response.json().catch(() => null)
    : await response.text().catch(() => '');

  if (!response.ok) {
    const backendError = payload && typeof payload === 'object' && 'error' in payload && typeof payload.error === 'string'
      ? payload.error
      : '';
    if (response.status === 401 && path === '/auth/login') {
      throw new ApiError('Correo o contraseña incorrectos.', 401, 'credentials');
    }
    if (response.status === 401) throw new ApiError('La sesión ya no es válida. Inicia sesión nuevamente.', 401, 'session');
    if (response.status === 403) throw new ApiError('Acceso denegado. Puede que tu sesión haya vencido o tu rol no tenga permiso para esta acción.', 403, 'permission');
    throw new ApiError(backendError || `La API respondió con el error ${response.status}.`, response.status, 'server');
  }
  return payload as T;
}

export const api = {
  login: (correo: string, contrasena: string) => request<AuthSession>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ correo, contrasena }),
  }, false),
  infrastructure: () => request<InfraestructuraResumen>('/infraestructura'),
  create: (path: string, body: unknown) => request<unknown>(path, { method: 'POST', body: JSON.stringify(body) }),
  patch: (path: string, body: unknown) => request<unknown>(path, { method: 'PATCH', body: JSON.stringify(body) }),
  update: (path: string, body: unknown) => request<unknown>(path, { method: 'PUT', body: JSON.stringify(body) }),
  emergencies: () => request<Emergencia[]>('/emergencias'),
  createEmergency: (body: { tipo: string; gravedad: string; descripcion: string; espaciosAfectadosIds: string[] }) => request<Emergencia>('/emergencias', { method: 'POST', body: JSON.stringify(body) }),
  updateEmergency: (id: string, body: { tipo: string; gravedad: string; descripcion: string }) => request<Emergencia>(`/emergencias/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  finishEmergency: (id: string) => request<Emergencia>(`/emergencias/${id}/finalizar`, { method: 'POST' }),
  updateAffectedSpaces: (id: string, espaciosAfectadosIds: string[]) => request<Emergencia>(`/emergencias/${id}/zonas-afectadas`, { method: 'PATCH', body: JSON.stringify({ espaciosAfectadosIds }) }),
  risks: () => request<CaracteristicaRiesgo[]>('/riesgos'),
  createRisk: (body: Omit<CaracteristicaRiesgo, 'id'>) => request<CaracteristicaRiesgo>('/riesgos', { method: 'POST', body: JSON.stringify(body) }),
  assignSpaceRiskLevel: (id: string, nivelRiesgo: NivelRiesgo) => request<unknown>(`/infraestructura/espacios/${id}/nivel-riesgo`, { method: 'PATCH', body: JSON.stringify({ nivelRiesgo }) }),
  riskLevels: () => request<NivelRiesgo[]>('/riesgos/niveles'),
  priorities: (id: string) => request<PrioridadEspacio[]>(`/riesgos/prioridades/${id}`),
  route: (origenEspacioId: string) => request<Ruta>('/rutas', { method: 'POST', body: JSON.stringify({ origenEspacioId }) }),
  blockConnection: (id: string, bloqueada: boolean) => request<unknown>(`/infraestructura/conexiones/${id}/bloqueo`, { method: 'PATCH', body: JSON.stringify({ bloqueada }) }),
  blockSpace: (id: string, bloqueado: boolean) => request<unknown>(`/infraestructura/espacios/${id}/bloqueo`, { method: 'PATCH', body: JSON.stringify({ bloqueado }) }),
  setExitAvailability: (id: string, estado: string) => request<unknown>(`/infraestructura/salidas/${id}/disponibilidad`, { method: 'PATCH', body: JSON.stringify({ estado }) }),
  setMeetingPointAvailability: (id: string, estado: string) => request<unknown>(`/infraestructura/puntos-encuentro/${id}/disponibilidad`, { method: 'PATCH', body: JSON.stringify({ estado }) }),
  users: () => request<Usuario[]>('/usuarios'),
  createUser: (body: { nombre: string; correo: string; contrasena: string; rol: string }) => request<Usuario>('/usuarios', { method: 'POST', body: JSON.stringify(body) }),
  changeRole: (id: string, rol: string) => request<Usuario>(`/usuarios/${id}/rol`, { method: 'PATCH', body: JSON.stringify({ rol }) }),
};
