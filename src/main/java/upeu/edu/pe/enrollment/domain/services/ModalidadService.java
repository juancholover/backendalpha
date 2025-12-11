package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.ModalidadResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.ModalidadMapper;
import upeu.edu.pe.enrollment.domain.entities.Modalidad;
import upeu.edu.pe.enrollment.domain.usecases.BuscarModalidadUseCase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de modalidades.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class ModalidadService {

    @Inject
    BuscarModalidadUseCase buscarUseCase;

    @Inject
    ModalidadMapper modalidadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public ModalidadResponseDTO buscarPorId(Long id) {
        Modalidad modalidad = buscarUseCase.findById(id);
        return modalidadMapper.toResponseDTO(modalidad);
    }

    public ModalidadResponseDTO buscarPorCodigo(String codigo, Long universidadId) {
        Modalidad modalidad = buscarUseCase.findByCodigo(codigo, universidadId);
        return modalidadMapper.toResponseDTO(modalidad);
    }

    public List<ModalidadResponseDTO> listarPorUniversidad(Long universidadId) {
        return buscarUseCase.findByUniversidad(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ModalidadResponseDTO> listarRequierenAula(Long universidadId) {
        return buscarUseCase.findRequiereAula(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ModalidadResponseDTO> listarRequierenPlataforma(Long universidadId) {
        return buscarUseCase.findRequierePlataforma(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ModalidadResponseDTO> buscarPorNombre(String nombre, Long universidadId) {
        return buscarUseCase.findByNombre(nombre, universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Modalidad getEntityById(Long id) {
        return buscarUseCase.findById(id);
    }
}
