# SafeRoute — instalación y ejecución

## Requisitos

- Node.js 20+ y npm.
- Java 21 y Maven 3.9+.
- MongoDB local para desarrollo o URI TLS de MongoDB Atlas.

## Configuración

1. Configura `MONGODB_URI`, `SAFEROUTE_JWT_SECRET` (secreto aleatorio de al menos 32 bytes), `CORS_ORIGIN` y opcionalmente `SERVER_PORT` en el entorno del backend.
2. Para la primera puesta en marcha, define temporalmente `SAFEROUTE_BOOTSTRAP_ADMIN_EMAIL` y `SAFEROUTE_BOOTSTRAP_ADMIN_PASSWORD` (mínimo 12 caracteres). La aplicación crea esa cuenta solo si el correo no existe. Retira ambas variables tras iniciar el backend correctamente.
3. Para restablecer una sola vez la contraseña de un administrador existente, define `SAFEROUTE_BOOTSTRAP_ADMIN_EMAIL` con el correo de la cuenta y `SAFEROUTE_ADMIN_RESET_PASSWORD` con la nueva contraseña (mínimo 12 caracteres). No configures `SAFEROUTE_BOOTSTRAP_ADMIN_PASSWORD` a la vez. Arranca el backend una vez y elimina `SAFEROUTE_ADMIN_RESET_PASSWORD` del entorno inmediatamente después. El restablecimiento falla si el correo no existe o no tiene rol `ADMINISTRADOR`; no crea usuarios. El hash usa el mismo `PasswordEncoder` de BCrypt utilizado por el login.
4. Copia `frontend/.env.example` a `frontend/.env.local` y establece `VITE_API_URL`. Este archivo local está excluido de Git.

Para ingresar la contraseña de restablecimiento en PowerShell sin mostrarla, usa una entrada oculta con `Read-Host -AsSecureString` y transfiérela en memoria a `SAFEROUTE_ADMIN_RESET_PASSWORD`; no la escribas en archivos ni Git. Después del primer arranque exitoso, elimina esa variable antes de cualquier reinicio.

Ejemplo de restablecimiento de una sola ejecución (detén primero cualquier instancia previa; `MONGODB_URI` y `SAFEROUTE_JWT_SECRET` deben estar configuradas en esa misma sesión):

```powershell
$env:SAFEROUTE_BOOTSTRAP_ADMIN_EMAIL = 'ejemplo1@gmail.com'
$secure = Read-Host 'Nueva contraseña del administrador' -AsSecureString
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:SAFEROUTE_ADMIN_RESET_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    $secure.Dispose()
}
cd backend
mvn spring-boot:run
```

Cuando Spring Boot indique que inició correctamente, detén esa ejecución con `Ctrl+C`, elimina las variables y arranca normalmente:

```powershell
Remove-Item Env:SAFEROUTE_ADMIN_RESET_PASSWORD
Remove-Item Env:SAFEROUTE_BOOTSTRAP_ADMIN_EMAIL
mvn spring-boot:run
```

Para probar sin mostrar la contraseña ni el JWT, solicita la contraseña con entrada oculta, llama `POST /api/auth/login`, y usa el token solo en memoria para `GET /api/usuarios`. Comprueba que la respuesta de login tenga `rol` igual a `ADMINISTRADOR` y que la consulta protegida responda `200`; no imprimas `$session` ni el token.

Spring Boot no lee `.env` automáticamente; establece variables en el entorno del proceso. El `.env.example` raíz documenta nombres y valores de ejemplo solamente.

## Ejecutar

Terminal backend:

```powershell
cd backend
mvn spring-boot:run
```

Terminal frontend:

```powershell
cd frontend
npm install
npm run dev
```

Abre `http://localhost:5173`. En producción, sirve la API por HTTPS y limita las IP de acceso a Atlas al servidor.

## Verificación

```powershell
cd backend
mvn test
```

```powershell
cd frontend
npm install
npm run build
```

Las pruebas de servicio del backend cubren exclusión de bloqueos, desempate determinista y prioridad no determinable mientras falte la fórmula. Para pruebas con persistencia se necesita una instancia MongoDB de prueba; no se crea información ficticia.

La prueba opcional de autenticación real con MongoDB se activa con `MONGODB_TEST_URI` apuntando a una base aislada. Crea y elimina un administrador temporal, valida la contraseña mediante el proveedor real de Spring Security y confirma el rol. Para probar la API tras el restablecimiento, `POST /api/auth/login` debe responder con rol `ADMINISTRADOR` y token; usa ese token en `GET /api/usuarios` para comprobar autenticación y autorización. No imprimas ni registres el token.

## Alcance implementado y pendientes

- La prioridad por espacio aún no entrega una clasificación; indica que la fórmula está pendiente.
- El tiempo estimado es `No disponible`.
- El cálculo sigue la dirección explícita origen→destino de cada conexión. La bidireccionalidad debe confirmarse; hasta entonces, una conexión de retorno se registra por separado. La distancia debe ser positiva. No se configura unidad por defecto; usar una unidad consistente y aprobarla para la institución.
- Una conexión almacena el atributo mínimo `bloqueada` porque el bloqueo explícito de conexiones fue aprobado como regla, aunque ese atributo no aparece en el dibujo de la clase `Conexion`. No se deduce bloqueo a partir de una zona afectada.
- Los pasos de ruta persisten el espacio y la distancia acumulada calculada. El destino contiene el espacio y si corresponde a una salida o a un punto de encuentro.
- El dominio UML no aporta geometría confirmada para un plano cartográfico; el resultado se presenta como lista de espacios.
- Estados de emergencia aceptan texto hasta recibir el contrato RF completo. Los espacios afectados no se bloquean automáticamente.
- La implementación actual es una base funcional incremental; no declara completos todos los RF. Consulta las filas marcadas como parciales en `trazabilidad.md`. Los permisos por actor, las transiciones de estados y las operaciones de edición que no aparecen especificadas siguen pendientes de validación.
- La integración completa con Atlas requiere configurar `MONGODB_URI` y verificar acceso de red/IP desde el entorno destino. Sin esa URI no se realizó una prueba de persistencia contra Atlas.
- Matriz de trazabilidad: `docs/implementacion/trazabilidad.md`.

## API inicial

- `POST /api/auth/login`: valida credenciales y emite JWT.
- `/api/usuarios`: alta, listado y asignación de rol para Administrador.
- `/api/infraestructura`: consulta y altas de institución, edificio, piso, espacio, conexión, salida y punto de encuentro.
- `PATCH /api/infraestructura/espacios/{id}/bloqueo`: marca un bloqueo explícito.
- `/api/emergencias`: registro manual, consulta y cambio de estado.
- `/api/riesgos`: características asociadas a espacios, catálogo fijo y consulta de prioridades no determinables.
- `POST /api/rutas` con `origenEspacioId`: ruta de menor distancia a un destino disponible.

La API exige JWT y aplica permisos por rol en las operaciones protegidas. La asignación inicial de permisos refleja los actores definidos, pero debe verificarse con los RF detallados antes de considerarse definitiva.

Los campos y estados se deben validar con los RF completos antes de tratar la API como contrato estable.
