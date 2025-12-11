package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.LocalizacionResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.LocalizacionMapper;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de localizaciones.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class LocalizacionService {

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    LocalizacionMapper localizacionMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<LocalizacionResponseDTO> findAll() {
        return localizacionMapper.toResponseDTOList(localizacionRepository.listAll());
    }

    public LocalizacionResponseDTO findById(Long id) {
        Localizacion localizacion = localizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + id));
        return localizacionMapper.toResponseDTO(localizacion);
    }

    public List<LocalizacionResponseDTO> findByTipoLocalizacion(Long tipoId) {
        return localizacionMapper.toResponseDTOList(
                localizacionRepository.findByTipo(tipoId));
    }

    public Localizacion getEntityById(Long id) {
        return localizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + id));
    }
}
