package upeu.edu.pe.curriculum.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.curriculum.domain.entities.SilaboHistorial;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad SilaboHistorial
 */
@ApplicationScoped
public class SilaboHistorialRepository implements PanacheRepositoryBase<SilaboHistorial, Long> {

    /**
     * Encuentra el historial de un sílabo ordenado por fecha descendente
     */
    public List<SilaboHistorial> findBySilabo(Long silaboId) {
        return find("silabo.id = ?1", Sort.descending("fecha"), silaboId).list();
    }

    /**
     * Encuentra el historial de un sílabo por acción
     */
    public List<SilaboHistorial> findBySilaboAndAccion(Long silaboId, String accion) {
        return find("silabo.id = ?1 and accion = ?2", 
                    Sort.descending("fecha"), 
                    silaboId, accion).list();
    }

    /**
     * Encuentra el historial de un usuario
     */
    public List<SilaboHistorial> findByUsuario(String usuario) {
        return find("usuario = ?1", Sort.descending("fecha"), usuario).list();
    }

    /**
     * Encuentra el historial en un rango de fechas
     */
    public List<SilaboHistorial> findByFechaRange(LocalDateTime desde, LocalDateTime hasta) {
        return find("fecha >= ?1 and fecha <= ?2", 
                    Sort.descending("fecha"), 
                    desde, hasta).list();
    }

    /**
     * Cuenta el número de cambios de un sílabo
     */
    public long countBySilabo(Long silaboId) {
        return count("silabo.id", silaboId);
    }

    /**
     * Encuentra el último cambio de un sílabo
     */
    public SilaboHistorial findUltimoByHistorial(Long silaboId) {
        return find("silabo.id = ?1", Sort.descending("fecha"), silaboId)
            .firstResult();
    }
}
