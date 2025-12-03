package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Silabo;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilaboRepository implements PanacheRepositoryBase<Silabo, Long> {

    /**
     * Busca un sílabo por curso y año académico
     */
    public Optional<Silabo> findByCursoAndAnio(Long cursoId, String anioAcademico, Long universidadId) {
        return find("curso.id = ?1 and anioAcademico = ?2 and universidad.id = ?3 and active = true", 
                    cursoId, anioAcademico, universidadId)
                .firstResultOptional();
    }

    /**
     * Busca el sílabo vigente de un curso
     */
    public List<Silabo> findVigenteByCurso(Long cursoId, Long universidadId) {
        return find("curso.id = ?1 and estado = 'VIGENTE' and universidad.id = ?2 and active = true", 
                    cursoId, universidadId)
                .list();
    }

    /**
     * Obtiene todos los sílabos de un curso
     */
    public List<Silabo> findByCurso(Long cursoId, Long universidadId) {
        return find("curso.id = ?1 and universidad.id = ?2 and active = true order by anioAcademico desc, version desc", 
                    cursoId, universidadId)
                .list();
    }

    /**
     * Obtiene todos los sílabos de un año académico
     */
    public List<Silabo> findByAnioAcademico(String anioAcademico, Long universidadId) {
        return find("anioAcademico = ?1 and universidad.id = ?2 and active = true", 
                    anioAcademico, universidadId)
                .list();
    }

    /**
     * Obtiene sílabos por estado
     */
    public List<Silabo> findByEstado(String estado, Long universidadId) {
        return find("estado = ?1 and universidad.id = ?2 and active = true", 
                    estado, universidadId)
                .list();
    }

    /**
     * Verifica si existe un sílabo para un curso en un año específico
     */
    public boolean existsByCursoAndAnio(Long cursoId, String anioAcademico, Long universidadId) {
        return count("curso.id = ?1 and anioAcademico = ?2 and universidad.id = ?3 and active = true", 
                     cursoId, anioAcademico, universidadId) > 0;
    }

    /**
     * Obtiene sílabos que requieren aprobación (en revisión)
     */
    public List<Silabo> findPendientesAprobacion(Long universidadId) {
        return find("estado = 'EN_REVISION' and universidad.id = ?1 and active = true", 
                    universidadId)
                .list();
    }

    /**
     * Obtiene sílabos aprobados de un año académico
     */
    public List<Silabo> findAprobadosByAnio(String anioAcademico, Long universidadId) {
        return find("(estado = 'APROBADO' or estado = 'VIGENTE') and anioAcademico = ?1 and universidad.id = ?2 and active = true", 
                    anioAcademico, universidadId)
                .list();
    }

    /**
     * Obtiene la última versión de un sílabo para un curso
     */
    public Optional<Silabo> findUltimaVersion(Long cursoId, Long universidadId) {
        return find("curso.id = ?1 and universidad.id = ?2 and active = true order by anioAcademico desc, version desc", 
                    cursoId, universidadId)
                .firstResultOptional();
    }

    /**
     * Cuenta sílabos por estado en una universidad
     */
    public long countByEstado(String estado, Long universidadId) {
        return count("estado = ?1 and universidad.id = ?2 and active = true", 
                     estado, universidadId);
    }
}
