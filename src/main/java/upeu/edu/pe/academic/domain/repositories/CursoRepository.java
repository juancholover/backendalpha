package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Curso;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CursoRepository implements PanacheRepositoryBase<Curso, Long> {

    /**
     * Buscar curso por código
     */
    public Optional<Curso> findByCodigoCurso(String codigoCurso) {
        return find("codigoCurso = ?1 and active = true", codigoCurso).firstResultOptional();
    }

    /**
     * Listar cursos por universidad
     */
    public List<Curso> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Listar todos los cursos activos
     */
    public List<Curso> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código de curso
     */
    public boolean existsByCodigoCurso(String codigoCurso) {
        return count("codigoCurso = ?1 and active = true", codigoCurso) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoCursoAndIdNot(String codigoCurso, Long id) {
        return count("codigoCurso = ?1 and id != ?2 and active = true", codigoCurso, id) > 0;
    }
}
