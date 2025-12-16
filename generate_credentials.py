#!/usr/bin/env python3
"""
Generador de Credenciales - Sistema UPEU
==========================================

Genera hashes bcrypt para contraseñas de usuario.
Compatible con Spring Security & Quarkus.

Uso:
    python generate_credentials.py

Dependencias:
    pip install bcrypt
"""

import bcrypt
import sys
import json
from datetime import datetime, timedelta

def generate_bcrypt_hash(password, rounds=10):
    """
    Genera un hash bcrypt con el algoritmo correcto del sistema

    Formato: $2a$10$...
    Parámetros:
    - password: contraseña en texto plano
    - rounds: número de rondas (10 es estándar, seguridad estándar)
    """
    try:
        password_bytes = password.encode('utf-8')
        salt = bcrypt.gensalt(rounds=rounds)
        hashed = bcrypt.hashpw(password_bytes, salt)
        return hashed.decode('utf-8')
    except Exception as e:
        print(f"ERROR: {e}")
        return None

def generate_mock_permissions():
    """
    Genera la estructura de permisos mock para testing del frontend
    """
    return {
        "islas": [
            {
                "id": "isla_admin",
                "codigo": "ADMIN",
                "nombre": "Administrador",
                "descripcion": "Administración completa del sistema",
                "icono": "shield-check",
                "color": "#DC2626",
                "ruta_default": "/admin/dashboard",
                "es_isla_principal": True,
                "orden": 1
            }
        ],
        "permisos": {
            "ADMIN": {
                "contexto": "ADMIN",
                "modulos": [
                    {
                        "id": "mod_universidades",
                        "codigo": "UNIVERSIDADES",
                        "nombre": "Universidades",
                        "descripcion": "Gestión de universidades",
                        "icono": "building",
                        "orden": 1,
                        "recursos": [
                            {
                                "id": "res_ver_universidades",
                                "codigo": "VER_UNIVERSIDADES",
                                "nombre": "Ver Universidades",
                                "descripcion": "Listar universidades",
                                "ruta_frontend": "/admin/universidades",
                                "icono": "building",
                                "orden": 1,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/universidades",
                                        "metodo": "GET",
                                        "descripcion": "Listar universidades"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/universidades",
                                        "metodo": "POST",
                                        "descripcion": "Crear universidad"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/universidades/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar universidad"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/universidades/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar universidad"
                                    }
                                }
                            }
                        ]
                    },
                    {
                        "id": "mod_organizacion",
                        "codigo": "ORGANIZACION",
                        "nombre": "Organización",
                        "descripcion": "Estructura organizativa",
                        "icono": "sitemap",
                        "orden": 2,
                        "recursos": [
                            {
                                "id": "res_unidades_organizativas",
                                "codigo": "UNIDADES_ORGANIZATIVAS",
                                "nombre": "Unidades Organizativas",
                                "descripcion": "Gestión de unidades organizativas",
                                "ruta_frontend": "/admin/unidades-organizativas",
                                "icono": "sitemap",
                                "orden": 1,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/unidades-organizativas",
                                        "metodo": "GET",
                                        "descripcion": "Listar unidades"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/unidades-organizativas",
                                        "metodo": "POST",
                                        "descripcion": "Crear unidad"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/unidades-organizativas/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar unidad"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/unidades-organizativas/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar unidad"
                                    }
                                }
                            },
                            {
                                "id": "res_tipos_unidad",
                                "codigo": "TIPOS_UNIDAD",
                                "nombre": "Tipos de Unidad",
                                "descripcion": "Catálogo de tipos de unidad",
                                "ruta_frontend": "/admin/tipos-unidad",
                                "icono": "tags",
                                "orden": 2,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-unidad",
                                        "metodo": "GET",
                                        "descripcion": "Listar tipos"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-unidad",
                                        "metodo": "POST",
                                        "descripcion": "Crear tipo"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-unidad/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar tipo"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-unidad/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar tipo"
                                    }
                                }
                            }
                        ]
                    },
                    {
                        "id": "mod_autoridades",
                        "codigo": "AUTORIDADES",
                        "nombre": "Autoridades",
                        "descripcion": "Gestión de autoridades universitarias",
                        "icono": "user-tie",
                        "orden": 3,
                        "recursos": [
                            {
                                "id": "res_tipos_autoridad",
                                "codigo": "TIPOS_AUTORIDAD",
                                "nombre": "Tipos de Autoridad",
                                "descripcion": "Catálogo de tipos de autoridad",
                                "ruta_frontend": "/admin/tipos-autoridad",
                                "icono": "crown",
                                "orden": 1,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-autoridad",
                                        "metodo": "GET",
                                        "descripcion": "Listar tipos de autoridad"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-autoridad",
                                        "metodo": "POST",
                                        "descripcion": "Crear tipo de autoridad"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-autoridad/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar tipo de autoridad"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-autoridad/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar tipo de autoridad"
                                    }
                                }
                            },
                            {
                                "id": "res_autoridades",
                                "codigo": "AUTORIDADES",
                                "nombre": "Autoridades",
                                "descripcion": "Gestión de autoridades",
                                "ruta_frontend": "/admin/autoridades",
                                "icono": "user-tie",
                                "orden": 2,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/autoridades",
                                        "metodo": "GET",
                                        "descripcion": "Listar autoridades"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/autoridades",
                                        "metodo": "POST",
                                        "descripcion": "Crear autoridad"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/autoridades/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar autoridad"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/autoridades/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar autoridad"
                                    }
                                }
                            }
                        ]
                    },
                    {
                        "id": "mod_ubicacion",
                        "codigo": "UBICACION",
                        "nombre": "Ubicación",
                        "descripcion": "Gestión de ubicaciones",
                        "icono": "map-pin",
                        "orden": 4,
                        "recursos": [
                            {
                                "id": "res_tipos_localizacion",
                                "codigo": "TIPOS_LOCALIZACION",
                                "nombre": "Tipos de Localización",
                                "descripcion": "Catálogo de tipos de localización",
                                "ruta_frontend": "/admin/tipos-localizacion",
                                "icono": "map",
                                "orden": 1,
                                "acciones": {
                                    "read": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-localizacion",
                                        "metodo": "GET",
                                        "descripcion": "Listar tipos de localización"
                                    },
                                    "create": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-localizacion",
                                        "metodo": "POST",
                                        "descripcion": "Crear tipo de localización"
                                    },
                                    "update": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-localizacion/:id",
                                        "metodo": "PUT",
                                        "descripcion": "Actualizar tipo"
                                    },
                                    "delete": {
                                        "permitido": True,
                                        "endpoint": "/api/v1/tipo-localizacion/:id",
                                        "metodo": "DELETE",
                                        "descripcion": "Eliminar tipo"
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        },
        "metadata": {
            "total_islas": 1,
            "total_modulos": 4,
            "total_recursos": 5,
            "total_permisos_activos": 20,
            "isla_principal": "ADMIN"
        }
    }

