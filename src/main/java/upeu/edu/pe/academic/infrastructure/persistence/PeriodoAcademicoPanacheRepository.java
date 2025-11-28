package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.repositories.PeriodoAcademicoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PeriodoAcademicoPanacheRepository
        implements PanacheRepositoryBase<PeriodoAcademico, Long>, PeriodoAcademicoRepository {

    @Override
    public void persist(PeriodoAcademico periodoAcademico) {
        PanacheRepositoryBase.super.persist(periodoAcademico);
    }

    @Override
    public Optional<PeriodoAcademico> findByIdOptional(Long id) {
        return PanacheRepositoryBase.super.findByIdOptional(id);
    }

    @Override
    public Optional<PeriodoAcademico> findByCodigoPeriodo(String codigoPeriodo) {
        return find("codigoPeriodo = ?1 and active = true", codigoPeriodo).firstResultOptional();
    }

    @Override
    public Optional<PeriodoAcademico> findPeriodoActual(Long universidadId) {
        return find("universidad.id = ?1 and esActual = true and active = true", universidadId).firstResultOptional();
    }

    @Override
    public List<PeriodoAcademico> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public List<PeriodoAcademico> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    @Override
    public Optional<PeriodoAcademico> findByCodigoAndUniversidad(String codigo, Long universidadId) {
        return find("codigoPeriodo = ?1 and universidad.id = ?2 and active = true", codigo, universidadId)
                .firstResultOptional();
    }

    @Override
    public List<PeriodoAcademico> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        return find("anio = ?1 and universidad.id = ?2 and active = true", anio, universidadId).list();
    }

    @Override
    public List<PeriodoAcademico> findByEstadoAndUniversidad(String estado, Long universidadId) {
        return find("estado = ?1 and universidad.id = ?2 and active = true", estado, universidadId).list();
    }

    @Override
    public List<PeriodoAcademico> findActivosAndUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    @Override
    public List<PeriodoAcademico> findByAnio(Integer anio) {
        return find("anio = ?1 and active = true", anio).list();
    }

    @Override
    public List<PeriodoAcademico> findByEstado(String estado) {
        return find("estado = ?1 and active = true", estado).list();
    }

    @Override
    public boolean existsByCodigoPeriodo(String codigoPeriodo) {
        return count("codigoPeriodo = ?1 and active = true", codigoPeriodo) > 0;
    }

    @Override
    public boolean existsByCodigoPeriodoAndIdNot(String codigoPeriodo, Long id) {
        return count("codigoPeriodo = :codigo and id != :id and active = true",
                Parameters.with("codigo", codigoPeriodo).and("id", id)) > 0;
    }

    @Override
    public void desactivarOtrosPeriodosActuales(Long universidadId, Long periodoActualId) {
        update("esActual = false where universidad.id = ?1 and id != ?2", universidadId, periodoActualId);
    }
}
