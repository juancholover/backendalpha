package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.SilaboResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboMapper;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.usecases.BuscarSilaboUseCase;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de Sílabos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class SilaboService {

    @Inject
    BuscarSilaboUseCase buscarUseCase;

    @Inject
    SilaboMapper silaboMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public SilaboResponseDTO buscarPorId(Long id) {
        Silabo silabo = buscarUseCase.findById(id)
                .orElseThrow(() -> new NotFoundException("Sílabo no encontrado con ID: " + id));
        return silaboMapper.toResponseDTO(silabo);
    }

    public List<SilaboResponseDTO> buscarVigentePorCurso(Long cursoId, Long universidadId) {
        return buscarUseCase.findVigenteByCurso(cursoId, universidadId).stream()
                .map(silaboMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SilaboResponseDTO> buscarPorCurso(Long cursoId, Long universidadId) {
        return buscarUseCase.findByCurso(cursoId, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    public SilaboResponseDTO buscarPorCursoYAnio(Long cursoId, String anioAcademico, Long universidadId) {
        Silabo silabo = buscarUseCase.findByCursoAndAnio(cursoId, anioAcademico, universidadId)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró sílabo para el curso " + cursoId + " del año " + anioAcademico));
        return silaboMapper.toResponseDTO(silabo);
    }

    public List<SilaboResponseDTO> listarPorAnio(String anioAcademico, Long universidadId) {
        return buscarUseCase.findByAnioAcademico(anioAcademico, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    public List<SilaboResponseDTO> listarPorEstado(String estado, Long universidadId) {
        return buscarUseCase.findByEstado(estado, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    public List<SilaboResponseDTO> listarPendientesAprobacion(Long universidadId) {
        return buscarUseCase.findPendientesAprobacion(universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    public List<SilaboResponseDTO> listarAprobadosPorAnio(String anioAcademico, Long universidadId) {
        return buscarUseCase.findAprobadosByAnio(anioAcademico, universidadId).stream()
                .map(silaboMapper::toResponseDTOWithoutUnidades)
                .collect(Collectors.toList());
    }

    public SilaboResponseDTO obtenerUltimaVersion(Long cursoId, Long universidadId) {
        Silabo silabo = buscarUseCase.findUltimaVersion(cursoId, universidadId)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró sílabo para el curso " + cursoId));
        return silaboMapper.toResponseDTO(silabo);
    }

    public long contarPorEstado(String estado, Long universidadId) {
        return buscarUseCase.countByEstado(estado, universidadId);
    }

    public Silabo getEntityById(Long id) {
        return buscarUseCase.findById(id)
                .orElseThrow(() -> new NotFoundException("Sílabo no encontrado con ID: " + id));
    }
}
