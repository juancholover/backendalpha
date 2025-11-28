package upeu.edu.pe.catalog.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import upeu.edu.pe.catalog.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.catalog.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.catalog.application.mapper.TipoLocalizacionMapper;
import upeu.edu.pe.catalog.domain.usecases.ActualizarTipoLocalizacionUseCase;
import upeu.edu.pe.catalog.domain.usecases.BuscarTipoLocalizacionUseCase;
import upeu.edu.pe.catalog.domain.usecases.CrearTipoLocalizacionUseCase;
import upeu.edu.pe.catalog.domain.usecases.EliminarTipoLocalizacionUseCase;

import java.util.List;

@ApplicationScoped
public class TipoLocalizacionApplicationService {

    @Inject
    CrearTipoLocalizacionUseCase crearTipoLocalizacionUseCase;

    @Inject
    ActualizarTipoLocalizacionUseCase actualizarTipoLocalizacionUseCase;

    @Inject
    BuscarTipoLocalizacionUseCase buscarTipoLocalizacionUseCase;

    @Inject
    EliminarTipoLocalizacionUseCase eliminarTipoLocalizacionUseCase;

    @Inject
    TipoLocalizacionMapper tipoLocalizacionMapper;

    @Transactional
    public TipoLocalizacionResponseDTO create(TipoLocalizacionRequestDTO requestDTO) {
        TipoLocalizacion tipoLocalizacion = crearTipoLocalizacionUseCase.execute(requestDTO.getNombre());
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    @Transactional
    public TipoLocalizacionResponseDTO update(Long id, TipoLocalizacionRequestDTO requestDTO) {
        TipoLocalizacion tipoLocalizacion = actualizarTipoLocalizacionUseCase.execute(id, requestDTO.getNombre());
        return tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);
    }

    public void delete(Long id) {
        eliminarTipoLocalizacionUseCase.execute(id);
    }

    public TipoLocalizacionResponseDTO findById(Long id) {
        return tipoLocalizacionMapper.toResponseDTO(buscarTipoLocalizacionUseCase.findById(id));
    }

    public List<TipoLocalizacionResponseDTO> findAll() {
        return tipoLocalizacionMapper.toResponseDTOList(buscarTipoLocalizacionUseCase.findAll());
    }

    public List<TipoLocalizacionResponseDTO> search(String query) {
        return tipoLocalizacionMapper.toResponseDTOList(buscarTipoLocalizacionUseCase.search(query));
    }
}
