package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatriculaPanacheRepository implements PanacheRepositoryBase<Matricula, Long>, MatriculaRepository {

    @Override
    public void persist(Matricula matricula) {
        PanacheRepositoryBase.super.persist(matricula);
    }

    @Override
    public Optional<Matricula> findByIdOptional(Long id) {
        return PanacheRepositoryBase.super.findByIdOptional(id);
    }

    @Override
    public List<Matricula> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true", estudianteId).list();
    }

    @Override
    public List<Matricula> findByCursoOfertado(Long cursoOfertadoId) {
        return find("cursoOfertado.id = ?1 and active = true", cursoOfertadoId).list();
    }

    @Override
    public List<Matricula> findByPeriodo(Long periodoId) {
        return find("cursoOfertado.periodoAcademico.id = ?1 and active = true", periodoId).list();
    }

    @Override
    public Optional<Matricula> findByEstudianteAndCurso(Long estudianteId, Long cursoOfertadoId) {
        return find("estudiante.id = ?1 and cursoOfertado.id = ?2 and active = true", estudianteId, cursoOfertadoId)
                .firstResultOptional();
    }

    @Override
    public boolean existsByEstudianteAndCurso(Long estudianteId, Long cursoOfertadoId) {
        return count("estudiante.id = ?1 and cursoOfertado.id = ?2 and active = true", estudianteId,
                cursoOfertadoId) > 0;
    }

    @Override
    public List<Matricula> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public List<Matricula> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        return find("estudiante.id = ?1 and cursoOfertado.periodoAcademico.id = ?2 and active = true", estudianteId,
                periodoId).list();
    }

    @Override
    public List<Matricula> findByEstadoMatricula(String estadoMatricula) {
        return find("estadoMatricula = ?1 and active = true", estadoMatricula).list();
    }

    @Override
    public List<Matricula> findByPeriodoAndActive(Long periodoId) {
        return find("cursoOfertado.periodoAcademico.id = ?1 and active = true", periodoId).list();
    }

    @Override
    public List<Matricula> findByEstudianteAndAprobado(Long estudianteId) {
        return find("estudiante.id = ?1 and estadoAprobacion = 'APROBADO' and active = true", estudianteId).list();
    }

    @Override
    public List<Matricula> findByTipoAndPeriodo(String tipoMatricula, Long periodoId) {
        return find("tipoMatricula = ?1 and cursoOfertado.periodoAcademico.id = ?2 and active = true", tipoMatricula,
                periodoId).list();
    }
}
