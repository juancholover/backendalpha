# 🚀 Configuración del Proyecto Backend Alpha

## Requisitos Previos

- **Java 21** (Microsoft Build of OpenJDK recomendado)
- **PostgreSQL 15+**
- **Gradle** (o usar el wrapper incluido `./gradlew`)

## 📋 Configuración Inicial

### 1. Clonar el Repositorio

```bash
git clone https://github.com/juancholover/backendalpha.git
cd backendalpha
```

### 2. Configurar Variables de Entorno

Copiar el archivo de ejemplo y configurar los valores:

```bash
cp .env.example .env
```

Editar `.env` con tus credenciales:

```properties
# Base de datos PostgreSQL
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/academicobd
QUARKUS_DATASOURCE_USERNAME=postgres
QUARKUS_DATASOURCE_PASSWORD=TuContraseña

# JWT Secret (mínimo 32 caracteres)
JWT_SECRET=TuClaveSecretaSuperSegura123456

# Azure Storage (opcional para desarrollo)
AZURE_STORAGE_CONNECTION_STRING=DefaultEndpointsProtocol=https;AccountName=...
AZURE_STORAGE_CONTAINER=institution-images
```

### 3. Crear la Base de Datos

```sql
CREATE DATABASE academicobd;
```

Las migraciones Flyway se ejecutarán automáticamente al iniciar.

### 4. Ejecutar el Proyecto

```bash
# Modo desarrollo (hot reload)
./gradlew quarkusDev

# O en Windows
gradlew.bat quarkusDev
```

## 🔧 Variables de Entorno Requeridas

| Variable | Descripción | Requerida |
|----------|-------------|-----------|
| `QUARKUS_DATASOURCE_JDBC_URL` | URL de conexión PostgreSQL | ✅ Sí |
| `QUARKUS_DATASOURCE_USERNAME` | Usuario de BD | ✅ Sí |
| `QUARKUS_DATASOURCE_PASSWORD` | Contraseña de BD | ✅ Sí |
| `JWT_SECRET` | Clave secreta para tokens JWT | ✅ Sí |
| `AZURE_STORAGE_CONNECTION_STRING` | Conexión a Azure Blob Storage | ⚠️ Solo para uploads |
| `AZURE_STORAGE_CONTAINER` | Nombre del contenedor | ⚠️ Solo para uploads |

## 📚 Documentación API

Una vez iniciado, acceder a:

- **Swagger UI**: http://localhost:8080/q/swagger-ui/
- **OpenAPI Spec**: http://localhost:8080/q/openapi

## 🔐 Credenciales de Prueba

Usuario superadmin por defecto:
- **Email**: `superadmin@upeu.edu.pe`
- **Password**: `Admin123!`

## ☁️ Azure Storage (Opcional)

Para habilitar la subida de imágenes:

1. Crear una cuenta de Azure Storage
2. Crear un contenedor llamado `institution-images`
3. Configurar el acceso público al contenedor como "Blob"
4. Copiar la cadena de conexión desde: **Azure Portal > Storage Account > Access Keys**

Si no se configura Azure Storage, las funcionalidades de upload mostrarán un error pero el resto del sistema funcionará normalmente.

## 🧪 Tests

```bash
./gradlew test
```

## 📂 Estructura del Proyecto

```
src/main/java/upeu/edu/pe/
├── core/                    # Dominio principal
│   ├── domain/             
│   │   ├── entities/       # Entidades JPA
│   │   ├── commands/       # Comandos CQRS
│   │   ├── services/       # Servicios de dominio
│   │   ├── usecases/       # Casos de uso
│   │   └── repositories/   # Repositorios
│   ├── application/        
│   │   ├── dto/            # DTOs
│   │   └── mapper/         # Mappers
│   └── infrastructure/     
│       └── rest/           # Controladores REST
├── auth/                    # Autenticación y autorización
├── people/                  # Gestión de personas
├── enrollment/              # Matrículas
└── shared/                  # Componentes compartidos
    └── infrastructure/
        └── storage/        # Azure Blob Storage
```
