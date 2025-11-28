package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Curso;
import java.util.List;
import java.util.Optional;

public interface CursoRepository {
    void persist(Curso curso);

    Optional<Curso> findByIdOptional(Long id);

    List<Curso> findAllActive();

    Optional<Curso> findByCodigoCurso(String codigoCurso);

    List<Curso> findByUniversidad(Long universidadId);

    boolean existsByCodigoCurso(String codigoCurso);

    boolean existsByCodigoCursoAndIdNot(String codigoCurso, Long id);
}
