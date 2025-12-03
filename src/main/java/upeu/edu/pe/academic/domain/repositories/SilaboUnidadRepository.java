package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.SilaboUnidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilaboUnidadRepository implements PanacheRepositoryBase<SilaboUnidad, Long> {

    /**
     * Obtiene todas las unidades de un sílabo ordenadas por número
     */
    public List<SilaboUnidad> findBySilabo(Long silaboId) {
        return find("silabo.id = ?1 and active = true order by numeroUnidad", silaboId)
                .list();
    }

    /**
     * Busca una unidad específica de un sílabo por número
     */
    public Optional<SilaboUnidad> findBySilaboAndNumero(Long silaboId, Integer numeroUnidad) {
        return find("silabo.id = ?1 and numeroUnidad = ?2 and active = true", 
                    silaboId, numeroUnidad)
                .firstResultOptional();
    }

    /**
     * Obtiene unidades que incluyen una semana específica
     */
    public List<SilaboUnidad> findBySemana(Long silaboId, Integer semana) {
        return find("silabo.id = ?1 and semanaInicio <= ?2 and semanaFin >= ?2 and active = true", 
                    silaboId, semana)
                .list();
    }

    /**
     * Cuenta las unidades de un sílabo
     */
    public long countBySilabo(Long silaboId) {
        return count("silabo.id = ?1 and active = true", silaboId);
    }

    /**
     * Verifica si existe una unidad con el número dado en el sílabo
     */
    public boolean existsByNumero(Long silaboId, Integer numeroUnidad) {
        return count("silabo.id = ?1 and numeroUnidad = ?2 and active = true", 
                     silaboId, numeroUnidad) > 0;
    }

    /**
     * Verifica si existe una unidad con el número dado en el sílabo (alias)
     */
    public boolean existsByNumeroUnidad(Long silaboId, Integer numeroUnidad) {
        return existsByNumero(silaboId, numeroUnidad);
    }

    /**
     * Obtiene el último número de unidad de un sílabo
     */
    public Optional<Integer> findMaxNumeroUnidad(Long silaboId) {
        return find("silabo.id = ?1 and active = true order by numeroUnidad desc", silaboId)
                .firstResultOptional()
                .map(SilaboUnidad::getNumeroUnidad);
    }
}
