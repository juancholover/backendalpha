package upeu.edu.pe.finance.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CuentaCorrienteAlumnoPanacheRepository
        implements CuentaCorrienteAlumnoRepository, PanacheRepository<CuentaCorrienteAlumno> {

    @Override
    public CuentaCorrienteAlumno save(CuentaCorrienteAlumno cuentaCorrienteAlumno) {
        persist(cuentaCorrienteAlumno);
        return cuentaCorrienteAlumno;
    }

    @Override
    public Optional<CuentaCorrienteAlumno> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<CuentaCorrienteAlumno> listAll() {
        return list("active = true ORDER BY fechaVencimiento");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(cta -> {
            cta.setActive(false);
            persist(cta);
        });
    }

    @Override
    public List<CuentaCorrienteAlumno> search(String query) {
        return list("active = true and (UPPER(concepto) like UPPER(?1) or UPPER(estudiante.nombre) like UPPER(?1))",
                "%" + query + "%");
    }

    @Override
    public List<CuentaCorrienteAlumno> findByEstudianteId(Long estudianteId) {
        return list("estudiante.id = ?1 and active = true", estudianteId);
    }
}
