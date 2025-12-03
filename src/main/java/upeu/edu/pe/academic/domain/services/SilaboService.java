package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.SilaboRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboResponseDTO;
import upeu.edu.pe.academic.application.mapper.SilaboMapper;
import upeu.edu.pe.academic.domain.commands.ActualizarSilaboCommand;
import upeu.edu.pe.academic.domain.commands.CrearSilaboCommand;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;
import upeu.edu.pe.academic.domain.usecases.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para gestión de Sílabos
 * 
 * Responsabilidades:
 * - Orquestar casos de uso de Sílabo
 * - Coordinar conversiones DTO ↔ Entity
 * - Gestionar transacciones complejas (ej: aprobar + activar + obsolete)
 * - Exponer API simplificada a los Controllers
 */
@ApplicationScoped
public class SilaboService {

    @Inject
    CrearSilaboUseCase crearUseCase;
    
    @Inject
    ActualizarSilaboUseCase actualizarUseCase;
    
    @Inject
    AprobarSilaboUseCase aprobarUseCase;
    
    @Inject
    RechazarSilaboUseCase rechazarUseCase;
    
    @Inject
    ActivarSilaboUseCase activarUseCase;
    
    @Inject
    BuscarSilaboUseCase buscarUseCase;
    
    @Inject
    SilaboMapper silaboMapper;
    
    @Inject
    SilaboRepository silaboRepository;

