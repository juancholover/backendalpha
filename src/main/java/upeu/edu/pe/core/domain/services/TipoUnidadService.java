package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.core.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.core.application.mapper.TipoUnidadMapper;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de tipos de unidad.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class TipoUnidadService {

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Inject
    TipoUnidadMapper tipoUnidadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<TipoUnidadResponseDTO> findAll() {
        return tipoUnidadMapper.toResponseDTOList(tipoUnidadRepository.listAll());
    }

    public TipoUnidadResponseDTO findById(Long id) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public TipoUnidad getEntityById(Long id) {
        return tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
    }
}
