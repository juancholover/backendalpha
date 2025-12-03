package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.SilaboActividadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboActividadResponseDTO;
import upeu.edu.pe.academic.application.mapper.SilaboActividadMapper;
import upeu.edu.pe.academic.domain.commands.AgregarActividadUnidadCommand;
import upeu.edu.pe.academic.domain.entities.SilaboActividad;
import upeu.edu.pe.academic.domain.repositories.SilaboActividadRepository;
import upeu.edu.pe.academic.domain.usecases.AgregarActividadUnidadUseCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de Actividades de Sílabo
 * 
 * Responsabilidades:
 * - Orquestar casos de uso de SilaboActividad
 * - Coordinar conversiones DTO ↔ Entity
 * - Gestionar transacciones
 * - Exponer API simplificada a los Controllers
 */
@ApplicationScoped
public class SilaboActividadService {

    @Inject
    AgregarActividadUnidadUseCase agregarActividadUseCase;
    
    @Inject
    SilaboActividadRepository silaboActividadRepository;
    
    @Inject
    SilaboActividadMapper silaboActividadMapper;

    /**
     * Agregar nueva actividad a una unidad
     * 
     * Flujo:
     * 1. DTO → Command
     * 2. Use Case (validaciones + persistencia)
     * 3. Entity → ResponseDTO
     */
    @Transactional
    public SilaboActividadResponseDTO agregar(SilaboActividadRequestDTO dto, String usuarioCreador) {
        // Convertir DTO → Command
        AgregarActividadUnidadCommand command = new AgregarActividadUnidadCommand(
            dto.getUnidadId(),
            dto.getTipo(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getPonderacion(),
            dto.getSemanaProgramada(),
            dto.getInstrumentoEvaluacion(),
            dto.getIndicadores(),
            dto.getCriteriosEvaluacion()
        );
        
        // Ejecutar caso de uso
        SilaboActividad actividad = agregarActividadUseCase.execute(command, usuarioCreador);
        
        // Convertir Entity → ResponseDTO
        return silaboActividadMapper.toResponseDTO(actividad);
    }

    /**
     * Actualizar actividad existente
     */
    @Transactional
    public SilaboActividadResponseDTO actualizar(Long id, SilaboActividadRequestDTO dto, String usuarioModificador) {
        SilaboActividad actividad = silaboActividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + id));
        
        // Validar que el sílabo sea modificable
        if (!actividad.getUnidad().getSilabo().esModificable()) {
            throw new IllegalStateException(
                "No se puede modificar una actividad de un sílabo en estado " + 
                actividad.getUnidad().getSilabo().getEstado()
            );
        }
        
        // Actualizar campos usando el mapper
        silaboActividadMapper.updateEntityFromDto(dto, actividad);
        
        return silaboActividadMapper.toResponseDTO(actividad);
    }

    /**
     * Buscar actividad por ID
     */
    public SilaboActividadResponseDTO buscarPorId(Long id) {
        SilaboActividad actividad = silaboActividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + id));
        return silaboActividadMapper.toResponseDTO(actividad);
    }

    /**
     * Listar actividades de una unidad
     */
    public List<SilaboActividadResponseDTO> listarPorUnidad(Long unidadId) {
        return silaboActividadRepository.findByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar actividades de una unidad por tipo
     */
    public List<SilaboActividadResponseDTO> listarPorUnidadYTipo(Long unidadId, String tipo) {
        return silaboActividadRepository.findByUnidadAndTipo(unidadId, tipo).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar actividades sumativas de una unidad
     */
    public List<SilaboActividadResponseDTO> listarSumativasPorUnidad(Long unidadId) {
        return silaboActividadRepository.findSumativasByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar actividades formativas de una unidad
     */
    public List<SilaboActividadResponseDTO> listarFormativasPorUnidad(Long unidadId) {
        return silaboActividadRepository.findFormativasByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar actividades programadas para una semana específica
     */
    public List<SilaboActividadResponseDTO> buscarPorSemana(Long silaboId, Integer semana) {
        return silaboActividadRepository.findBySemana(silaboId, semana).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcular suma de ponderaciones de una unidad
     */
    public BigDecimal calcularPonderacionTotalUnidad(Long unidadId) {
        return silaboActividadRepository.sumPonderacionesByUnidad(unidadId);
    }

    /**
     * Calcular suma de ponderaciones de todo el sílabo
     */
    public BigDecimal calcularPonderacionTotalSilabo(Long silaboId) {
        return silaboActividadRepository.sumPonderacionesBySilabo(silaboId);
    }

    /**
     * Eliminar (lógicamente) una actividad
     * 
     * Solo permite eliminar si el sílabo está en estado modificable
     */
    @Transactional
    public void eliminar(Long id) {
        SilaboActividad actividad = silaboActividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada con ID: " + id));
        
        if (!actividad.getUnidad().getSilabo().esModificable()) {
            throw new IllegalStateException(
                "No se puede eliminar una actividad de un sílabo en estado " + 
                actividad.getUnidad().getSilabo().getEstado()
            );
        }
        
        actividad.setActive(false);
    }
}
