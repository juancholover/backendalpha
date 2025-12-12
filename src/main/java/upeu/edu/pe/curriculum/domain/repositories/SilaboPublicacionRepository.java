package upeu.edu.pe.curriculum.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.curriculum.domain.entities.SilaboPublicacion;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilaboPublicacionRepository implements PanacheRepositoryBase<SilaboPublicacion, Long> {

    /**
     * Busca publicaciones por plantilla.
     */
    public List<SilaboPublicacion> findByPlantilla(Long plantillaId) {
        return find("plantilla.id = ?1 and active = true order by fechaPublicacion desc", plantillaId)
                .list();
    }

    /**
     * Busca publicaciones por localización (campus).
     */
    public List<SilaboPublicacion> findByLocalizacion(Long localizacionId) {
        return find("localizacion.id = ?1 and active = true order by anioAcademico desc", localizacionId)
                .list();
    }

    /**
     * Busca publicación específica: plantilla + campus + año.
     */
    public Optional<SilaboPublicacion> findByPlantillaLocalizacionAnio(
            Long plantillaId, 
            Long localizacionId, 
            String anioAcademico) {
        return find("""
                plantilla.id = ?1 
                and localizacion.id = ?2 
                and anioAcademico = ?3 
                and active = true
                """, plantillaId, localizacionId, anioAcademico)
                .firstResultOptional();
    }

    /**
     * Lista publicaciones activas de un campus.
     */
    public List<SilaboPublicacion> findActivasByLocalizacion(Long localizacionId) {
        return find("""
                localizacion.id = ?1 
                and estado = 'ACTIVA' 
                and active = true 
                order by anioAcademico desc
                """, localizacionId)
                .list();
    }

    /**
     * Lista publicaciones por año académico.
     */
    public List<SilaboPublicacion> findByAnioAcademico(String anioAcademico) {
        return find("anioAcademico = ?1 and active = true order by createdAt desc", anioAcademico)
                .list();
    }

    /**
     * Lista publicaciones por estado.
     */
    public List<SilaboPublicacion> findByEstado(String estado) {
        return find("estado = ?1 and active = true order by createdAt desc", estado)
                .list();
    }

    /**
     * Busca publicaciones pendientes de adaptación.
     */
    public List<SilaboPublicacion> findPendientesAdaptacion(Long localizacionId) {
        return find("""
                localizacion.id = ?1 
                and estado = 'ACTIVA' 
                and adaptado = false 
                and active = true 
                order by fechaPublicacion asc
                """, localizacionId)
                .list();
    }

    /**
     * Busca publicaciones ya adaptadas por un campus.
     */
    public List<SilaboPublicacion> findAdaptadasByLocalizacion(Long localizacionId) {
        return find("""
                localizacion.id = ?1 
                and adaptado = true 
                and active = true 
                order by fechaAdaptacion desc
                """, localizacionId)
                .list();
    }

    /**
     * Cuenta publicaciones por estado.
     */
    public long countByEstado(String estado) {
        return count("estado = ?1 and active = true", estado);
    }

    /**
     * Cuenta publicaciones de una plantilla.
     */
    public long countByPlantilla(Long plantillaId) {
        return count("plantilla.id = ?1 and active = true", plantillaId);
    }

    /**
     * Verifica si existe publicación activa para plantilla + campus + año.
     */
    public boolean existsActivaByPlantillaLocalizacionAnio(
            Long plantillaId, 
            Long localizacionId, 
            String anioAcademico) {
        return count("""
                plantilla.id = ?1 
                and localizacion.id = ?2 
                and anioAcademico = ?3 
                and estado != 'CANCELADA'
                and active = true
                """, plantillaId, localizacionId, anioAcademico) > 0;
    }
}
