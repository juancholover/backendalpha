# Integración de Seguridad Casbin - Guía para Frontend

Esta guía detalla los cambios en la API y la lógica necesaria en el Frontend para soportar el nuevo sistema de roles dinámicos (Casbin).

## 1. Cambio Fundamental: Multi-Roles

**Antes**: El usuario tenía un solo campo `rolNombre` (String).
**Ahora**: El sistema soporta **múltiples roles simultáneos** y aditivos. Un usuario puede ser `PROFESOR` y `DIRECTOR DE ESCUELA` al mismo tiempo, acumulando los permisos de ambos.

### Nueva Respuesta de Login

El endpoint de autenticación (`/auth/login`) ha cambiado su respuesta. El objeto `user` ahora incluye una lista de `roles`.

**Ejemplo de Respuesta JSON:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1Ni...",
  "refreshToken": "d8e35c...",
  "user": {
    "id": 15,
    "username": "juan.perez@upeu.edu.pe",
    "email": "juan.perez@upeu.edu.pe",
    "firstName": "Juan",
    "lastName": "Perez",
    "roles": [          <--- NUEVO CAMPO (Array de Strings)
      "EMPLEADO",
      "PROFESOR",
      "DEC"
    ],
    "status": "ACTIVE",
    "lastLogin": "2025-12-15T10:00:00"
  }
}
```

> **Nota**: El campo antiguo `role` (singular) podría haber sido eliminado o marcado como obsoleto. Usad `roles` (plural).

## 2. Gestión de Menús y Vistas

El Frontend debe adaptar su lógica de renderizado condicional. Ya no debéis hacer `if (user.role === 'ADMIN')`, sino verificar si el rol necesario **existe en la lista**.

### Lógica Recomendada (Pseudocódigo)

1. **Almacenar Roles**: Guardar el array `roles` en vuestro Global Store (Redux, Pinia, Context, etc.).

2. **Función de Verificación**:

```javascript
/**
 * Verifica si el usuario tiene al menos uno de los roles requeridos.
 * @param {string[]} requiredRoles - Roles que permiten ver el elemento.
 */
function hasPermission(requiredRoles) {
  const userRoles = store.user.roles; // ["PROFESOR", "DEC"]
  return userRoles.some(role => requiredRoles.includes(role));
}
```

3. **Uso en Renderizado**:

```javascript
// Menú "Gestión Académica" (Solo para Autoridades)
if (hasPermission(['DEC', 'SG', 'DE', 'CA'])) {
  renderMenuOption('Gestión Académica');
}

// Menú "Mis Cursos" (Solo Profesores)
if (hasPermission(['PROFESOR'])) {
  renderMenuOption('Mis Cursos');
}
```

## 3. Catálogo de Roles del Sistema

Estos son los Strings que recibiréis en el array `roles`. Vienen directamente de la base de datos y se asignan automáticamente.

| Rol (String) | Descripción | Acceso Principal |
|---|---|---|
| `ESTUDIANTE` | Alumno matriculado | Ver notas, matrícula, mi perfil. |
| `EMPLEADO` | Trabajador administrativo/docente | Perfil laboral básico. |
| `PROFESOR` | Docente con carga | Registro de notas, sílabos, asistencia. |
| `DEC` | Decano | Reportes de facultad, gestión de docentes. |
| `SG` | Secretario General | Gestión institucional, grados y títulos. |
| `DE` | Director de Escuela | Gestión de alumnos, horarios de escuela. |
| `DP` | Director de Postgrado | Gestión de programas de postgrado. |
| `DI` | Director de Investigación | Proyectos de investigación. |
| `DA` | Director Administrativo | Pagos, presupuestos. |
| `CA` | Coordinador Académico | Sílabos, seguimiento académico. |
| `JD` | Jefe de Departamento | Carga lectiva, profesores. |

## 4. Manejo de Errores (HTTP 403)

La seguridad final está en el Backend. Si un usuario intenta llamar a una API para la cual no tiene permiso (e.g., un Estudiante llamando a `/api/v1/presupuestos`), el servidor responderá:

- **Status Code**: `403 Forbidden`
- **Body**: Mensaje de error genérico o específico de Casbin.

**Acción Frontend**:
Vuestro interceptor de HTTP (Axios/Fetch interceptor) debe escuchar el error 403 y mostrar un `Toast` o `Alert` no intrusivo: *"No tienes permisos para realizar esta acción"*. No desloguear al usuario (a menos que sea 401).

## 5. Casos Especiales

### Primer Login
El campo `requiereCambioPassword` (si lo estáis usando) sigue activo. Si es `true`, redirigir obligatoriamente a pantalla de cambio de contraseña.

### Sincronización
Si un usuario dice "Soy Director pero no veo el menú", es posible que el rol no se haya sincronizado.
Un administrador puede llamar al endpoint `POST /api/v1/casbin/sync-all-users` para forzar la actualización de roles de todos los usuarios.
