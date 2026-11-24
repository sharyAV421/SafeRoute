# Despliegue de SafeRoute en Render y Vercel

No se incluye configuración de producción ni secretos en el repositorio. El backend usa Docker con Java 21; el frontend se publica como sitio estático Vite.

## Render: API

Crea un **Web Service** conectado al repositorio y selecciona:

- Runtime: Docker.
- Root Directory: `backend`.
- Dockerfile: `Dockerfile`.
- Health Check Path: `/actuator/health`.

El contenedor usa `PORT` si Render lo proporciona (Render lo asigna automáticamente); si no está disponible, conserva el puerto local configurado. El health check requiere que la aplicación y la conexión MongoDB estén saludables. Solo expone el estado general; no devuelve detalles ni componentes.

Configura en el entorno del servicio Render:

| Variable | Valor |
| --- | --- |
| `MONGODB_URI` | URI TLS de MongoDB Atlas para la base `saferoute`; introducirla directamente en el panel privado de Render. |
| `SAFEROUTE_JWT_SECRET` | Secreto aleatorio de al menos 32 bytes; guardarlo únicamente en el panel privado de Render. |
| `CORS_ORIGIN` | Origen HTTPS exacto del frontend de Vercel, por ejemplo `https://<proyecto>.vercel.app`, sin ruta final. |
| `SAFEROUTE_JWT_TTL_MINUTES` | Opcional; duración del JWT en minutos. Por defecto `20`. |

Render asigna `PORT`; no es necesario establecer `SERVER_PORT`. No configures `SAFEROUTE_BOOTSTRAP_ADMIN_PASSWORD`, `SAFEROUTE_ADMIN_RESET_PASSWORD` ni variables de reinicio salvo que se ejecute intencionalmente el procedimiento temporal documentado en la guía de implementación. No dejes esas variables configuradas tras inicializar/restablecer la cuenta. No copies ningún `.env` al servicio.

En MongoDB Atlas, permite la conexión de red desde el servicio Render según las opciones de conectividad y rangos de salida que ofrezca la cuenta. No abras el acceso a todas las IP salvo que se haya aprobado expresamente como una decisión de seguridad.

## Vercel: frontend

Importa el mismo repositorio como proyecto Vercel y configura:

- Root Directory: `frontend`.
- Framework Preset: Vite.
- Build Command: `npm run build`.
- Output Directory: `dist`.

Añade `VITE_API_URL` en los entornos de Vercel donde se vaya a compilar (Production y, si se requieren previews, Preview): `https://<servicio-render>.onrender.com/api`. Es una URL pública, no un secreto. Vite inserta este valor durante la compilación, así que vuelve a desplegar el frontend después de cambiarlo.

Después de conocer el dominio definitivo de Vercel, establece ese mismo origen exacto en `CORS_ORIGIN` en Render y reinicia/re-despliega la API. El backend admite un origen configurado por vez.

## Variables que quedan fuera de Git

No subas `MONGODB_URI`, `SAFEROUTE_JWT_SECRET`, contraseñas, tokens ni archivos `.env`. Los valores se agregan directamente en los paneles de las plataformas. El `.dockerignore` excluye los archivos de entorno del contexto de construcción del backend.

## Verificación manual tras configurar las plataformas

1. Confirma que Render termine el despliegue y que `GET https://<servicio-render>.onrender.com/actuator/health` responda HTTP 200 y solo muestre el estado general.
2. Verifica `GET https://<servicio-render>.onrender.com/api/riesgos/niveles` desde el navegador del frontend. Una respuesta sin bloqueo CORS confirma que el origen configurado coincide.
3. Inicia sesión desde Vercel y confirma una llamada protegida de acuerdo con el rol existente.
4. No expongas los valores del panel ni la respuesta JWT al revisar logs o capturas.

La conexión real con Atlas no puede validarse desde este entorno si `MONGODB_URI` no está disponible en el proceso local. Para verificarla tras configurar Render, usa el health check protegido por respuesta de estado (sin revelar secretos) o revisa el estado del servicio en Render.