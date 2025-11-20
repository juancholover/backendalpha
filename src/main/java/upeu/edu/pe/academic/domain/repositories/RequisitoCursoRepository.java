package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RequisitoCursoRepository implements PanacheRepository<RequisitoCurso> {

    /**
     * Busca todos los requisitos de un curso
     */
    public List<RequisitoCurso> findByCurso(Long cursoId) {
        return find("curso.id = ?1 and active = true", cursoId).list();
    }

    /**
     * Busca prerrequisitos de un curso
     */
    public List<RequisitoCurso> findPrerequisitosByCurso(Long cursoId) {
        return find("curso.id = ?1 and UPPER(tipoRequisito) = 'PRERREQUISITO' and active = true", 
                   cursoId).list();
    }

    /**
     * Busca correquisitos de un curso
     */
    public List<RequisitoCurso> findCorrequisitosByCurso(Long cursoId) {
        return find("curso.id = ?1 and UPPER(tipoRequisito) = 'CORREQUISITO' and active = true", 
                   cursoId).list();
    }

    /**
     * Busca cursos que tienen como requisito a un curso específico
     */
    public List<RequisitoCurso> findCursosQueTienenComoRequisito(Long cursoRequisitoId) {
        return find("cursoRequisito.id = ?1 and active = true", cursoRequisitoId).list();
    }

    /**
     * Verifica si existe una relación de requisito entre dos cursos
     */
    public boolean existsRequisito(Long cursoId, Long cursoRequisitoId) {
        return count("curso.id = ?1 and cursoRequisito.id = ?2", cursoId, cursoRequisitoId) > 0;
    }

    /**
     * Busca un requisito específico
     */
    public Optional<RequisitoCurso> findByCursoAndRequisito(Long cursoId, Long cursoRequisitoId, String tipoRequisito) {
        return find("curso.id = ?1 and cursoRequisito.id = ?2 and UPPER(tipoRequisito) = UPPER(?3)", 
                   cursoId, cursoRequisitoId, tipoRequisito).firstResultOptional();
    }

    /**
     * Busca requisitos obligatorios de un curso
     */
    public List<RequisitoCurso> findObligatoriosByCurso(Long cursoId) {
        return find("curso.id = ?1 and esObligatorio = true and active = true", cursoId).list();
    }

    /**
     * Cuenta requisitos de un curso
     */
    public long countByCurso(Long cursoId) {
        return count("curso.id = ?1 and active = true", cursoId);
    }

    /**
     * Busca todos los requisitos (directo e indirectos) de un curso
     * Útil para detectar ciclos en dependencias
     */
    public List<RequisitoCurso> findAllRequisitosCascada(Long cursoId) {
        return find("SELECT DISTINCT rc FROM RequisitoCurso rc " +
                   "WHERE rc.curso.id = ?1 " +
                   "AND rc.active = true", cursoId).list();
    }
}
