package upeu.edu.pe.people.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;

@ApplicationScoped
public class TipoAutoridadDataInitializer {

    private static final Logger LOG = Logger.getLogger(TipoAutoridadDataInitializer.class);

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (tipoAutoridadRepository.count() == 0) {
            LOG.info("Seeding TipoAutoridad data...");

            // === NIVEL 0: Administradores del Sistema ===
            createTipo("SUPERADMIN", "Super Administrador", 0);
            createTipo("ADMIN", "Administrador", 0);

            // === NIVEL 1: Máximas Autoridades Universitarias ===
            createTipo("RECTOR", "Rector", 1);

            // === NIVEL 2: Vicerrectores ===
            createTipo("VICERRECTOR_ACADEMICO", "Vicerrector Académico", 2);
            createTipo("VICERRECTOR_INVESTIGACION", "Vicerrector de Investigación", 2);

            // === NIVEL 3: Decanos y Secretarios ===
            createTipo("DECANO", "Decano", 3);
            createTipo("SECRETARIO_GENERAL", "Secretario General", 3);

            // === NIVEL 4: Directores ===
            createTipo("DIRECTOR_ESCUELA", "Director de Escuela", 4);
            createTipo("DIRECTOR_POSTGRADO", "Director de Postgrado", 4);
            createTipo("DIRECTOR_INVESTIGACION", "Director de Investigación", 4);
            createTipo("DIRECTOR_ADMINISTRATIVO", "Director Administrativo", 4);

            // === NIVEL 5: Coordinadores y Jefes ===
            createTipo("COORDINADOR_ACADEMICO", "Coordinador Académico", 5);
            createTipo("JEFE_DEPARTAMENTO", "Jefe de Departamento", 5);

            LOG.info("TipoAutoridad seeded successfully.");
        }
    }

    private void createTipo(String codigo, String nombre, Integer jerarquia) {
        TipoAutoridad tipo = new TipoAutoridad();
        tipo.setCodigo(codigo);
        tipo.setNombre(nombre);
        tipo.setNivelJerarquia(jerarquia);
        tipo.setActive(true);
        tipoAutoridadRepository.persist(tipo);
    }
}
