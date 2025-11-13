package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Estudiante;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EstudianteRepository implements PanacheRepositoryBase<Estudiante, Long> {

    /**
     * Buscar estudiante por código
     */
    public Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante) {
        return find("codigoEstudiante = ?1 and active = true", codigoEstudiante).firstResultOptional();
    }

    /**
     * Buscar estudiante por persona
     */
    public Optional<Estudiante> findByPersona(Long personaId) {
        return find("persona.id = ?1 and active = true", personaId).firstResultOptional();
    }

    /**
     * Listar estudiantes por programa académico
     */
    public List<Estudiante> findByProgramaAcademico(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and active = true", programaAcademicoId).list();
    }

    /**
     * Listar estudiantes por estado académico
     */
    public List<Estudiante> findByEstadoAcademico(String estadoAcademico) {
        return find("estadoAcademico = ?1 and active = true", estadoAcademico).list();
    }

    /**
     * Listar estudiantes activos por programa
     */
    public List<Estudiante> findEstudiantesActivos(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and estadoAcademico = 'ACTIVO' and active = true", programaAcademicoId).list();
    }

    /**
     * Listar estudiantes por ciclo actual
     */
    public List<Estudiante> findByCicloActual(Long programaAcademicoId, Integer ciclo) {
        return find("programaAcademico.id = ?1 and cicloActual = ?2 and active = true", programaAcademicoId, ciclo).list();
    }

    /**
     * Listar todos los estudiantes activos
     */
    public List<Estudiante> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código de estudiante
     */
    public boolean existsByCodigoEstudiante(String codigoEstudiante) {
        return count("codigoEstudiante = ?1 and active = true", codigoEstudiante) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoEstudianteAndIdNot(String codigoEstudiante, Long id) {
        return count("codigoEstudiante = ?1 and id != ?2 and active = true", codigoEstudiante, id) > 0;
    }

    /**
     * Verificar si una persona ya es estudiante
     */
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }
}
