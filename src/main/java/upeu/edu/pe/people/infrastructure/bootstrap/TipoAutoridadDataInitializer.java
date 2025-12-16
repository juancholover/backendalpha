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

            createTipo("DEC", "Decano", 1);
            createTipo("SG", "Secretario General", 2);
            createTipo("DE", "Director de Escuela", 3);
            createTipo("DP", "Director de Postgrado", 3);
            createTipo("DI", "Director de Investigación", 3);
            createTipo("DA", "Director Administrativo", 3);
            createTipo("CA", "Coordinador Académico", 4);
            createTipo("JD", "Jefe de Departamento", 4);

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
