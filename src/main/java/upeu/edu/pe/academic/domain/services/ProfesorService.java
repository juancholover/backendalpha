package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.ProfesorRequestDTO;
import upeu.edu.pe.academic.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.academic.application.mapper.ProfesorMapper;
import upeu.edu.pe.academic.domain.entities.Empleado;
import upeu.edu.pe.academic.domain.entities.Profesor;
import upeu.edu.pe.academic.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.academic.domain.repositories.ProfesorRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gestión de profesores
 * Reglas de negocio:
 * - Empleado debe existir y estar activo
 * - Un empleado solo puede ser profesor una vez
 * - Validación de grado académico según categoría
 * - Validación de dedicación según normativa
 */
@ApplicationScoped
public class ProfesorService {

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    ProfesorMapper profesorMapper;

    /**
     * Lista todos los profesores activos
     */
    public List<ProfesorResponseDTO> listarTodos() {
        return profesorRepository.findAllActive()
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca profesor por ID
     */
    public ProfesorResponseDTO buscarPorId(Long id) {
        Profesor profesor = profesorRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
        
        if (!profesor.getActive()) {
            throw new ResourceNotFoundException("Profesor no está activo");
        }
        
        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Busca profesor por empleado
     */
    public ProfesorResponseDTO buscarPorEmpleado(Long empleadoId) {
        Profesor profesor = profesorRepository.findByEmpleado(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No se encontró profesor para el empleado ID: " + empleadoId));
        
        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Lista profesores por grado académico
     */
    public List<ProfesorResponseDTO> listarPorGradoAcademico(String gradoAcademico) {
        return profesorRepository.findByGradoAcademico(gradoAcademico)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores por categoría docente
     */
    public List<ProfesorResponseDTO> listarPorCategoriaDocente(String categoriaDocente) {
        return profesorRepository.findByCategoriaDocente(categoriaDocente)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores por dedicación
     */
    public List<ProfesorResponseDTO> listarPorDedicacion(String dedicacion) {
        return profesorRepository.findByDedicacion(dedicacion)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores con código RENACYT
     */
    public List<ProfesorResponseDTO> listarConRenacyt() {
        return profesorRepository.findAllActive()
                .stream()
                .filter(p -> p.getCodigoRenacyt() != null && !p.getCodigoRenacyt().isEmpty())
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea nuevo profesor
     * Validaciones:
     * - Empleado existe y está activo
     * - Empleado no es profesor aún
     * - Grado académico según categoría
     */
    @Transactional
    public ProfesorResponseDTO crear(ProfesorRequestDTO dto) {
        // Validar que empleado existe y está activo
        Empleado empleado = empleadoRepository.findByIdOptional(dto.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Empleado no encontrado con ID: " + dto.getEmpleadoId()));
        
        if (!empleado.getActive()) {
            throw new BusinessRuleException("El empleado no está activo");
        }

        // Validar que empleado no es profesor ya
        if (profesorRepository.existsByEmpleado(dto.getEmpleadoId())) {
            throw new DuplicateResourceException(
                "El empleado con ID " + dto.getEmpleadoId() + " ya es profesor");
        }

        // Validar grado académico según categoría
        validarGradoAcademicoSegunCategoria(dto.getGradoAcademico(), dto.getCategoriaDocente());

        // Crear profesor
        Profesor profesor = profesorMapper.toEntity(dto);
        profesor.setEmpleado(empleado);

        profesorRepository.persist(profesor);
        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Actualiza profesor existente
     */
    @Transactional
    public ProfesorResponseDTO actualizar(Long id, ProfesorRequestDTO dto) {
        Profesor profesor = profesorRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        // Validar grado académico según categoría
        validarGradoAcademicoSegunCategoria(dto.getGradoAcademico(), dto.getCategoriaDocente());

        // Actualizar datos
        profesorMapper.updateEntityFromDTO(dto, profesor);
        
        // No se permite cambiar el empleado
        
        profesorRepository.persist(profesor);
        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Elimina profesor (lógicamente)
     */
    @Transactional
    public void eliminar(Long id) {
        Profesor profesor = profesorRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        profesor.setActive(false);
        profesorRepository.persist(profesor);
    }

    /**
     * Valida que el grado académico sea coherente con la categoría docente
     * Reglas:
     * - PRINCIPAL: requiere DOCTOR o MAGISTER
     * - ASOCIADO: requiere mínimo MAGISTER
     * - AUXILIAR: requiere mínimo LICENCIADO
     */
    private void validarGradoAcademicoSegunCategoria(String gradoAcademico, String categoriaDocente) {
        switch (categoriaDocente) {
            case "PRINCIPAL":
                if (!gradoAcademico.equals("DOCTOR") && !gradoAcademico.equals("MAGISTER")) {
                    throw new BusinessRuleException(
                        "La categoría PRINCIPAL requiere grado académico de DOCTOR o MAGISTER");
                }
                break;
            
            case "ASOCIADO":
                if (gradoAcademico.equals("BACHILLER") || gradoAcademico.equals("LICENCIADO")) {
                    throw new BusinessRuleException(
                        "La categoría ASOCIADO requiere grado académico de MAGISTER o DOCTOR");
                }
                break;
            
            case "AUXILIAR":
                if (gradoAcademico.equals("BACHILLER")) {
                    throw new BusinessRuleException(
                        "La categoría AUXILIAR requiere mínimo grado académico de LICENCIADO");
                }
                break;
            
            default:
                throw new BusinessRuleException("Categoría docente no válida: " + categoriaDocente);
        }
    }
}
