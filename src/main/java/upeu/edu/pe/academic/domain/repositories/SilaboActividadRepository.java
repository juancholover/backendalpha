package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.SilaboActividad;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class SilaboActividadRepository implements PanacheRepositoryBase<SilaboActividad, Long> {

    /**
     * Obtiene todas las actividades de una unidad
     */
    public List<SilaboActividad> findByUnidad(Long unidadId) {
        return find("unidad.id = ?1 and active = true order by semanaProgramada, nombre", 
                    unidadId)
                .list();
    }

    /**
     * Obtiene actividades por tipo
     */
    public List<SilaboActividad> findByUnidadAndTipo(Long unidadId, String tipo) {
        return find("unidad.id = ?1 and tipo = ?2 and active = true", 
                    unidadId, tipo)
                .list();
    }

    /**
     * Obtiene actividades sumativas de una unidad
     */
    public List<SilaboActividad> findSumativasByUnidad(Long unidadId) {
        return findByUnidadAndTipo(unidadId, "SUMATIVA");
    }

    /**
     * Obtiene actividades formativas de una unidad
     */
    public List<SilaboActividad> findFormativasByUnidad(Long unidadId) {
        return findByUnidadAndTipo(unidadId, "FORMATIVA");
    }

    /**
     * Calcula la suma de ponderaciones de actividades sumativas de una unidad
     */
    public BigDecimal sumPonderacionesByUnidad(Long unidadId) {
        List<SilaboActividad> actividades = findSumativasByUnidad(unidadId);
        return actividades.stream()
                .map(SilaboActividad::getPonderacion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene actividades programadas para una semana específica
     */
    public List<SilaboActividad> findBySemana(Long unidadId, Integer semana) {
        return find("unidad.id = ?1 and semanaProgramada = ?2 and active = true", 
                    unidadId, semana)
                .list();
    }

    /**
     * Cuenta actividades de una unidad por tipo
     */
    public long countByUnidadAndTipo(Long unidadId, String tipo) {
        return count("unidad.id = ?1 and tipo = ?2 and active = true", 
                     unidadId, tipo);
    }

    /**
     * Obtiene todas las actividades sumativas de un sílabo
     */
    public List<SilaboActividad> findSumativasBySilabo(Long silaboId) {
        return find("unidad.silabo.id = ?1 and tipo = 'SUMATIVA' and active = true order by unidad.numeroUnidad, semanaProgramada", 
                    silaboId)
                .list();
    }

    /**
     * Calcula el total de ponderaciones sumativas de un sílabo
     */
    public BigDecimal sumPonderacionesBySilabo(Long silaboId) {
        List<SilaboActividad> actividades = findSumativasBySilabo(silaboId);
        return actividades.stream()
                .map(SilaboActividad::getPonderacion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
