package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.academic.application.mapper.UnidadOrganizativaMapper;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.academic.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class UnidadOrganizativaService {

    @Inject
    UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Inject
    UnidadOrganizativaMapper unidadOrganizativaMapper;

    public List<UnidadOrganizativaResponseDTO> findAll() {
        return unidadOrganizativaMapper.toResponseDTOList(unidadOrganizativaRepository.listAll());
    }

    public UnidadOrganizativaResponseDTO findById(Long id) {
        UnidadOrganizativa unidad = unidadOrganizativaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
        return unidadOrganizativaMapper.toResponseDTO(unidad);
    }

    public List<UnidadOrganizativaResponseDTO> findByTipoDeUnidad(Long tipoId) {
        return unidadOrganizativaMapper.toResponseDTOList(
            unidadOrganizativaRepository.findByTipo(tipoId)
        );
    }

    public List<UnidadOrganizativaResponseDTO> findByUnidadPadre(Long unidadPadreId) {
        return unidadOrganizativaMapper.toResponseDTOList(
            unidadOrganizativaRepository.findByUnidadPadre(unidadPadreId)
        );
    }

    @Transactional
    public UnidadOrganizativaResponseDTO create(UnidadOrganizativaRequestDTO requestDTO) {
        UnidadOrganizativa unidad = unidadOrganizativaMapper.toEntity(requestDTO);
        unidadOrganizativaRepository.persist(unidad);
        return unidadOrganizativaMapper.toResponseDTO(unidad);
    }

    @Transactional
    public UnidadOrganizativaResponseDTO update(Long id, UnidadOrganizativaRequestDTO requestDTO) {
        UnidadOrganizativa unidad = unidadOrganizativaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
        unidadOrganizativaMapper.updateEntityFromDTO(requestDTO, unidad);
        unidadOrganizativaRepository.persist(unidad);
        return unidadOrganizativaMapper.toResponseDTO(unidad);
    }

    @Transactional
    public void delete(Long id) {
        UnidadOrganizativa unidad = unidadOrganizativaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
        unidadOrganizativaRepository.delete(unidad);
    }
}
