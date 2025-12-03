package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.TipoUnidadRequestDTO;
import upeu.edu.pe.academic.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.academic.application.mapper.TipoUnidadMapper;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import upeu.edu.pe.academic.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class TipoUnidadService {

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Inject
    TipoUnidadMapper tipoUnidadMapper;

    public List<TipoUnidadResponseDTO> findAll() {
        return tipoUnidadMapper.toResponseDTOList(tipoUnidadRepository.listAll());
    }

    public TipoUnidadResponseDTO findById(Long id) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    @Transactional
    public TipoUnidadResponseDTO create(TipoUnidadRequestDTO requestDTO) {
        TipoUnidad tipoUnidad = tipoUnidadMapper.toEntity(requestDTO);
        tipoUnidadRepository.persist(tipoUnidad);
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    @Transactional
    public TipoUnidadResponseDTO update(Long id, TipoUnidadRequestDTO requestDTO) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
        tipoUnidadMapper.updateEntityFromDTO(requestDTO, tipoUnidad);
        tipoUnidadRepository.persist(tipoUnidad);
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    @Transactional
    public void delete(Long id) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
        tipoUnidadRepository.delete(tipoUnidad);
    }
}
