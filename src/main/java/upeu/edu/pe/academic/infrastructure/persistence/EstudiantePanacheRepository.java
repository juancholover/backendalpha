package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EstudiantePanacheRepository implements PanacheRepositoryBase<Estudiante, Long>, EstudianteRepository {

    @Override
    public void persist(Estudiante estudiante) {
        PanacheRepositoryBase.super.persist(estudiante);
    }

    @Override
    public Optional<Estudiante> findByIdOptional(Long id) {
        return PanacheRepositoryBase.super.findByIdOptional(id);
    }

    @Override
    public Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante) {
        return find("codigoEstudiante = ?1 and active = true", codigoEstudiante).firstResultOptional();
    }

    @Override
    public List<Estudiante> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public List<Estudiante> findByProgramaAcademico(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and active = true", programaAcademicoId).list();
    }

    @Override
    public List<Estudiante> findByEstadoAcademico(String estadoAcademico) {
        return find("estadoAcademico = ?1 and active = true", estadoAcademico).list();
    }

    @Override
    public List<Estudiante> findEstudiantesActivos(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and estadoAcademico = 'ACTIVO' and active = true", programaAcademicoId)
                .list();
    }

    @Override
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }

    @Override
    public boolean existsByCodigoEstudiante(String codigoEstudiante) {
        return count("codigoEstudiante = ?1 and active = true", codigoEstudiante) > 0;
    }

    @Override
    public boolean existsByCodigoEstudianteAndIdNot(String codigoEstudiante, Long id) {
        return count("codigoEstudiante = :codigo and id != :id and active = true",
                Parameters.with("codigo", codigoEstudiante).and("id", id)) > 0;
    }
}