    /**
     * Crear nuevo sílabo
     * 
     * Flujo:
     * 1. DTO → Command
     * 2. Use Case (validaciones + persistencia)
     * 3. Entity → ResponseDTO
     */
    @Transactional
    public SilaboResponseDTO crear(SilaboRequestDTO dto, String usuarioCreador) {
     
        CrearSilaboCommand command = new CrearSilaboCommand(
            dto.getCursoId(),
            dto.getUniversidadId(),
            dto.getAnioAcademico(),
            dto.getCompetencias(),
            dto.getSumilla(),
            dto.getBibliografia(),
            dto.getMetodologia(),
            dto.getRecursosDidacticos()
        );
        
        // Ejecutar caso de uso
        Silabo silabo = crearUseCase.execute(command, usuarioCreador);
        
   
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Actualizar sílabo existente
     * 
     * Solo permite actualizar si está en estado modificable (BORRADOR o EN_REVISION)
     */
    @Transactional
    public SilaboResponseDTO actualizar(Long id, ActualizarSilaboCommand command, String usuarioModificador) {
        // Ejecutar caso de uso
        Silabo silabo = actualizarUseCase.execute(command, usuarioModificador);
        
        // Convertir Entity → ResponseDTO
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Enviar sílabo a revisión
     * 
     * Valida completitud antes de cambiar estado
     */
    @Transactional
    public SilaboResponseDTO enviarARevision(Long silaboId, String usuarioSolicitante) {
        Silabo silabo = buscarUseCase.findById(silaboId)
                .orElseThrow(() -> new IllegalArgumentException("Sílabo no encontrado con ID: " + silaboId));
        
        // Validar que esté en BORRADOR
        if (!"BORRADOR".equals(silabo.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden enviar a revisión sílabos en borrador. Estado actual: " + silabo.getEstado()
            );
        }
        
        // Validar completitud
        if (!silabo.estaCompleto()) {
            throw new IllegalStateException(
                "El sílabo no está completo. Debe tener sumilla, competencias, al menos una unidad y las evaluaciones deben sumar 100%"
            );
        }
        
        // Cambiar estado
        silabo.enviarARevision();
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "ENVIO_REVISION",
            usuarioSolicitante != null ? usuarioSolicitante : "SYSTEM",
            "Sílabo enviado a revisión"
        );
        silabo.getHistorial().add(historial);
        
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Aprobar sílabo en revisión
     */
    @Transactional
    public SilaboResponseDTO aprobar(Long silaboId, String usuarioAprobador, String observaciones) {
        Silabo silabo = aprobarUseCase.execute(silaboId, usuarioAprobador, observaciones);
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Rechazar sílabo en revisión
     * 
     * Requiere motivo de rechazo
     */
    @Transactional
    public SilaboResponseDTO rechazar(Long silaboId, String usuarioRevisor, String motivoRechazo) {
        Silabo silabo = rechazarUseCase.execute(silaboId, usuarioRevisor, motivoRechazo);
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Activar sílabo aprobado
     * 
     * Marca como VIGENTE y obsoleta versiones anteriores
     */
    @Transactional
    public SilaboResponseDTO activar(Long silaboId, String usuarioActivador) {
        Silabo silabo = activarUseCase.execute(silaboId, usuarioActivador);
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Proceso completo: Aprobar y Activar en una sola transacción
     * 
     * Útil cuando se quiere aprobar y publicar inmediatamente
     */
    @Transactional
    public SilaboResponseDTO aprobarYActivar(Long silaboId, String usuarioAprobador, String observaciones) {
        // 1. Aprobar
        Silabo silabo = aprobarUseCase.execute(silaboId, usuarioAprobador, observaciones);
        
        // 2. Activar
        silabo = activarUseCase.execute(silabo.getId(), usuarioAprobador);
        
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Buscar sílabo por ID
     */
    public SilaboResponseDTO buscarPorId(Long id) {
        Silabo silabo = buscarUseCase.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sílabo no encontrado con ID: " + id));
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Buscar sílabo vigente de un curso
     */
    public List<SilaboResponseDTO> buscarVigentePorCurso(Long cursoId, Long universidadId) {
        return buscarUseCase.findVigenteByCurso(cursoId, universidadId).stream()
                .map(silaboMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar todas las versiones de sílabo de un curso
     */
    public List<SilaboResponseDTO> buscarPorCurso(Long cursoId, Long universidadId) {
        return buscarUseCase.findByCurso(cursoId, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    /**
     * Buscar sílabo de un curso en un año específico
     */
    public SilaboResponseDTO buscarPorCursoYAnio(Long cursoId, String anioAcademico, Long universidadId) {
        Silabo silabo = buscarUseCase.findByCursoAndAnio(cursoId, anioAcademico, universidadId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró sílabo para el curso " + cursoId + " del año " + anioAcademico
                ));
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Listar sílabos por año académico
     */
    public List<SilaboResponseDTO> listarPorAnio(String anioAcademico, Long universidadId) {
        return buscarUseCase.findByAnioAcademico(anioAcademico, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    /**
     * Listar sílabos por estado
     */
    public List<SilaboResponseDTO> listarPorEstado(String estado, Long universidadId) {
        return buscarUseCase.findByEstado(estado, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    /**
     * Listar sílabos pendientes de aprobación
     */
    public List<SilaboResponseDTO> listarPendientesAprobacion(Long universidadId) {
        return buscarUseCase.findPendientesAprobacion(universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    /**
     * Listar sílabos aprobados en un año
     */
    public List<SilaboResponseDTO> listarAprobadosPorAnio(String anioAcademico, Long universidadId) {
        return buscarUseCase.findAprobadosByAnio(anioAcademico, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    /**
     * Obtener última versión de sílabo de un curso
     */
    public SilaboResponseDTO obtenerUltimaVersion(Long cursoId, Long universidadId) {
        Silabo silabo = buscarUseCase.findUltimaVersion(cursoId, universidadId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró sílabo para el curso " + cursoId
                ));
        return silaboMapper.toResponseDTO(silabo);
    }

    /**
     * Contar sílabos por estado
     */
    public long contarPorEstado(String estado, Long universidadId) {
        return buscarUseCase.countByEstado(estado, universidadId);
    }

    /**
     * Eliminar (lógicamente) un sílabo
     * 
     * Solo permite eliminar sílabos en BORRADOR
     */
    @Transactional
    public void eliminar(Long id) {
        Silabo silabo = buscarUseCase.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sílabo no encontrado con ID: " + id));
        
        if (!"BORRADOR".equals(silabo.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden eliminar sílabos en borrador. Estado actual: " + silabo.getEstado()
            );
        }
        
        silabo.setActive(false);
    }
}
