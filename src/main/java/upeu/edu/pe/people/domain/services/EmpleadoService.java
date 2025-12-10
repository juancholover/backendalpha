package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.people.application.mapper.EmpleadoMapper;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de empleados.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (crear, actualizar, eliminar) se manejan en Use
 * Cases.
 */
@ApplicationScoped
public class EmpleadoService {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    EmpleadoMapper empleadoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Lista todos los empleados
     */
    public List<EmpleadoResponseDTO> findAll() {
        return empleadoRepository.listAll().stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo empleados activos
     */
    public List<EmpleadoResponseDTO> findAllActive() {
        return empleadoRepository.find("estadoLaboral", "ACTIVO").list().stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca empleado por ID
     */
    public EmpleadoResponseDTO findById(Long id) {
        Empleado empleado = empleadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));
        return empleadoMapper.toResponseDTO(empleado);
    }

    /**
     * Busca empleado por código
     */
    public EmpleadoResponseDTO findByCodigoEmpleado(String codigo) {
        Empleado empleado = empleadoRepository.findByCodigoEmpleado(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con código: " + codigo));
        return empleadoMapper.toResponseDTO(empleado);
    }

    /**
     * Busca empleado por persona
     */
    public EmpleadoResponseDTO findByPersona(Long personaId) {
        Empleado empleado = empleadoRepository.findByPersona(personaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Empleado no encontrado para persona ID: " + personaId));
        return empleadoMapper.toResponseDTO(empleado);
    }

    /**
     * Busca empleados por estado laboral
     */
    public List<EmpleadoResponseDTO> findByEstadoLaboral(String estado) {
        return empleadoRepository.find("estadoLaboral", estado).list().stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca empleados por tipo de contrato
     */
    public List<EmpleadoResponseDTO> findByTipoContrato(String tipo) {
        return empleadoRepository.find("tipoContrato", tipo).list().stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca empleados por unidad organizativa
     */
    public List<EmpleadoResponseDTO> findByUnidadOrganizativa(Long unidadId) {
        return empleadoRepository.findByUnidadOrganizativa(unidadId).stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Búsqueda general por nombre o código
     */
    public List<EmpleadoResponseDTO> search(String query) {
        return empleadoRepository.list(
                "LOWER(codigoEmpleado) LIKE LOWER(?1) OR " +
                        "LOWER(persona.nombres) LIKE LOWER(?1) OR " +
                        "LOWER(persona.apellidoPaterno) LIKE LOWER(?1) OR " +
                        "LOWER(persona.apellidoMaterno) LIKE LOWER(?1)",
                "%" + query + "%").stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public Empleado getEntityById(Long id) {
        return empleadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));
    }
}
