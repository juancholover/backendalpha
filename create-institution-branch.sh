#!/bin/bash
cd "C:/Leroy/PROYECTOS ING. SISTEMAS/alpha/backendalpha"

echo "Verificando rama actual..."
git branch -v

echo ""
echo "Creando nueva rama 'institution'..."
git checkout -b institution

echo ""
echo "Agregando todos los cambios..."
git add -A

echo ""
echo "Mostrando archivos a ser commiteados..."
git status

echo ""
echo "Realizando commit..."
git commit -m "feat: Implementación de subida de imágenes para configuración de universidad

- Agregar soporte para actualizar imágenes de elementos de configuración (log_url, pantalla_principal)
- Implementar endpoint PUT para subir imágenes a Azure Blob Storage
- Actualizar campo configuracion en entidad Universidad para permitir actualizaciones
- Agregar anotación @JdbcTypeCode para conversión de String a JSONB
- Agregar transaccionalidad al endpoint de actualización
- Mejorar logs de depuración para rastrear flujo de actualización
- Limpiar respuesta del PUT para devolver solo el elemento actualizado"

echo ""
echo "Mostrando commit log..."
git log --oneline -5

echo ""
echo "✅ Rama 'institution' creada y cambios commiteados correctamente"
echo "Puedes hacer push con: git push -u origin institution"

