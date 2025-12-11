package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.core.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.core.application.mapper.TipoLocalizacionMapper;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de tipos de localización.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class TipoLocalizacionService {

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    TipoLocalizacionMapper tipoLocalizacionMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<TipoLocalizacionResponseDTO> findAll() {
        return tipoLocalizacionMapper.toResponseDTOList(tipoLocalizacionRepository.listAll());
    }

    public TipoLocalizacionResponseDTO findById(Long id) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public TipoLocalizacion getEntityById(Long id) {
        return tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
    }
}
