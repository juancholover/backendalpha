package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Estudiante;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository {
    void persist(Estudiante estudiante);

    Optional<Estudiante> findByIdOptional(Long id);

    Optional<Estudiante> findByCodigoEstudiante(String codigoEstudiante);

    List<Estudiante> findAllActive();

    List<Estudiante> findByProgramaAcademico(Long programaAcademicoId);

    List<Estudiante> findByEstadoAcademico(String estadoAcademico);

    List<Estudiante> findEstudiantesActivos(Long programaAcademicoId);

    boolean existsByPersona(Long personaId);

    boolean existsByCodigoEstudiante(String codigoEstudiante);

    boolean existsByCodigoEstudianteAndIdNot(String codigoEstudiante, Long id);
}
