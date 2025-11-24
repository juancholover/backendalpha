package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.academic.application.mapper.UnidadOrganizativaMapper;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class UnidadOrganizativaService {

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    UnidadOrganizativaMapper unidadMapper;

    public List<UnidadOrganizativaResponseDTO> findAll() {
        return unidadMapper.toResponseDTOList(unidadRepository.findAllActive());
    }

    public UnidadOrganizativaResponseDTO findById(Long id) {
        UnidadOrganizativa unidad = unidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
        return unidadMapper.toResponseDTO(unidad);
    }

    public List<UnidadOrganizativaResponseDTO> findByUniversidad(Long universidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findByUniversidad(universidadId));
    }

    public List<UnidadOrganizativaResponseDTO> findByTipoUnidad(Long tipoUnidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findByTipoUnidad(tipoUnidadId));
    }

    public List<UnidadOrganizativaResponseDTO> findRootUnidades(Long universidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findRootUnidades(universidadId));
    }

    public List<UnidadOrganizativaResponseDTO> findByUnidadPadre(Long unidadPadreId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findByUnidadPadre(unidadPadreId));
    }

    @Transactional
    public UnidadOrganizativaResponseDTO create(UnidadOrganizativaRequestDTO requestDTO) {
        // Validar que la universidad existe
        if (!universidadRepository.findByIdOptional(requestDTO.getUniversidadId()).isPresent()) {
            throw new NotFoundException("Universidad no encontrada con ID: " + requestDTO.getUniversidadId());
        }

        // Validar que el tipo de unidad existe
        if (!tipoUnidadRepository.findByIdOptional(requestDTO.getTipoUnidadId()).isPresent()) {
            throw new NotFoundException("Tipo de unidad no encontrado con ID: " + requestDTO.getTipoUnidadId());
        }

        // Validar código único por universidad (si se proporciona)
        if (requestDTO.getCodigo() != null && 
            unidadRepository.existsByCodigoAndUniversidad(requestDTO.getCodigo(), requestDTO.getUniversidadId())) {
            throw new BusinessException("Ya existe una unidad organizativa con el código: " + requestDTO.getCodigo());
        }

        // Validar nombre único por universidad
        if (unidadRepository.existsByNombreAndUniversidad(requestDTO.getNombre(), requestDTO.getUniversidadId())) {
            throw new BusinessException("Ya existe una unidad organizativa con el nombre: " + requestDTO.getNombre());
        }

        // Validar que la unidad padre existe y pertenece a la misma universidad (si se especifica)
        if (requestDTO.getUnidadPadreId() != null) {
            UnidadOrganizativa unidadPadre = unidadRepository.findByIdOptional(requestDTO.getUnidadPadreId())
                    .orElseThrow(() -> new NotFoundException("Unidad padre no encontrada con ID: " + requestDTO.getUnidadPadreId()));
            
            if (!unidadPadre.getUniversidad().getId().equals(requestDTO.getUniversidadId())) {
                throw new BusinessException("La unidad padre debe pertenecer a la misma universidad");
            }
        }

        // Validar que la localización existe y pertenece a la misma universidad (si se especifica)
        if (requestDTO.getLocalizacionId() != null) {
            var localizacion = localizacionRepository.findByIdOptional(requestDTO.getLocalizacionId())
                    .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + requestDTO.getLocalizacionId()));
            
            if (!localizacion.getUniversidad().getId().equals(requestDTO.getUniversidadId())) {
                throw new BusinessException("La localización debe pertenecer a la misma universidad");
            }
        }

        UnidadOrganizativa unidad = unidadMapper.toEntity(requestDTO);
        unidadRepository.persist(unidad);
        return unidadMapper.toResponseDTO(unidad);
    }

    @Transactional
    public UnidadOrganizativaResponseDTO update(Long id, UnidadOrganizativaRequestDTO requestDTO) {
        UnidadOrganizativa unidad = unidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));

        // Validar que la universidad existe
        if (!universidadRepository.findByIdOptional(requestDTO.getUniversidadId()).isPresent()) {
            throw new NotFoundException("Universidad no encontrada con ID: " + requestDTO.getUniversidadId());
        }

        // Validar que el tipo de unidad existe
        if (!tipoUnidadRepository.findByIdOptional(requestDTO.getTipoUnidadId()).isPresent()) {
            throw new NotFoundException("Tipo de unidad no encontrado con ID: " + requestDTO.getTipoUnidadId());
        }

        // Validar código único por universidad (excluyendo el actual)
        if (requestDTO.getCodigo() != null && 
            unidadRepository.existsByCodigoAndUniversidadAndIdNot(
                requestDTO.getCodigo(), requestDTO.getUniversidadId(), id)) {
            throw new BusinessException("Ya existe otra unidad organizativa con el código: " + requestDTO.getCodigo());
        }

        // Validar nombre único por universidad (excluyendo el actual)
        if (unidadRepository.existsByNombreAndUniversidadAndIdNot(
                requestDTO.getNombre(), requestDTO.getUniversidadId(), id)) {
            throw new BusinessException("Ya existe otra unidad organizativa con el nombre: " + requestDTO.getNombre());
        }

        // Validar que la unidad padre existe y no es la misma unidad (evitar ciclos)
        if (requestDTO.getUnidadPadreId() != null) {
            if (requestDTO.getUnidadPadreId().equals(id)) {
                throw new BusinessException("Una unidad no puede ser padre de sí misma");
            }
            
            UnidadOrganizativa unidadPadre = unidadRepository.findByIdOptional(requestDTO.getUnidadPadreId())
                    .orElseThrow(() -> new NotFoundException("Unidad padre no encontrada con ID: " + requestDTO.getUnidadPadreId()));
            
            if (!unidadPadre.getUniversidad().getId().equals(requestDTO.getUniversidadId())) {
                throw new BusinessException("La unidad padre debe pertenecer a la misma universidad");
            }
        }

        // Validar que la localización existe y pertenece a la misma universidad (si se especifica)
        if (requestDTO.getLocalizacionId() != null) {
            var localizacion = localizacionRepository.findByIdOptional(requestDTO.getLocalizacionId())
                    .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + requestDTO.getLocalizacionId()));
            
            if (!localizacion.getUniversidad().getId().equals(requestDTO.getUniversidadId())) {
                throw new BusinessException("La localización debe pertenecer a la misma universidad");
            }
        }

        unidadMapper.updateEntityFromDTO(requestDTO, unidad);
        unidadRepository.persist(unidad);
        return unidadMapper.toResponseDTO(unidad);
    }

    @Transactional
    public void delete(Long id) {
        UnidadOrganizativa unidad = unidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));

        // Validar que no tenga unidades hijas antes de eliminar
        if (unidadRepository.hasUnidadesHijas(id)) {
            throw new BusinessException("No se puede eliminar la unidad organizativa porque tiene unidades hijas asociadas");
        }

        // Soft delete
        unidad.setActive(false);
        unidadRepository.persist(unidad);
    }
}
