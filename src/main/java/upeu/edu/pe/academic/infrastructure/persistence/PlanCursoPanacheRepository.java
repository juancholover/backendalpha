package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.repositories.PlanCursoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PlanCursoPanacheRepository implements PlanCursoRepository, PanacheRepository<PlanCurso> {

    @Override
    public void persist(PlanCurso planCurso) {
        PanacheRepository.super.persist(planCurso);
    }

    @Override
    public Optional<PlanCurso> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<PlanCurso> findByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and active = true order by ciclo, curso.nombre", planAcademicoId);
    }

    @Override
    public List<PlanCurso> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        return list("planAcademico.id = ?1 and ciclo = ?2 and active = true order by curso.nombre",
                planAcademicoId, ciclo);
    }

    @Override
    public List<PlanCurso> findByCurso(Long cursoId) {
        return list("curso.id = ?1 and active = true order by planAcademico.version desc", cursoId);
    }

    @Override
    public List<PlanCurso> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and esObligatorio = true and active = true order by ciclo",
                planAcademicoId);
    }

    @Override
    public List<PlanCurso> findElectivosByPlanAcademico(Long planAcademicoId) {
        return list("planAcademico.id = ?1 and esObligatorio = false and active = true order by ciclo",
                planAcademicoId);
    }

    @Override
    public boolean existsByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId) {
        return count("planAcademico.id = ?1 and curso.id = ?2 and active = true",
                planAcademicoId, cursoId) > 0;
    }

    @Override
    public Optional<PlanCurso> findByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId) {
        return find("planAcademico.id = ?1 and curso.id = ?2 and active = true",
                planAcademicoId, cursoId).firstResultOptional();
    }

    @Override
    public Integer sumCreditosObligatorios(Long planAcademicoId) {
        return find("select sum(pc.creditos) from PlanCurso pc " +
                "where pc.planAcademico.id = ?1 and pc.esObligatorio = true and pc.active = true",
                planAcademicoId)
                .project(Integer.class)
                .firstResult();
    }

    @Override
    public Integer sumCreditosElectivos(Long planAcademicoId) {
        return find("select sum(pc.creditos) from PlanCurso pc " +
                "where pc.planAcademico.id = ?1 and pc.esObligatorio = false and pc.active = true",
                planAcademicoId)
                .project(Integer.class)
                .firstResult();
    }
}
