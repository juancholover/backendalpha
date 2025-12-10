package upeu.edu.pe.finance.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.BecaOtorgada;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class BecaOtorgadaRepository implements PanacheRepository<BecaOtorgada> {

    /**
     * Busca todas las becas activas
     */
    public List<BecaOtorgada> findAllActive() {
        return find("active = true ORDER BY fechaOtorgamiento DESC").list();
    }

    /**
     * Busca becas por estudiante
     */
    public List<BecaOtorgada> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true ORDER BY fechaOtorgamiento DESC", 
                   estudianteId).list();
    }

    /**
     * Busca becas por tipo
     */
    public List<BecaOtorgada> findByTipo(String tipoBeca) {
        return find("UPPER(tipoBeca) = UPPER(?1) and active = true ORDER BY fechaOtorgamiento DESC", 
                   tipoBeca).list();
    }

    /**
     * Busca becas por estado
     */
    public List<BecaOtorgada> findByEstado(String estado) {
        return find("UPPER(estado) = UPPER(?1) and active = true ORDER BY fechaOtorgamiento DESC", 
                   estado).list();
    }

    /**
     * Busca becas por período académico
     */
    public List<BecaOtorgada> findByPeriodoAcademico(String periodoAcademico) {
        return find("periodoAcademico = ?1 and active = true ORDER BY fechaOtorgamiento DESC", 
                   periodoAcademico).list();
    }

    /**
     * Busca becas otorgadas en un rango de fechas
     */
    public List<BecaOtorgada> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin) {
        return find("fechaOtorgamiento BETWEEN ?1 AND ?2 and active = true ORDER BY fechaOtorgamiento DESC", 
                   fechaInicio, fechaFin).list();
    }

    /**
     * Cuenta becas activas por estudiante
     */
    public long countBecasActivasByEstudiante(Long estudianteId) {
        return count("estudiante.id = ?1 and UPPER(estado) = 'APLICADA' and active = true", 
                    estudianteId);
    }

    /**
     * Verifica si un estudiante tiene becas en un período
     */
    public boolean existeBecaEnPeriodo(Long estudianteId, String periodoAcademico) {
        return count("estudiante.id = ?1 and periodoAcademico = ?2 and active = true", 
                    estudianteId, periodoAcademico) > 0;
    }
}
