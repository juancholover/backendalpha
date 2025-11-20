package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.RolPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.RolPermisoResponseDTO;
import upeu.edu.pe.security.application.mapper.RolPermisoMapper;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class RolPermisoService {

    @Inject
    RolPermisoRepository rolPermisoRepository;

    @Inject
    RolRepository rolRepository;

    @Inject
    PermisoRepository permisoRepository;

    @Inject
    RolPermisoMapper rolPermisoMapper;

    public List<RolPermisoResponseDTO> findByRol(Long rolId) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByRol(rolId);
        return rolPermisoMapper.toResponseDTOList(rolPermisos);
    }

    public List<RolPermisoResponseDTO> findByPermiso(Long permisoId) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByPermiso(permisoId);
        return rolPermisoMapper.toResponseDTOList(rolPermisos);
    }

    public List<RolPermisoResponseDTO> findDelegablesByRol(Long rolId) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findDelegablesByRol(rolId);
        return rolPermisoMapper.toResponseDTOList(rolPermisos);
    }

    public List<RolPermisoResponseDTO> findByRolAndModulo(Long rolId, String modulo) {
        List<RolPermiso> rolPermisos = rolPermisoRepository.findByRolAndModulo(rolId, modulo);
        return rolPermisoMapper.toResponseDTOList(rolPermisos);
    }

    @Transactional
    public RolPermisoResponseDTO assignPermiso(RolPermisoRequestDTO requestDTO) {
        // Validar que el rol existe
        Rol rol = rolRepository.findByIdOptional(requestDTO.getRolId())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + requestDTO.getRolId()));

        // Validar que el permiso existe
        Permiso permiso = permisoRepository.findByIdOptional(requestDTO.getPermisoId())
                .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + requestDTO.getPermisoId()));

        // Validar que no exista la asignación
        if (rolPermisoRepository.existsByRolAndPermiso(requestDTO.getRolId(), requestDTO.getPermisoId())) {
            throw new BusinessException("El permiso ya está asignado a este rol");
        }

        RolPermiso rolPermiso = rolPermisoMapper.toEntity(requestDTO);
        rolPermiso.setRol(rol);
        rolPermiso.setPermiso(permiso);

        rolPermisoRepository.persist(rolPermiso);
        return rolPermisoMapper.toResponseDTO(rolPermiso);
    }

    @Transactional
    public void removePermiso(Long rolId, Long permisoId) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermisoRepository.delete(rolPermiso);
    }

    @Transactional
    public void removeAllPermisosByRol(Long rolId) {
        // Validar que el rol existe
        rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + rolId));

        long count = rolPermisoRepository.deleteByRol(rolId);
        
        if (count == 0) {
            throw new NotFoundException("No se encontraron permisos asignados al rol");
        }
    }

    @Transactional
    public RolPermisoResponseDTO updateRestriccion(Long rolId, Long permisoId, String restriccion) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermiso.setRestriccion(restriccion);
        rolPermisoRepository.persist(rolPermiso);

        return rolPermisoMapper.toResponseDTO(rolPermiso);
    }

    @Transactional
    public RolPermisoResponseDTO toggleDelegacion(Long rolId, Long permisoId) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermiso.setPuedeDeleagar(!rolPermiso.getPuedeDeleagar());
        rolPermisoRepository.persist(rolPermiso);

        return rolPermisoMapper.toResponseDTO(rolPermiso);
    }

    public long countByRol(Long rolId) {
        return rolPermisoRepository.countByRol(rolId);
    }

    @Transactional
    public void assignMultiplePermisos(Long rolId, List<Long> permisoIds) {
        // Validar que el rol existe
        Rol rol = rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + rolId));

        for (Long permisoId : permisoIds) {
            // Validar que el permiso existe
            Permiso permiso = permisoRepository.findByIdOptional(permisoId)
                    .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + permisoId));

            // Solo asignar si no existe
            if (!rolPermisoRepository.existsByRolAndPermiso(rolId, permisoId)) {
                RolPermiso rolPermiso = new RolPermiso();
                rolPermiso.setRol(rol);
                rolPermiso.setPermiso(permiso);
                rolPermiso.setPuedeDeleagar(false);
                rolPermisoRepository.persist(rolPermiso);
            }
        }
    }

    @Transactional
    public void removeMultiplePermisos(Long rolId, List<Long> permisoIds) {
        // Validar que el rol existe
        rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + rolId));

        for (Long permisoId : permisoIds) {
            rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                    .ifPresent(rolPermisoRepository::delete);
        }
    }

    public boolean existsByRolAndPermiso(Long rolId, Long permisoId) {
        return rolPermisoRepository.existsByRolAndPermiso(rolId, permisoId);
    }
}
