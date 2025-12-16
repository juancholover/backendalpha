-- =====================================================
-- DIAGNÓSTICO Y CORRECCIÓN DE CASBIN
-- =====================================================

DO $$
DECLARE
    total_politicas INTEGER;
    politicas_admin INTEGER;
    asignaciones_admin INTEGER;
    politicas_admin_ahora INTEGER;
    politicas_wildcard INTEGER;
    rec RECORD;
BEGIN
    RAISE NOTICE '=== DIAGNÓSTICO DE CASBIN ===';
    RAISE NOTICE '';

    SELECT COUNT(*) INTO total_politicas FROM casbin_rule WHERE ptype = 'p';
    SELECT COUNT(*) INTO politicas_admin FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN';
    SELECT COUNT(*) INTO asignaciones_admin FROM casbin_rule WHERE ptype = 'g' AND v0 = 'superadmin@upeu.edu.pe';

    RAISE NOTICE 'Total de políticas (p): %', total_politicas;
    RAISE NOTICE 'Políticas ADMIN: %', politicas_admin;
    RAISE NOTICE 'Asignaciones super admin: %', asignaciones_admin;

    RAISE NOTICE '';
    RAISE NOTICE '=== POLÍTICAS PARA ADMIN (primeras 20) ===';

    -- Mostrar todas las políticas para ADMIN
    FOR rec IN SELECT ptype, v0, v1, v2 FROM casbin_rule
               WHERE ptype = 'p' AND v0 = 'ADMIN'
               ORDER BY v1 LIMIT 20
    LOOP
        RAISE NOTICE 'Policy: % -> % (%)', rec.v0, rec.v1, rec.v2;
    END LOOP;

    RAISE NOTICE '';
    RAISE NOTICE '=== INSERTANDO POLÍTICAS FALTANTES ===';

    -- Insertar políticas específicas una por una para asegurar
    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/universidades', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/universidades', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/tipo-unidad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/tipo-unidad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/tipo-localizacion', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/tipo-localizacion', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/tipo-autoridad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/tipo-autoridad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/tipos-unidad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/tipos-unidad', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/unidades-organizativas', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/unidades-organizativas', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/autoridades', 'GET')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/autoridades', 'GET')
    ON CONFLICT DO NOTHING;

    -- Insertar wildcard más específico
    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '/api/v1/*', '*')
    ON CONFLICT DO NOTHING;

    INSERT INTO casbin_rule (ptype, v0, v1, v2)
    VALUES ('p', 'ADMIN', '//api/v1/*', '*')
    ON CONFLICT DO NOTHING;

    RAISE NOTICE '✅ Políticas insertadas';

    RAISE NOTICE '';
    RAISE NOTICE '=== VERIFICACIÓN FINAL ===';

    SELECT COUNT(*) INTO politicas_admin_ahora FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN';
    SELECT COUNT(*) INTO politicas_wildcard FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 LIKE '%*%';

    RAISE NOTICE 'Políticas ADMIN ahora: %', politicas_admin_ahora;
    RAISE NOTICE 'Políticas wildcard: %', politicas_wildcard;

    RAISE NOTICE '';
    RAISE NOTICE '=== ACCIONES REQUERIDAS ===';
    RAISE NOTICE '1. ✅ Script ejecutado en pgAdmin';
    RAISE NOTICE '2. ⏳ Ahora REINICIA el backend';
    RAISE NOTICE '3. ⏳ Espera 10 segundos a que Casbin recargue';
    RAISE NOTICE '4. ⏳ Intenta acceder a las APIs de nuevo';

END $$;

