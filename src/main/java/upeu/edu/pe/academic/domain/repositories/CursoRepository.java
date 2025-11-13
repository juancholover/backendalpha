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
     * Listar cursos por plan académico
     */
    public List<Curso> findByPlanAcademico(Long planAcademicoId) {
        return find("planAcademico.id = ?1 and active = true", planAcademicoId).list();
    }

    /**
     * Listar cursos por ciclo
     */
    public List<Curso> findByCiclo(Long planAcademicoId, Integer ciclo) {
        return find("planAcademico.id = ?1 and ciclo = ?2 and active = true", planAcademicoId, ciclo).list();
    }

    /**
     * Listar cursos por tipo (OBLIGATORIO, ELECTIVO, LIBRE)
     */
    public List<Curso> findByTipoCurso(String tipoCurso) {
        return find("tipoCurso = ?1 and active = true", tipoCurso).list();
    }

    /**
     * Listar cursos por área curricular
     */
    public List<Curso> findByAreaCurricular(String areaCurricular) {
        return find("areaCurricular = ?1 and active = true", areaCurricular).list();
    }

    /**
     * Listar cursos sin prerequisitos (primer ciclo)
     */
    public List<Curso> findCursosSinPrerequisitos(Long planAcademicoId) {
        return find("planAcademico.id = ?1 and prerequisito is null and active = true", planAcademicoId).list();
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
