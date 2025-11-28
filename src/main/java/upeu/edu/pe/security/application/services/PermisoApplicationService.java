package upeu.edu.pe.security.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.PermisoRequestDTO;
import upeu.edu.pe.security.application.dto.PermisoResponseDTO;
import upeu.edu.pe.security.application.mapper.PermisoMapper;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.usecases.ActualizarPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.BuscarPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.CrearPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.EliminarPermisoUseCase;

import java.util.List;

@ApplicationScoped
public class PermisoApplicationService {

    @Inject
    CrearPermisoUseCase crearUseCase;

    @Inject
    ActualizarPermisoUseCase actualizarUseCase;

    @Inject
    BuscarPermisoUseCase buscarUseCase;

    @Inject
    EliminarPermisoUseCase eliminarUseCase;

    @Inject
    PermisoMapper mapper;

    @Transactional
    public PermisoResponseDTO create(PermisoRequestDTO requestDTO) {
        Permiso permiso = crearUseCase.execute(
                requestDTO.getNombreClave(),
                requestDTO.getDescripcion(),
                requestDTO.getModulo(),
                requestDTO.getRecurso(),
                requestDTO.getAccion());
        return mapper.toResponseDTO(permiso);
    }

    @Transactional
    public PermisoResponseDTO update(Long id, PermisoRequestDTO requestDTO) {
        Permiso permiso = actualizarUseCase.execute(
                id,
                requestDTO.getNombreClave(),
                requestDTO.getDescripcion(),
                requestDTO.getModulo(),
                requestDTO.getRecurso(),
                requestDTO.getAccion());
        return mapper.toResponseDTO(permiso);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public List<PermisoResponseDTO> findAll() {
        return mapper.toResponseDTOList(buscarUseCase.findAll());
    }

    public PermisoResponseDTO findById(Long id) {
        Permiso permiso = buscarUseCase.findById(id);
        PermisoResponseDTO response = mapper.toResponseDTO(permiso);
        response.setCantidadRoles(permiso.getRolPermisos() != null ? permiso.getRolPermisos().size() : 0);
        return response;
    }

    public PermisoResponseDTO findByNombreClave(String nombreClave) {
        return mapper.toResponseDTO(buscarUseCase.findByNombreClave(nombreClave));
    }

    public List<PermisoResponseDTO> findByModulo(String modulo) {
        return mapper.toResponseDTOList(buscarUseCase.findByModulo(modulo));
    }

    public List<PermisoResponseDTO> findByRecurso(String recurso) {
        return mapper.toResponseDTOList(buscarUseCase.findByRecurso(recurso));
    }

    public List<PermisoResponseDTO> findByAccion(String accion) {
        return mapper.toResponseDTOList(buscarUseCase.findByAccion(accion));
    }

    public List<PermisoResponseDTO> findByRol(Long rolId) {
        return mapper.toResponseDTOList(buscarUseCase.findByRol(rolId));
    }

    public List<PermisoResponseDTO> search(String query) {
        return mapper.toResponseDTOList(buscarUseCase.search(query));
    }
}
