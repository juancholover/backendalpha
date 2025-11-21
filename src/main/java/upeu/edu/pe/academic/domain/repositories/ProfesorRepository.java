package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Profesor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProfesorRepository implements PanacheRepositoryBase<Profesor, Long> {

    /**
     * Buscar profesor por persona
     */
    public Optional<Profesor> findByPersona(Long personaId) {
        return find("persona.id = ?1 and active = true", personaId).firstResultOptional();
    }

    /**
     * Listar profesores por grado académico
     */
    public List<Profesor> findByGradoAcademico(String gradoAcademico) {
        return find("gradoAcademico = ?1 and active = true", gradoAcademico).list();
    }

    /**
     * Listar profesores por categoría docente
     */
    public List<Profesor> findByCategoriaDocente(String categoriaDocente) {
        return find("categoriaDocente = ?1 and active = true", categoriaDocente).list();
    }

    /**
     * Listar profesores por condición docente
     */
    public List<Profesor> findByCondicionDocente(String condicionDocente) {
        return find("condicionDocente = ?1 and active = true", condicionDocente).list();
    }

    /**
     * Listar profesores por dedicación
     */
    public List<Profesor> findByDedicacion(String dedicacion) {
        return find("dedicacion = ?1 and active = true", dedicacion).list();
    }

    /**
     * Listar profesores por especialidad
     */
    public List<Profesor> findByEspecialidad(String especialidad) {
        return find("especialidad like ?1 and active = true", "%" + especialidad + "%").list();
    }

    /**
     * Listar profesores tiempo completo
     */
    public List<Profesor> findProfesoresTiempoCompleto() {
        return find("dedicacion = 'TIEMPO_COMPLETO' and active = true").list();
    }

    /**
     * Listar todos los profesores activos
     */
    public List<Profesor> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si una persona ya es profesor
     */
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }

    /**
     * Buscar profesor por código ORCID
     */
    public Optional<Profesor> findByCodigoOrcid(String codigoOrcid) {
        return find("codigoOrcid = ?1 and active = true", codigoOrcid).firstResultOptional();
    }
}
