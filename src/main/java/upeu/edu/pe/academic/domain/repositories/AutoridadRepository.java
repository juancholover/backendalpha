package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Autoridad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AutoridadRepository implements PanacheRepository<Autoridad> {

    /**
     * Busca todas las autoridades activas de una universidad ordenadas por jerarquía
     */
    public List<Autoridad> findActivasByUniversidadId(Long universidadId) {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.universidadId = ?1 
            and a.activo = true
            order by ta.nivelJerarquia asc
            """, universidadId);
    }

    /**
     * Busca todas las autoridades de una universidad (activas e inactivas)
     */
    public List<Autoridad> findByUniversidadId(Long universidadId) {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.universidadId = ?1
            order by ta.nivelJerarquia asc, a.fechaInicio desc
            """, universidadId);
    }

    /**
     * Busca la autoridad actual de un tipo específico
     */
    public Optional<Autoridad> findActivaByTipoAutoridadId(Long tipoAutoridadId, Long universidadId) {
        return find("""
            select a from Autoridad a 
            join fetch a.persona p
            where a.tipoAutoridad.id = ?1 
            and a.universidadId = ?2
            and a.activo = true
            """, tipoAutoridadId, universidadId)
                .firstResultOptional();
    }

    /**
     * Busca el historial de autoridades de una persona
     */
    public List<Autoridad> findByPersonaId(Long personaId) {
        return list("""
            select a from Autoridad a 
            join fetch a.tipoAutoridad ta
            where a.persona.id = ?1
            order by a.fechaInicio desc
            """, personaId);
    }

    /**
     * Busca autoridades vigentes (activas y dentro del rango de fechas)
     */
    public List<Autoridad> findVigentesByUniversidadId(Long universidadId) {
        LocalDate hoy = LocalDate.now();
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.universidadId = ?1 
            and a.activo = true
            and (a.fechaInicio is null or a.fechaInicio <= ?2)
            and (a.fechaFin is null or a.fechaFin >= ?2)
            order by ta.nivelJerarquia asc
            """, universidadId, hoy);
    }

    /**
     * Verifica si existe una autoridad activa para un tipo específico
     */
    public boolean existsActivaByTipoAutoridadId(Long tipoAutoridadId, Long universidadId) {
        return count("""
            tipoAutoridad.id = ?1 
            and universidadId = ?2 
            and activo = true
            """, tipoAutoridadId, universidadId) > 0;
    }

    /**
     * Busca autoridades por rango de fechas
     */
    public List<Autoridad> findByFechaInicioRange(Long universidadId, LocalDate desde, LocalDate hasta) {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.universidadId = ?1 
            and a.fechaInicio between ?2 and ?3
            order by a.fechaInicio desc
            """, universidadId, desde, hasta);
    }
}
