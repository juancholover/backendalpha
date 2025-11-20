package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.PermisoRequestDTO;
import upeu.edu.pe.security.application.dto.PermisoResponseDTO;
import upeu.edu.pe.security.application.mapper.PermisoMapper;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class PermisoService {

    @Inject
    PermisoRepository permisoRepository;

    @Inject
    PermisoMapper permisoMapper;

    public List<PermisoResponseDTO> findAll() {
        List<Permiso> permisos = permisoRepository.findAllActive();
        return permisoMapper.toResponseDTOList(permisos);
    }

    public PermisoResponseDTO findById(Long id) {
        Permiso permiso = permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + id));
        
        PermisoResponseDTO response = permisoMapper.toResponseDTO(permiso);
        response.setCantidadRoles(permiso.getRolPermisos() != null ? permiso.getRolPermisos().size() : 0);
        
        return response;
    }

    public PermisoResponseDTO findByNombreClave(String nombreClave) {
        Permiso permiso = permisoRepository.findByNombreClave(nombreClave)
                .orElseThrow(() -> new NotFoundException("Permiso no encontrado: " + nombreClave));
        return permisoMapper.toResponseDTO(permiso);
    }

    public List<PermisoResponseDTO> findByModulo(String modulo) {
        List<Permiso> permisos = permisoRepository.findByModulo(modulo);
        return permisoMapper.toResponseDTOList(permisos);
    }

    public List<PermisoResponseDTO> findByRecurso(String recurso) {
        List<Permiso> permisos = permisoRepository.findByRecurso(recurso);
        return permisoMapper.toResponseDTOList(permisos);
    }

    public List<PermisoResponseDTO> findByAccion(String accion) {
        List<Permiso> permisos = permisoRepository.findByAccion(accion);
        return permisoMapper.toResponseDTOList(permisos);
    }

    public List<PermisoResponseDTO> findByModuloAndRecurso(String modulo, String recurso) {
        List<Permiso> permisos = permisoRepository.findByModuloAndRecurso(modulo, recurso);
        return permisoMapper.toResponseDTOList(permisos);
    }

    public List<PermisoResponseDTO> findByRol(Long rolId) {
        List<Permiso> permisos = permisoRepository.findByRol(rolId);
        return permisoMapper.toResponseDTOList(permisos);
    }

    public List<PermisoResponseDTO> search(String query) {
        List<Permiso> permisos = permisoRepository.search(query);
        return permisoMapper.toResponseDTOList(permisos);
    }

    @Transactional
    public PermisoResponseDTO create(PermisoRequestDTO requestDTO) {
        Permiso permiso = permisoMapper.toEntity(requestDTO);
        
        // El nombreClave se genera automáticamente en @PrePersist
        // Validar que no exista
        if (permisoRepository.existsByNombreClave(permiso.getNombreClave())) {
            throw new BusinessException("Ya existe un permiso con esa combinación de módulo, acción y recurso");
        }

        permisoRepository.persist(permiso);
        return permisoMapper.toResponseDTO(permiso);
    }

    @Transactional
    public PermisoResponseDTO update(Long id, PermisoRequestDTO requestDTO) {
        Permiso permiso = permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + id));

        permisoMapper.updateEntityFromDTO(requestDTO, permiso);
        
        // Regenerar nombreClave con los nuevos valores
        permiso.generarNombreClave();
        
        // Validar que no exista otro permiso con el mismo nombreClave
        permisoRepository.findByNombreClave(permiso.getNombreClave())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un permiso con esa combinación");
                    }
                });

        permisoRepository.persist(permiso);
        return permisoMapper.toResponseDTO(permiso);
    }

    @Transactional
    public void delete(Long id) {
        Permiso permiso = permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + id));

        // Validar que no esté asignado a ningún rol
        if (permiso.getRolPermisos() != null && !permiso.getRolPermisos().isEmpty()) {
            throw new BusinessException("No se puede eliminar el permiso porque está asignado a " + 
                                       permiso.getRolPermisos().size() + " rol(es)");
        }

        // Soft delete
        permiso.setActive(false);
        permisoRepository.persist(permiso);
    }
}
