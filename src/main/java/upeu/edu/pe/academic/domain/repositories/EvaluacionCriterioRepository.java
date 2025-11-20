package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.EvaluacionCriterio;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EvaluacionCriterioRepository implements PanacheRepository<EvaluacionCriterio> {

    /**
     * Busca criterios por sección
     */
    public List<EvaluacionCriterio> findBySeccion(Long seccionId) {
        return find("cursoOfertado.id = ?1 and active = true ORDER BY orden, id", seccionId).list();
    }

    /**
     * Busca criterios activos por sección
     */
    public List<EvaluacionCriterio> findActivosBySeccion(Long seccionId) {
        return find("cursoOfertado.id = ?1 and UPPER(estado) = 'ACTIVO' and active = true ORDER BY orden, id", 
                   seccionId).list();
    }

    /**
     * Busca un criterio específico por nombre y sección
     */
    public Optional<EvaluacionCriterio> findByNombreAndSeccion(String nombre, Long seccionId) {
        return find("UPPER(nombre) = UPPER(?1) and cursoOfertado.id = ?2 and active = true", 
                   nombre, seccionId).firstResultOptional();
    }

    /**
     * Busca criterios por tipo de evaluación
     */
    public List<EvaluacionCriterio> findByTipoAndSeccion(String tipoEvaluacion, Long seccionId) {
        return find("UPPER(tipoEvaluacion) = UPPER(?1) and cursoOfertado.id = ?2 and active = true ORDER BY orden", 
                   tipoEvaluacion, seccionId).list();
    }

    /**
     * Busca criterios recuperables de una sección
     */
    public List<EvaluacionCriterio> findRecuperablesBySeccion(Long seccionId) {
        return find("cursoOfertado.id = ?1 and esRecuperable = true and active = true ORDER BY orden", 
                   seccionId).list();
    }

    /**
     * Calcula el peso total de los criterios de una sección
     */
    public Integer sumPesoBySeccion(Long seccionId) {
        Object result = find("SELECT COALESCE(SUM(peso), 0) FROM EvaluacionCriterio WHERE cursoOfertado.id = ?1 and active = true", 
                            seccionId).project(Integer.class).firstResult();
        return result != null ? (Integer) result : 0;
    }

    /**
     * Verifica si la suma de pesos es válida (debe ser 100)
     */
    public boolean isPesoTotalValido(Long seccionId) {
        Integer total = sumPesoBySeccion(seccionId);
        return total != null && total == 100;
    }

    /**
     * Cuenta criterios de una sección
     */
    public long countBySeccion(Long seccionId) {
        return count("cursoOfertado.id = ?1 and active = true", seccionId);
    }

    /**
     * Busca el siguiente número de orden para una sección
     */
    public Integer getNextOrden(Long seccionId) {
        Object result = find("SELECT COALESCE(MAX(orden), 0) + 1 FROM EvaluacionCriterio WHERE cursoOfertado.id = ?1", 
                            seccionId).project(Integer.class).firstResult();
        return result != null ? (Integer) result : 1;
    }
}
