package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.SilaboUnidadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboUnidadResponseDTO;
import upeu.edu.pe.academic.application.mapper.SilaboUnidadMapper;
import upeu.edu.pe.academic.domain.commands.AgregarUnidadSilaboCommand;
import upeu.edu.pe.academic.domain.entities.SilaboUnidad;
import upeu.edu.pe.academic.domain.repositories.SilaboUnidadRepository;
import upeu.edu.pe.academic.domain.usecases.AgregarUnidadSilaboUseCase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de Unidades de Sílabo
 * 
 * Responsabilidades:
 * - Orquestar casos de uso de SilaboUnidad
 * - Coordinar conversiones DTO ↔ Entity
 * - Gestionar transacciones
 * - Exponer API simplificada a los Controllers
 */
@ApplicationScoped
public class SilaboUnidadService {

    @Inject
    AgregarUnidadSilaboUseCase agregarUnidadUseCase;
    
    @Inject
    SilaboUnidadRepository silaboUnidadRepository;
    
    @Inject
    SilaboUnidadMapper silaboUnidadMapper;

    /**
     * Agregar nueva unidad a un sílabo
     * 
     * Flujo:
     * 1. DTO → Command
     * 2. Use Case (validaciones + persistencia)
     * 3. Entity → ResponseDTO
     */
    @Transactional
    public SilaboUnidadResponseDTO agregar(SilaboUnidadRequestDTO dto, String usuarioCreador) {
        // Convertir DTO → Command
        AgregarUnidadSilaboCommand command = new AgregarUnidadSilaboCommand(
            dto.getSilaboId(),
            dto.getNumeroUnidad(),
            dto.getTitulo(),
            dto.getSemanaInicio(),
            dto.getSemanaFin(),
            dto.getContenidos(),
            dto.getLogroAprendizaje()
        );
        
        // Ejecutar caso de uso
        SilaboUnidad unidad = agregarUnidadUseCase.execute(command, usuarioCreador);
        
        // Convertir Entity → ResponseDTO
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    /**
     * Actualizar unidad existente
     */
    @Transactional
    public SilaboUnidadResponseDTO actualizar(Long id, SilaboUnidadRequestDTO dto, String usuarioModificador) {
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada con ID: " + id));
        
        // Validar que el sílabo sea modificable
        if (!unidad.getSilabo().esModificable()) {
            throw new IllegalStateException(
                "No se puede modificar una unidad de un sílabo en estado " + unidad.getSilabo().getEstado()
            );
        }
        
        // Actualizar campos usando el mapper
        silaboUnidadMapper.updateEntityFromDto(dto, unidad);
        
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    /**
     * Buscar unidad por ID
     */
    public SilaboUnidadResponseDTO buscarPorId(Long id) {
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada con ID: " + id));
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    /**
     * Listar unidades de un sílabo
     */
    public List<SilaboUnidadResponseDTO> listarPorSilabo(Long silaboId) {
        return silaboUnidadRepository.findBySilabo(silaboId).stream()
                .map(silaboUnidadMapper::toResponseDTOWithoutActividades)
                .collect(Collectors.toList());
    }

    /**
     * Buscar unidad específica de un sílabo por número
     */
    public SilaboUnidadResponseDTO buscarPorSilaboYNumero(Long silaboId, Integer numeroUnidad) {
        SilaboUnidad unidad = silaboUnidadRepository.findBySilaboAndNumero(silaboId, numeroUnidad)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró la unidad " + numeroUnidad + " del sílabo " + silaboId
                ));
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    /**
     * Buscar unidades que incluyen una semana específica
     */
    public List<SilaboUnidadResponseDTO> buscarPorSemana(Long silaboId, Integer semana) {
        return silaboUnidadRepository.findBySemana(silaboId, semana).stream()
                .map(silaboUnidadMapper::toResponseDTOWithoutActividades)
                .collect(Collectors.toList());
    }

    /**
     * Eliminar (lógicamente) una unidad
     * 
     * Solo permite eliminar si el sílabo está en estado modificable
     */
    @Transactional
    public void eliminar(Long id) {
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada con ID: " + id));
        
        if (!unidad.getSilabo().esModificable()) {
            throw new IllegalStateException(
                "No se puede eliminar una unidad de un sílabo en estado " + unidad.getSilabo().getEstado()
            );
        }
        
        unidad.setActive(false);
    }
}
