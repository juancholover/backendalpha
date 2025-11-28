package upeu.edu.pe.security.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.RolPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.RolPermisoResponseDTO;
import upeu.edu.pe.security.application.mapper.RolPermisoMapper;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.usecases.AsignarPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.BuscarRolPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.RemoverPermisoUseCase;
import upeu.edu.pe.security.domain.usecases.ActualizarRolPermisoUseCase;

import java.util.List;

@ApplicationScoped
public class RolPermisoApplicationService {

    @Inject
    AsignarPermisoUseCase asignarUseCase;

    @Inject
    RemoverPermisoUseCase removerUseCase;

    @Inject
    BuscarRolPermisoUseCase buscarUseCase;

    @Inject
    ActualizarRolPermisoUseCase actualizarUseCase;

    @Inject
    RolPermisoMapper mapper;

    @Transactional
    public RolPermisoResponseDTO assignPermiso(RolPermisoRequestDTO requestDTO) {
        RolPermiso rolPermiso = asignarUseCase.execute(
                requestDTO.getRolId(),
                requestDTO.getPermisoId(),
                requestDTO.getPuedeDelegar(),
                requestDTO.getRestriccion());
        return mapper.toResponseDTO(rolPermiso);
    }

    @Transactional
    public void removePermiso(Long rolId, Long permisoId) {
        removerUseCase.execute(rolId, permisoId);
    }

    @Transactional
    public void removeAllPermisosByRol(Long rolId) {
        removerUseCase.removeAllByRol(rolId);
    }

    @Transactional
    public void assignMultiplePermisos(Long rolId, List<Long> permisoIds) {
        asignarUseCase.assignMultiple(rolId, permisoIds);
    }

    @Transactional
    public void removeMultiplePermisos(Long rolId, List<Long> permisoIds) {
        removerUseCase.removeMultiple(rolId, permisoIds);
    }

    public List<RolPermisoResponseDTO> findByRol(Long rolId) {
        return mapper.toResponseDTOList(buscarUseCase.findByRol(rolId));
    }

    public List<RolPermisoResponseDTO> findByPermiso(Long permisoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPermiso(permisoId));
    }

    public List<RolPermisoResponseDTO> findDelegablesByRol(Long rolId) {
        return mapper.toResponseDTOList(buscarUseCase.findDelegablesByRol(rolId));
    }

    public List<RolPermisoResponseDTO> findByRolAndModulo(Long rolId, String modulo) {
        return mapper.toResponseDTOList(buscarUseCase.findByRolAndModulo(rolId, modulo));
    }

    public long countByRol(Long rolId) {
        return buscarUseCase.countByRol(rolId);
    }

    @Transactional
    public RolPermisoResponseDTO updateRestriccion(Long rolId, Long permisoId, String restriccion) {
        // Implementación directa o delegada a un caso de uso específico si la lógica
        // crece
        // Por ahora, podemos usar un caso de uso existente o crear uno nuevo.
        // Dado que es una actualización simple, podemos hacerlo aquí o en
        // ActualizarRolPermisoUseCase
        // Para mantener consistencia, crearemos ActualizarRolPermisoUseCase
        return mapper.toResponseDTO(actualizarUseCase.updateRestriccion(rolId, permisoId, restriccion));
    }

    @Transactional
    public RolPermisoResponseDTO toggleDelegacion(Long rolId, Long permisoId) {
        return mapper.toResponseDTO(actualizarUseCase.toggleDelegacion(rolId, permisoId));
    }

    public boolean existsByRolAndPermiso(Long rolId, Long permisoId) {
        // Esto debería estar en BuscarRolPermisoUseCase, pero el controlador lo llama
        // directamente
        // Podemos agregarlo a BuscarRolPermisoUseCase
        return buscarUseCase.existsByRolAndPermiso(rolId, permisoId);
    }
}
