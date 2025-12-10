package upeu.edu.pe.curriculum.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.curriculum.domain.entities.Silabo;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilaboRepository implements PanacheRepositoryBase<Silabo, Long> {

    /**
     * Busca un sílabo por curso y año académico
     */
    public Optional<Silabo> findByCursoAndAnio(Long cursoId, String anioAcademico) {
        return find("curso.id = ?1 and anioAcademico = ?2 and active = true", 
                    cursoId, anioAcademico)
                .firstResultOptional();
    }

    /**
     * Busca el sílabo vigente de un curso
     */
    public List<Silabo> findVigenteByCurso(Long cursoId) {
        return find("curso.id = ?1 and estado = 'VIGENTE' and active = true", 
                    cursoId)
                .list();
    }

    /**
     * Obtiene todos los sílabos de un curso
     */
    public List<Silabo> findByCurso(Long cursoId) {
        return find("curso.id = ?1 and active = true order by anioAcademico desc, version desc", 
                    cursoId)
                .list();
    }

    /**
     * Obtiene todos los sílabos de un año académico
     */
    public List<Silabo> findByAnioAcademico(String anioAcademico) {
        return find("anioAcademico = ?1 and active = true", 
                    anioAcademico)
                .list();
    }

    /**
     * Obtiene sílabos por estado
     */
    public List<Silabo> findByEstado(String estado) {
        return find("estado = ?1 and active = true", 
                    estado)
                .list();
    }

    /**
     * Verifica si existe un sílabo para un curso en un año específico
     */
    public boolean existsByCursoAndAnio(Long cursoId, String anioAcademico) {
        return count("curso.id = ?1 and anioAcademico = ?2 and active = true", 
                     cursoId, anioAcademico) > 0;
    }

    /**
     * Obtiene sílabos que requieren aprobación (en revisión)
     */
    public List<Silabo> findPendientesAprobacion() {
        return find("estado = 'EN_REVISION' and active = true")
                .list();
    }

    /**
     * Obtiene sílabos aprobados de un año académico
     */
    public List<Silabo> findAprobadosByAnio(String anioAcademico) {
        return find("(estado = 'APROBADO' or estado = 'VIGENTE') and anioAcademico = ?1 and active = true", 
                    anioAcademico)
                .list();
    }

    /**
     * Obtiene la última versión de un sílabo para un curso
     */
    public Optional<Silabo> findUltimaVersion(Long cursoId) {
        return find("curso.id = ?1 and active = true order by anioAcademico desc, version desc", 
                    cursoId)
                .firstResultOptional();
    }

    /**
     * Cuenta sílabos por estado
     */
    public long countByEstado(String estado) {
        return count("estado = ?1 and active = true", 
                     estado);
    }
}

