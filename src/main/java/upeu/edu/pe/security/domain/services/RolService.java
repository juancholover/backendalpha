package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.RolRequestDTO;
import upeu.edu.pe.security.application.dto.RolResponseDTO;
import upeu.edu.pe.security.application.mapper.RolMapper;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class RolService {

    @Inject
    RolRepository rolRepository;

    @Inject
    RolMapper rolMapper;

    public List<RolResponseDTO> findAll() {
        List<Rol> roles = rolRepository.listAll();
        return rolMapper.toResponseDTOList(roles);
    }

    public List<RolResponseDTO> findByUniversidad(Long universidadId) {
        List<Rol> roles = rolRepository.findByUniversidad(universidadId);
        return rolMapper.toResponseDTOList(roles);
    }

    public RolResponseDTO findById(Long id) {
        Rol rol = rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));
        
        RolResponseDTO response = rolMapper.toResponseDTO(rol);
        response.setCantidadPermisos(rol.getRolPermisos() != null ? rol.getRolPermisos().size() : 0);
        response.setCantidadUsuarios((int) rolRepository.countUsuariosConRol(id));
        
        return response;
    }

    public RolResponseDTO findByNombreAndUniversidad(String nombre, Long universidadId) {
        Rol rol = rolRepository.findByNombreAndUniversidad(nombre, universidadId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + nombre));
        return rolMapper.toResponseDTO(rol);
    }

    public List<RolResponseDTO> findRolesSistema() {
        List<Rol> roles = rolRepository.findRolesSistema();
        return rolMapper.toResponseDTOList(roles);
    }

    public List<RolResponseDTO> findActiveByUniversidad(Long universidadId) {
        List<Rol> roles = rolRepository.findActiveByUniversidad(universidadId);
        return rolMapper.toResponseDTOList(roles);
    }

    @Transactional
    public RolResponseDTO create(RolRequestDTO requestDTO) {
        // Validar que no exista un rol con el mismo nombre
        if (rolRepository.existsByNombreAndUniversidad(requestDTO.getNombre(), requestDTO.getUniversidadId())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + requestDTO.getNombre());
        }

        Rol rol = rolMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.Universidad universidad = new upeu.edu.pe.academic.domain.entities.Universidad();
        universidad.setId(requestDTO.getUniversidadId());
        rol.setUniversidad(universidad);
        
        rolRepository.persist(rol);
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional
    public RolResponseDTO update(Long id, RolRequestDTO requestDTO) {
        Rol rol = rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));

        // Validar roles de sistema
        if (rol.getEsSistema() && !requestDTO.getEsSistema()) {
            throw new BusinessException("No se puede modificar un rol de sistema");
        }

        // Validar nombre duplicado (excepto el actual)
        rolRepository.findByNombreAndUniversidad(requestDTO.getNombre(), requestDTO.getUniversidadId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un rol con el nombre: " + requestDTO.getNombre());
                    }
                });

        rolMapper.updateEntityFromDTO(requestDTO, rol);
        rolRepository.persist(rol);
        
        return rolMapper.toResponseDTO(rol);
    }

    @Transactional
    public void delete(Long id) {
        Rol rol = rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));

        // Validar que no sea rol de sistema
        if (rol.getEsSistema()) {
            throw new BusinessException("No se puede eliminar un rol de sistema");
        }

        // Validar que no tenga usuarios asignados
        long cantidadUsuarios = rolRepository.countUsuariosConRol(id);
        if (cantidadUsuarios > 0) {
            throw new BusinessException("No se puede eliminar el rol porque tiene " + cantidadUsuarios + " usuario(s) asignado(s)");
        }

        rol.setActive(false);
        rolRepository.persist(rol);
    }

    public List<RolResponseDTO> findByPermisoNombre(String permisoNombre, Long universidadId) {
        List<Rol> roles = rolRepository.findByPermisoNombre(permisoNombre, universidadId);
        return rolMapper.toResponseDTOList(roles);
    }

    public boolean existsByNombre(String nombre, Long universidadId) {
        return rolRepository.existsByNombreAndUniversidad(nombre, universidadId);
    }

    public long countUsuariosConRol(Long rolId) {
        return rolRepository.countUsuariosConRol(rolId);
    }
}
