package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CursoOfertadoPanacheRepository implements CursoOfertadoRepository, PanacheRepository<CursoOfertado> {

    @Override
    public void persist(CursoOfertado cursoOfertado) {
        PanacheRepository.super.persist(cursoOfertado);
    }

    @Override
    public Optional<CursoOfertado> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<CursoOfertado> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    @Override
    public List<CursoOfertado> findByPeriodoAcademico(Long periodoId) {
        return find("periodoAcademico.id = ?1 and active = true", periodoId).list();
    }

    @Override
    public List<CursoOfertado> findByPlanAcademico(Long planId) {
        return find("planCurso.planAcademico.id = ?1 and active = true", planId).list();
    }

    @Override
    public List<CursoOfertado> findByPlanCurso(Long planCursoId) {
        return find("planCurso.id = ?1 and active = true", planCursoId).list();
    }

    @Override
    public List<CursoOfertado> findByCurso(Long cursoId) {
        return find("planCurso.curso.id = ?1 and active = true", cursoId).list();
    }

    @Override
    public List<CursoOfertado> findByProfesor(Long profesorId) {
        return find("profesor.id = ?1 and active = true", profesorId).list();
    }

    @Override
    public List<CursoOfertado> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId) {
        return find("periodoAcademico.id = ?1 and universidad.id = ?2 and estado = 'ABIERTA' and active = true",
                periodoId, universidadId).list();
    }

    @Override
    public List<CursoOfertado> findByModalidadAndPeriodo(String modalidad, Long periodoId) {
        return find("modalidad = ?1 and periodoAcademico.id = ?2 and active = true", modalidad, periodoId).list();
    }

    @Override
    public List<CursoOfertado> findConVacantesByPeriodo(Long periodoId) {
        return find("periodoAcademico.id = ?1 and vacantesDisponibles > 0 and estado = 'ABIERTA' and active = true",
                periodoId).list();
    }

    @Override
    public Optional<CursoOfertado> findByCodigoAndPeriodoAndUniversidad(String codigoSeccion, Long periodoId,
            Long universidadId) {
        return find("codigoSeccion = ?1 and periodoAcademico.id = ?2 and universidad.id = ?3 and active = true",
                codigoSeccion, periodoId, universidadId).firstResultOptional();
    }

    @Override
    public boolean existsByCodigoAndPeriodoAndPlanCurso(String codigoSeccion, Long periodoId, Long planCursoId) {
        return count("codigoSeccion = ?1 and periodoAcademico.id = ?2 and planCurso.id = ?3 and active = true",
                codigoSeccion, periodoId, planCursoId) > 0;
    }

    @Override
    public long countMatriculados(Long cursoOfertadoId) {
        // Assuming Matricula entity has a relationship to CursoOfertado named
        // 'cursoOfertado'
        return getEntityManager()
                .createQuery("SELECT count(m) FROM Matricula m WHERE m.cursoOfertado.id = :id AND m.active = true",
                        Long.class)
                .setParameter("id", cursoOfertadoId)
                .getSingleResult();
    }
}
