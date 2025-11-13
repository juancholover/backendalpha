package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProgramaAcademicoRepository implements PanacheRepositoryBase<ProgramaAcademico, Long> {

    /**
     * Buscar programa por código
     */
    public Optional<ProgramaAcademico> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    /**
     * Listar programas por unidad organizativa
     */
    public List<ProgramaAcademico> findByUnidadOrganizativa(Long unidadOrganizativaId) {
        return find("unidadOrganizativa.id = ?1 and active = true", unidadOrganizativaId).list();
    }

    /**
     * Listar programas por nivel académico
     */
    public List<ProgramaAcademico> findByNivelAcademico(String nivelAcademico) {
        return find("nivelAcademico = ?1 and active = true", nivelAcademico).list();
    }

    /**
     * Listar programas por modalidad
     */
    public List<ProgramaAcademico> findByModalidad(String modalidad) {
        return find("modalidad = ?1 and active = true", modalidad).list();
    }

    /**
     * Listar programas activos (estado = ACTIVO)
     */
    public List<ProgramaAcademico> findProgramasActivos() {
        return find("estado = 'ACTIVO' and active = true").list();
    }

    /**
     * Listar todos los programas activos
     */
    public List<ProgramaAcademico> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código
     */
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return count("codigo = ?1 and id != ?2 and active = true", codigo, id) > 0;
    }
}
