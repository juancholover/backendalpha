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
     * Busca todas las autoridades activas ordenadas por jerarquía
     */
    public List<Autoridad> findActivas() {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.activo = true
            order by ta.nivelJerarquia asc
            """);
    }

    /**
     * Busca todas las autoridades (activas e inactivas)
     */
    public List<Autoridad> findAllWithDetails() {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            order by ta.nivelJerarquia asc, a.fechaInicio desc
            """);
    }

    /**
     * Busca la autoridad actual de un tipo específico
     */
    public Optional<Autoridad> findActivaByTipoAutoridadId(Long tipoAutoridadId) {
        return find("""
            select a from Autoridad a 
            join fetch a.persona p
            where a.tipoAutoridad.id = ?1 
            and a.activo = true
            """, tipoAutoridadId)
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
    public List<Autoridad> findVigentes() {
        LocalDate hoy = LocalDate.now();
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.activo = true
            and (a.fechaInicio is null or a.fechaInicio <= ?1)
            and (a.fechaFin is null or a.fechaFin >= ?1)
            order by ta.nivelJerarquia asc
            """, hoy);
    }

    /**
     * Verifica si existe una autoridad activa para un tipo específico
     */
    public boolean existsActivaByTipoAutoridadId(Long tipoAutoridadId) {
        return count("""
            tipoAutoridad.id = ?1 
            and activo = true
            """, tipoAutoridadId) > 0;
    }

    /**
     * Busca autoridades por rango de fechas
     */
    public List<Autoridad> findByFechaInicioRange(LocalDate desde, LocalDate hasta) {
        return list("""
            select a from Autoridad a 
            join fetch a.persona p
            join fetch a.tipoAutoridad ta
            where a.fechaInicio between ?1 and ?2
            order by a.fechaInicio desc
            """, desde, hasta);
    }
}
