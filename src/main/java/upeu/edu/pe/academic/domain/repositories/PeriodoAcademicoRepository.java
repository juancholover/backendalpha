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
     * Busca todos los períodos activos
     */
    public List<PeriodoAcademico> findAllActive() {
        return find("active = true ORDER BY fechaInicio DESC").list();
    }

    /**
     * Busca el período actual
     */
    public Optional<PeriodoAcademico> findActual() {
        return find("esActual = true and active = true").firstResultOptional();
    }

    /**
     * Busca período por código
     */
    public Optional<PeriodoAcademico> findByCodigo(String codigoPeriodo) {
        return find("UPPER(codigoPeriodo) = UPPER(?1) and active = true", 
                   codigoPeriodo).firstResultOptional();
    }

    /**
     * Busca períodos por año
     */
    public List<PeriodoAcademico> findByAnio(Integer anio) {
        return find("anio = ?1 and active = true ORDER BY numeroPeriodo", 
                   anio).list();
    }

    /**
     * Busca períodos por estado
     */
    public List<PeriodoAcademico> findByEstado(String estado) {
        return find("UPPER(estado) = UPPER(?1) and active = true ORDER BY fechaInicio DESC", 
                   estado).list();
    }

    /**
     * Busca períodos en un rango de fechas
     */
    public List<PeriodoAcademico> findByFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return find("fechaInicio >= ?1 and fechaFin <= ?2 and active = true ORDER BY fechaInicio", 
                   fechaInicio, fechaFin).list();
    }

    /**
     * Verifica si existe un período con ese código
     */
    public boolean existsByCodigo(String codigoPeriodo) {
        return count("UPPER(codigoPeriodo) = UPPER(?1) and active = true", 
                    codigoPeriodo) > 0;
    }

    /**
     * Busca períodos activos (en curso o matrícula abierta)
     */
    public List<PeriodoAcademico> findActivos() {
        return find("estado IN ('EN_CURSO', 'MATRICULA_ABIERTA') and active = true ORDER BY fechaInicio DESC").list();
    }

    /**
     * Desmarca todos los períodos como actual
     */
    public void desmarcarTodosComoActual() {
        update("esActual = false");
    }
}
