import type { CaracteristicaRiesgo, Emergencia, InfraestructuraResumen, NivelRiesgo, PrioridadEspacio, Ruta, Usuario } from './types';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';
function credentials(): Record<string, string> {
  const raw = sessionStorage.getItem('saferoute.credentials');
  return raw ? { Authorization: `Bearer ${raw}` } : {};
}
async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json', ...credentials() };
  new Headers(init.headers).forEach((value, key) => { headers[key] = value; });
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers,
  });
  if (!response.ok) {
    const body = await response.json().catch(() => null) as { error?: string } | null;
    throw new Error(body?.error ?? (response.status === 401 || response.status === 403 ? 'Acceso no autorizado' : `Error de API (${response.status})`));
  }
  return response.status === 204 ? undefined as T : response.json() as Promise<T>;
}
export const api = {
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
  loginCheck: async (username: string, password: string) => {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ correo: username, contrasena: password }),
    });
    if (!response.ok) throw new Error('No fue posible iniciar sesión. Verifica las credenciales o el acceso a la API.');
    const session = await response.json() as Usuario & { token: string };
    sessionStorage.setItem('saferoute.credentials', session.token);
    sessionStorage.setItem('saferoute.role', session.rol);
    return session;
  },
};
