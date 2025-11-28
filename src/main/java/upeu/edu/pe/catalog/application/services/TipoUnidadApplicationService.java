package upeu.edu.pe.catalog.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import upeu.edu.pe.catalog.application.dto.TipoUnidadRequestDTO;
import upeu.edu.pe.catalog.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.catalog.application.mapper.TipoUnidadMapper;
import upeu.edu.pe.catalog.domain.usecases.ActualizarTipoUnidadUseCase;
import upeu.edu.pe.catalog.domain.usecases.BuscarTipoUnidadUseCase;
import upeu.edu.pe.catalog.domain.usecases.CrearTipoUnidadUseCase;
import upeu.edu.pe.catalog.domain.usecases.EliminarTipoUnidadUseCase;

import java.util.List;

@ApplicationScoped
public class TipoUnidadApplicationService {

    @Inject
    CrearTipoUnidadUseCase crearTipoUnidadUseCase;

    @Inject
    ActualizarTipoUnidadUseCase actualizarTipoUnidadUseCase;

    @Inject
    BuscarTipoUnidadUseCase buscarTipoUnidadUseCase;

    @Inject
    EliminarTipoUnidadUseCase eliminarTipoUnidadUseCase;

    @Inject
    TipoUnidadMapper tipoUnidadMapper;

    @Transactional
    public TipoUnidadResponseDTO create(TipoUnidadRequestDTO requestDTO) {
        TipoUnidad tipoUnidad = crearTipoUnidadUseCase.execute(
                requestDTO.getUniversidadId(),
                requestDTO.getNombre(),
                requestDTO.getDescripcion(),
                requestDTO.getNivel());
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    @Transactional
    public TipoUnidadResponseDTO update(Long id, TipoUnidadRequestDTO requestDTO) {
        TipoUnidad tipoUnidad = actualizarTipoUnidadUseCase.execute(
                id,
                requestDTO.getUniversidadId(),
                requestDTO.getNombre(),
                requestDTO.getDescripcion(),
                requestDTO.getNivel());
        return tipoUnidadMapper.toResponseDTO(tipoUnidad);
    }

    public void delete(Long id) {
        eliminarTipoUnidadUseCase.execute(id);
    }

    public TipoUnidadResponseDTO findById(Long id) {
        return tipoUnidadMapper.toResponseDTO(buscarTipoUnidadUseCase.findById(id));
    }

    public List<TipoUnidadResponseDTO> findAll() {
        return tipoUnidadMapper.toResponseDTOList(buscarTipoUnidadUseCase.findAll());
    }

    public List<TipoUnidadResponseDTO> search(String query) {
        return tipoUnidadMapper.toResponseDTOList(buscarTipoUnidadUseCase.search(query));
    }
}
