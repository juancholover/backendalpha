package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PeriodoAcademicoRepository implements PanacheRepository<PeriodoAcademico> {

    /**
     * Busca períodos por universidad
     */
    public List<PeriodoAcademico> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true ORDER BY fechaInicio DESC", 
                   universidadId).list();
    }

    /**
     * Busca el período actual de una universidad
     */
    public Optional<PeriodoAcademico> findActualByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and esActual = true and active = true", 
                   universidadId).firstResultOptional();
    }

    /**
     * Busca período por código y universidad
     */
    public Optional<PeriodoAcademico> findByCodigoAndUniversidad(String codigoPeriodo, Long universidadId) {
        return find("UPPER(codigoPeriodo) = UPPER(?1) and universidad.id = ?2 and active = true", 
                   codigoPeriodo, universidadId).firstResultOptional();
    }

    /**
     * Busca períodos por año y universidad
     */
    public List<PeriodoAcademico> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        return find("anio = ?1 and universidad.id = ?2 and active = true ORDER BY numeroPeriodo", 
                   anio, universidadId).list();
    }

    /**
     * Busca períodos por estado
     */
    public List<PeriodoAcademico> findByEstadoAndUniversidad(String estado, Long universidadId) {
        return find("UPPER(estado) = UPPER(?1) and universidad.id = ?2 and active = true ORDER BY fechaInicio DESC", 
                   estado, universidadId).list();
    }

    /**
     * Busca períodos en un rango de fechas
     */
    public List<PeriodoAcademico> findByFechasAndUniversidad(LocalDate fechaInicio, LocalDate fechaFin, Long universidadId) {
        return find("universidad.id = ?1 and fechaInicio >= ?2 and fechaFin <= ?3 and active = true ORDER BY fechaInicio", 
                   universidadId, fechaInicio, fechaFin).list();
    }

    /**
     * Verifica si existe un período con ese código en la universidad
     */
    public boolean existsByCodigoAndUniversidad(String codigoPeriodo, Long universidadId) {
        return count("UPPER(codigoPeriodo) = UPPER(?1) and universidad.id = ?2", 
                    codigoPeriodo, universidadId) > 0;
    }

    /**
     * Busca períodos activos (en curso o matrícula abierta)
     */
    public List<PeriodoAcademico> findActivosAndUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and estado IN ('EN_CURSO', 'MATRICULA_ABIERTA') and active = true ORDER BY fechaInicio DESC", 
                   universidadId).list();
    }

    /**
     * Desmarca todos los períodos como actual en una universidad
     */
    public void desmarcarTodosComoActual(Long universidadId) {
        update("esActual = false WHERE universidad.id = ?1", universidadId);
    }
}
