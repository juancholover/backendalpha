package upeu.edu.pe.security.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.RolRequestDTO;
import upeu.edu.pe.security.application.dto.RolResponseDTO;
import upeu.edu.pe.security.application.mapper.RolMapper;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.usecases.ActualizarRolUseCase;
import upeu.edu.pe.security.domain.usecases.BuscarRolUseCase;
import upeu.edu.pe.security.domain.usecases.CrearRolUseCase;
import upeu.edu.pe.security.domain.usecases.EliminarRolUseCase;

import java.util.List;

@ApplicationScoped
public class RolApplicationService {

    @Inject
    CrearRolUseCase crearUseCase;

    @Inject
    ActualizarRolUseCase actualizarUseCase;

    @Inject
    BuscarRolUseCase buscarUseCase;

    @Inject
    EliminarRolUseCase eliminarUseCase;

    @Inject
    RolMapper mapper;

    @Transactional
    public RolResponseDTO create(RolRequestDTO dto) {
        Rol rol = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getEsSistema());
        return mapper.toResponseDTO(rol);
    }

    @Transactional
    public RolResponseDTO update(Long id, RolRequestDTO dto) {
        Rol rol = actualizarUseCase.execute(
                id,
                dto.getNombre(),
                dto.getDescripcion());
        return mapper.toResponseDTO(rol);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public RolResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public List<RolResponseDTO> findAll() {
        return mapper.toResponseDTOList(buscarUseCase.findAll());
    }

    public List<RolResponseDTO> findByUniversidad(Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findByUniversidad(universidadId));
    }

    public List<RolResponseDTO> findRolesSistema() {
        return mapper.toResponseDTOList(buscarUseCase.findRolesSistema());
    }

    public boolean existsByNombre(String nombre, Long universidadId) {
        return buscarUseCase.existsByNombre(nombre, universidadId);
    }

    public List<RolResponseDTO> findByPermisoNombre(String permisoNombre, Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPermisoNombre(permisoNombre, universidadId));
    }

    public long countUsuariosConRol(Long rolId) {
        return buscarUseCase.countUsuariosConRol(rolId);
    }
}
