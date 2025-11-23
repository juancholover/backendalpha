package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.PlanCurso;

import java.util.List;

@ApplicationScoped
public class PlanCursoRepository implements PanacheRepository<PlanCurso> {

    /**
     * Busca todos los cursos de un plan académico
     */
    public List<PlanCurso> findByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and active = true order by ciclo, curso.nombre", planAcademicoId);
    }

    /**
     * Busca todos los cursos de un plan académico por ciclo
     */
    public List<PlanCurso> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        return list("planAcademico.id = ?1 and ciclo = ?2 and active = true order by curso.nombre", 
                    planAcademicoId, ciclo);
    }

    /**
     * Busca todos los planes que contienen un curso específico
     */
    public List<PlanCurso> findByCurso(Long cursoId) {
        return list("curso.id = ?1 and active = true order by planAcademico.version desc", cursoId);
    }

    /**
     * Busca cursos obligatorios de un plan
     */
    public List<PlanCurso> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and esObligatorio = true and active = true order by ciclo", 
                    planAcademicoId);
    }

    /**
     * Busca cursos electivos de un plan
     */
    public List<PlanCurso> findElectivosByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and esObligatorio = false and active = true order by ciclo", 
                    planAcademicoId);
    }

    /**
     * Verifica si un curso ya existe en un plan académico
     */
    public boolean existsByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId) {
        return count("planAcademico.id = ?1 and curso.id = ?2 and active = true", 
                     planAcademicoId, cursoId) > 0;
    }

    /**
     * Busca un curso específico dentro de un plan académico
     */
    public PlanCurso findByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId) {
        return find("planAcademico.id = ?1 and curso.id = ?2 and active = true", 
                    planAcademicoId, cursoId).firstResult();
    }

    /**
     * Calcula el total de créditos obligatorios de un plan
     */
    public Integer sumCreditosObligatorios(Long planAcademicoId) {
        return find("select sum(pc.creditos) from PlanCurso pc " +
                   "where pc.planAcademico.id = ?1 and pc.esObligatorio = true and pc.active = true", 
                   planAcademicoId)
                .project(Integer.class)
                .firstResult();
    }

    /**
     * Calcula el total de créditos electivos de un plan
     */
    public Integer sumCreditosElectivos(Long planAcademicoId) {
        return find("select sum(pc.creditos) from PlanCurso pc " +
                   "where pc.planAcademico.id = ?1 and pc.esObligatorio = false and pc.active = true", 
                   planAcademicoId)
                .project(Integer.class)
                .firstResult();
    }
}
