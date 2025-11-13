package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Empleado;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EmpleadoRepository implements PanacheRepositoryBase<Empleado, Long> {

    /**
     * Buscar empleado por código
     */
    public Optional<Empleado> findByCodigoEmpleado(String codigoEmpleado) {
        return find("codigoEmpleado = ?1 and active = true", codigoEmpleado).firstResultOptional();
    }

    /**
     * Buscar empleado por persona
     */
    public Optional<Empleado> findByPersona(Long personaId) {
        return find("persona.id = ?1 and active = true", personaId).firstResultOptional();
    }

    /**
     * Listar empleados por unidad organizativa
     */
    public List<Empleado> findByUnidadOrganizativa(Long unidadOrganizativaId) {
        return find("unidadOrganizativa.id = ?1 and active = true", unidadOrganizativaId).list();
    }

    /**
     * Listar empleados por cargo
     */
    public List<Empleado> findByCargo(String cargo) {
        return find("cargo = ?1 and active = true", cargo).list();
    }

    /**
     * Listar empleados por tipo de contrato
     */
    public List<Empleado> findByTipoContrato(String tipoContrato) {
        return find("tipoContrato = ?1 and active = true", tipoContrato).list();
    }

    /**
     * Listar empleados por estado laboral
     */
    public List<Empleado> findByEstadoLaboral(String estadoLaboral) {
        return find("estadoLaboral = ?1 and active = true", estadoLaboral).list();
    }

    /**
     * Listar empleados activos
     */
    public List<Empleado> findEmpleadosActivos() {
        return find("estadoLaboral = 'ACTIVO' and active = true").list();
    }

    /**
     * Listar todos los empleados activos
     */
    public List<Empleado> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código de empleado
     */
    public boolean existsByCodigoEmpleado(String codigoEmpleado) {
        return count("codigoEmpleado = ?1 and active = true", codigoEmpleado) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoEmpleadoAndIdNot(String codigoEmpleado, Long id) {
        return count("codigoEmpleado = ?1 and id != ?2 and active = true", codigoEmpleado, id) > 0;
    }

    /**
     * Verificar si una persona ya es empleado
     */
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }
}
