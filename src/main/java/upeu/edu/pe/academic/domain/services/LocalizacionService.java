package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.LocalizacionRequestDTO;
import upeu.edu.pe.academic.application.dto.LocalizacionResponseDTO;
import upeu.edu.pe.academic.application.mapper.LocalizacionMapper;
import upeu.edu.pe.academic.domain.entities.Localizacion;
import upeu.edu.pe.academic.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class LocalizacionService {

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    LocalizacionMapper localizacionMapper;

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
            localizacionRepository.findByTipo(tipoId)
        );
    }

    @Transactional
    public LocalizacionResponseDTO create(LocalizacionRequestDTO requestDTO) {
        Localizacion localizacion = localizacionMapper.toEntity(requestDTO);
        localizacionRepository.persist(localizacion);
        return localizacionMapper.toResponseDTO(localizacion);
    }

    @Transactional
    public LocalizacionResponseDTO update(Long id, LocalizacionRequestDTO requestDTO) {
        Localizacion localizacion = localizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + id));
        localizacionMapper.updateEntityFromDTO(requestDTO, localizacion);
        localizacionRepository.persist(localizacion);
        return localizacionMapper.toResponseDTO(localizacion);
    }

    @Transactional
    public void delete(Long id) {
        Localizacion localizacion = localizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + id));
        localizacionRepository.delete(localizacion);
    }
}