def main():
    print("=" * 80)
    print("GENERADOR DE CREDENCIALES Y PERMISOS - SISTEMA UPEU")
    print("=" * 80)
    print()

    # Generar hash para super admin
    password = "SuperAdmin2025!"
    hash_value = generate_bcrypt_hash(password)

    if not hash_value:
        print("ERROR: No se pudo generar el hash bcrypt")
        print("Asegúrate de tener bcrypt instalado: pip install bcrypt")
        sys.exit(1)

    print("🔐 SUPER ADMINISTRADOR")
    print("-" * 80)
    print(f"Email:    superadmin@upeu.edu.pe")
    print(f"Password: {password}")
    print()
    print(f"Hash Bcrypt:")
    print(f"  {hash_value}")
    print()

    # Generar SQL de actualización
    print("📝 SQL PARA ACTUALIZAR EN pgAdmin:")
    print("-" * 80)
    print("-- 1. Actualizar hash de contraseña")
    print("UPDATE auth_usuario")
    print(f"SET password_hash = '{hash_value}'")
    print("WHERE id IN (")
    print("    SELECT au.id FROM auth_usuario au")
    print("    JOIN persona p ON au.persona_id = p.id")
    print("    WHERE p.email = 'superadmin@upeu.edu.pe'")
    print(");")
    print()
    print("-- 2. Agregar políticas específicas de administración")
    print("-- (Ejecutar las políticas adicionales del setup_superadmin.sql)")
    print()

    # Generar mock de permisos
    permissions = generate_mock_permissions()

    print("🎯 ESTRUCTURA DE PERMISOS MOCK (Para el Frontend)")
    print("-" * 80)
    print("Guarda esto como mock para el frontend hasta que el backend implemente la respuesta completa:")
    print()
    print("```javascript")
    print("// Mock response para testing del frontend")
    print("const mockLoginResponse = {")
    print("  success: true,")
    print("  data: {")
    print("    access_token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...',")
    print("    token_type: 'Bearer',")
    print("    expires_in: 3600,")
    print("    user: {")
    print("      id_persona: 1,")
    print("      email: 'superadmin@upeu.edu.pe',")
    print("      nombre: 'Super',")
    print("      apellidos: 'Administrador Sistema',")
    print("      nombre_completo: 'Super Administrador Sistema',")
    print("      roles_base: ['admin'],")
    print("      estado_cuenta: 'activa'")
    print("    },")
    print("    permissions:", json.dumps(permissions, indent=6))
    print("  }")
    print("};")
    print("```")
    print()

    # Información técnica
    print("ℹ️  INFORMACIÓN TÉCNICA")
    print("-" * 80)
    print(f"Algorithm: bcrypt")
    print(f"Version:   $2a$")
    print(f"Rounds:    10")
    print(f"Format:    $2a$10$[salt][hash]")
    print()

    # Plan de acción
    print("📋 PLAN DE ACCIÓN INMEDIATO")
    print("-" * 80)
    print("✅ FASE 1: Arreglar Casbin (COMPLETADO)")
    print("  - Hash bcrypt generado")
    print("  - Políticas de administración agregadas")
    print()
    print("⏳ FASE 2: Probar APIs básicas")
    print("  1. Actualiza el hash en pgAdmin (SQL de arriba)")
    print("  2. Ejecuta las nuevas políticas Casbin")
    print("  3. Reinicia el backend")
    print("  4. Testa estas APIs:")
    print("     - GET /api/v1/universidades")
    print("     - GET /api/v1/unidades-organizativas")
    print("     - GET /api/v1/tipo-unidad")
    print("     - GET /api/v1/tipo-autoridad")
    print("     - GET /api/v1/tipo-localizacion")
    print()
    print("⏳ FASE 3: Estructura de respuesta del login")
    print("  - Pedir al backend que implemente la respuesta completa")
    print("  - Usar el mock de arriba para el frontend mientras tanto")
    print()
    print("⏳ FASE 4: APIs de configuración de permisos")
    print("  - /api/v1/casbin/policies (gestionar políticas)")
    print("  - /api/v1/casbin/roles (asignar roles)")
    print("  - /api/v1/permisos/usuarios (configurar permisos individuales)")
    print()
    print("=" * 80)
    print()

    # Opción para generar hash de otro usuario
    print("💡 GENERAR HASH PARA OTRO USUARIO")
    print("-" * 80)
    response = input("¿Deseas generar un hash para otro usuario? (s/n): ").strip().lower()

    if response == 's':
        print()
        email = input("Email del usuario: ").strip()
        password = input("Contraseña: ").strip()

        if not email or not password:
            print("ERROR: Email y contraseña son requeridos")
            sys.exit(1)

        hash_value = generate_bcrypt_hash(password)
        if hash_value:
            print()
            print("🔐 NUEVO USUARIO")
            print("-" * 80)
            print(f"Email:    {email}")
            print(f"Password: {password}")
            print()
            print(f"Hash Bcrypt:")
            print(f"  {hash_value}")
            print()
            print("📝 SQL:")
            print("-" * 80)
            print("UPDATE auth_usuario")
            print(f"SET password_hash = '{hash_value}'")
            print("WHERE id IN (")
            print(f"    SELECT au.id FROM auth_usuario au")
            print(f"    JOIN persona p ON au.persona_id = p.id")
            print(f"    WHERE p.email = '{email}'")
            print(");")
            print()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\nOperación cancelada")
        sys.exit(0)
    except ImportError:
        print("ERROR: bcrypt module not installed")
        print()
        print("Instálalo con:")
        print("  pip install bcrypt")
        print()
        print("Luego ejecuta este script nuevamente")
        sys.exit(1)
    except Exception as e:
        print(f"ERROR INESPERADO: {e}")
        sys.exit(1)
