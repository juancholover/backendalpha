package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.core.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.core.application.mapper.UniversidadMapper;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.repositories.UniversidadRepository;
import upeu.edu.pe.core.domain.usecases.BuscarUniversidadUseCase;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de universidades.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class UniversidadService {

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    UniversidadMapper universidadMapper;

    @Inject
    BuscarUniversidadUseCase buscarUseCase;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<UniversidadResponseDTO> findAll() {
        return buscarUseCase.ejecutarListarTodas().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UniversidadResponseDTO> findAllActive() {
        return buscarUseCase.ejecutarListarActivas().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UniversidadResponseDTO findById(Long id) {
        Universidad universidad = buscarUseCase.ejecutarPorId(id);
        return universidadMapper.toResponseDTO(universidad);
    }

    public UniversidadResponseDTO findByCodigo(String codigo) {
        Universidad universidad = buscarUseCase.ejecutarPorCodigo(codigo);
        return universidadMapper.toResponseDTO(universidad);
    }

    public UniversidadResponseDTO findByDominio(String dominio) {
        Universidad universidad = universidadRepository.findByDominio(dominio)
                .orElseThrow(() -> new NotFoundException("Universidad no encontrada con dominio: " + dominio));
        return universidadMapper.toResponseDTO(universidad);
    }

    public List<UniversidadResponseDTO> search(String query) {
        return universidadRepository.list("LOWER(nombre) LIKE LOWER(?1)", "%" + query + "%").stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public Universidad getEntityById(Long id) {
        return buscarUseCase.ejecutarPorId(id);
    }
}
