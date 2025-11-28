package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RequisitoCursoPanacheRepository implements RequisitoCursoRepository, PanacheRepository<RequisitoCurso> {

    @Override
    public void persist(RequisitoCurso requisitoCurso) {
        PanacheRepository.super.persist(requisitoCurso);
    }

    @Override
    public Optional<RequisitoCurso> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<RequisitoCurso> findByCurso(Long cursoId) {
        return find("curso.id = ?1 and active = true", cursoId).list();
    }

    @Override
    public List<RequisitoCurso> findPrerequisitosByCurso(Long cursoId) {
        return find("curso.id = ?1 and UPPER(tipoRequisito) = 'PRERREQUISITO' and active = true",
                cursoId).list();
    }

    @Override
    public List<RequisitoCurso> findCorrequisitosByCurso(Long cursoId) {
        return find("curso.id = ?1 and UPPER(tipoRequisito) = 'CORREQUISITO' and active = true",
                cursoId).list();
    }

    @Override
    public List<RequisitoCurso> findCursosQueTienenComoRequisito(Long cursoRequisitoId) {
        return find("cursoRequisito.id = ?1 and active = true", cursoRequisitoId).list();
    }

    @Override
    public boolean existsRequisito(Long cursoId, Long cursoRequisitoId) {
        return count("curso.id = ?1 and cursoRequisito.id = ?2", cursoId, cursoRequisitoId) > 0;
    }

    @Override
    public Optional<RequisitoCurso> findByCursoAndRequisito(Long cursoId, Long cursoRequisitoId, String tipoRequisito) {
        return find("curso.id = ?1 and cursoRequisito.id = ?2 and UPPER(tipoRequisito) = UPPER(?3)",
                cursoId, cursoRequisitoId, tipoRequisito).firstResultOptional();
    }

    @Override
    public List<RequisitoCurso> findObligatoriosByCurso(Long cursoId) {
        return find("curso.id = ?1 and esObligatorio = true and active = true", cursoId).list();
    }

    @Override
    public long countByCurso(Long cursoId) {
        return count("curso.id = ?1 and active = true", cursoId);
    }

    @Override
    public List<RequisitoCurso> findAllRequisitosCascada(Long cursoId) {
        return find("SELECT DISTINCT rc FROM RequisitoCurso rc " +
                "WHERE rc.curso.id = ?1 " +
                "AND rc.active = true", cursoId).list();
    }
}
