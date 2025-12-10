package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.core.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.core.application.mapper.TipoLocalizacionMapper;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class TipoLocalizacionService {

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    TipoLocalizacionMapper tipoLocalizacionMapper;

    public List<TipoLocalizacionResponseDTO> findAll() {
        return tipoLocalizacionMapper.toResponseDTOList(tipoLocalizacionRepository.listAll());
    }

    public TipoLocalizacionResponseDTO findById(Long id) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    @Transactional
    public TipoLocalizacionResponseDTO create(TipoLocalizacionRequestDTO requestDTO) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionMapper.toEntity(requestDTO);
        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    @Transactional
    public TipoLocalizacionResponseDTO update(Long id, TipoLocalizacionRequestDTO requestDTO) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
        tipoLocalizacionMapper.updateEntityFromDTO(requestDTO, tipoLocalizacion);
        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    @Transactional
    public void delete(Long id) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
        tipoLocalizacionRepository.delete(tipoLocalizacion);
    }
}

