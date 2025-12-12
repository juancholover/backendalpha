package upeu.edu.pe.curriculum.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.curriculum.domain.entities.SilaboCalidad;

/**
 * Repositorio para la entidad SilaboCalidad
 */
@ApplicationScoped
public class SilaboCalidadRepository implements PanacheRepository<SilaboCalidad> {
    
    /**
     * Encuentra la última evaluación de calidad de un sílabo
     */
    public SilaboCalidad findLatestBySilabo(Long silaboId) {
        return find("silabo.id = ?1 ORDER BY fechaEvaluacion DESC", silaboId)
            .firstResult();
    }
    
    /**
     * Verifica si un sílabo tiene evaluación de calidad aprobada (>= 80%)
     */
    public boolean hasApprovedQuality(Long silaboId) {
        SilaboCalidad calidad = findLatestBySilabo(silaboId);
        return calidad != null && calidad.getPuntajeTotal() >= 80.0;
    }
}
