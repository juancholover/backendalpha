package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.EmpleadoRequestDTO;
import upeu.edu.pe.academic.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.academic.application.mapper.EmpleadoMapper;
import upeu.edu.pe.academic.domain.entities.Empleado;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.academic.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmpleadoService {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Inject
    EmpleadoMapper empleadoMapper;

    /**
     * Crea un nuevo empleado
     */
    @Transactional
    public EmpleadoResponseDTO create(EmpleadoRequestDTO dto) {
        // Validar que la persona existe
        Persona persona = personaRepository.findByIdOptional(dto.personaId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + dto.personaId()));

        // Validar que la persona no sea menor de edad
        if (persona.getFechaNacimiento() != null) {
            int edad = LocalDate.now().getYear() - persona.getFechaNacimiento().getYear();
            if (edad < 18) {
                throw new BusinessRuleException("El empleado debe ser mayor de edad (18 años o más)");
            }
        }

        // Validar que la persona no sea ya empleado
        if (empleadoRepository.existsByPersona(dto.personaId())) {
            throw new DuplicateResourceException("La persona con ID " + dto.personaId() + " ya es empleado");
        }

        // Validar código único
        if (empleadoRepository.existsByCodigoEmpleado(dto.codigoEmpleado())) {
            throw new DuplicateResourceException("Empleado", "codigoEmpleado", dto.codigoEmpleado());
        }

        // Validar unidad organizativa si se proporciona
        if (dto.unidadOrganizativaId() != null) {
            unidadOrganizativaRepository.findByIdOptional(dto.unidadOrganizativaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidad Organizativa no encontrada con ID: " + dto.unidadOrganizativaId()));
        }

        // Validar fechas
        if (dto.fechaCese() != null && dto.fechaIngreso().isAfter(dto.fechaCese())) {
            throw new BusinessRuleException("La fecha de cese no puede ser anterior a la fecha de ingreso");
        }

        Empleado empleado = empleadoMapper.toEntity(dto);
        empleado.setPersona(persona);
        
        if (dto.unidadOrganizativaId() != null) {
            UnidadOrganizativa unidad = new UnidadOrganizativa();
            unidad.setId(dto.unidadOrganizativaId());
            empleado.setUnidadOrganizativa(unidad);
        }

        empleadoRepository.persist(empleado);
        return empleadoMapper.toResponseDTO(empleado);
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado para persona ID: " + personaId));
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
            "%" + query + "%"
        ).stream()
                .map(empleadoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un empleado
     */
    @Transactional
    public EmpleadoResponseDTO update(Long id, EmpleadoRequestDTO dto) {
        Empleado empleado = empleadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));

        // Validar código único (excluyendo el empleado actual)
        if (!empleado.getCodigoEmpleado().equals(dto.codigoEmpleado()) && 
            empleadoRepository.existsByCodigoEmpleado(dto.codigoEmpleado())) {
            throw new DuplicateResourceException("Empleado", "codigoEmpleado", dto.codigoEmpleado());
        }

        // Validar unidad organizativa si se proporciona
        if (dto.unidadOrganizativaId() != null) {
            UnidadOrganizativa unidad = unidadOrganizativaRepository.findByIdOptional(dto.unidadOrganizativaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidad Organizativa no encontrada con ID: " + dto.unidadOrganizativaId()));
            empleado.setUnidadOrganizativa(unidad);
        }

        // Validar fechas
        if (dto.fechaCese() != null && dto.fechaIngreso().isAfter(dto.fechaCese())) {
            throw new BusinessRuleException("La fecha de cese no puede ser anterior a la fecha de ingreso");
        }

        empleadoMapper.updateEntityFromDto(dto, empleado);
        return empleadoMapper.toResponseDTO(empleado);
    }

    /**
     * Cambia el estado laboral de un empleado
     */
    @Transactional
    public EmpleadoResponseDTO cambiarEstadoLaboral(Long id, String nuevoEstado) {
        Empleado empleado = empleadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));

        // Validar estado
        if (!List.of("ACTIVO", "CESADO", "SUSPENDIDO", "LICENCIA").contains(nuevoEstado)) {
            throw new BusinessRuleException("Estado laboral inválido: " + nuevoEstado);
        }

        empleado.setEstadoLaboral(nuevoEstado);

        // Si es CESADO, registrar fecha de cese si no existe
        if ("CESADO".equals(nuevoEstado) && empleado.getFechaCese() == null) {
            empleado.setFechaCese(LocalDate.now());
        }

        return empleadoMapper.toResponseDTO(empleado);
    }

    /**
     * Elimina (lógicamente) un empleado
     */
    @Transactional
    public void delete(Long id) {
        Empleado empleado = empleadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));
        
        empleado.setActive(false);
        empleado.setEstadoLaboral("CESADO");
        if (empleado.getFechaCese() == null) {
            empleado.setFechaCese(LocalDate.now());
        }
    }
}                                                                                                                                                                                                                                                                                                                                                                                                               
