package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.AuthUsuarioPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.AuthUsuarioPermisoResponseDTO;
import upeu.edu.pe.security.application.mapper.AuthUsuarioPermisoMapper;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.AuthUsuarioPermiso;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioPermisoRepository;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthUsuarioPermisoService {

    @Inject
    AuthUsuarioPermisoRepository repository;

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    @Inject
    PermisoRepository permisoRepository;

    @Inject
    AuthUsuarioPermisoMapper mapper;

    /**
     * Asigna un permiso individual a un usuario
     */
    @Transactional
    public AuthUsuarioPermisoResponseDTO asignarPermiso(AuthUsuarioPermisoRequestDTO dto, Long asignadoPorId) {
        // Validar que el usuario existe
        AuthUsuario usuario = authUsuarioRepository.findByIdOptional(dto.authUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.authUsuarioId()));

        // Validar que el permiso existe
        Permiso permiso = permisoRepository.findByIdOptional(dto.permisoId())
            .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con ID: " + dto.permisoId()));

        // Validar que no exista ya la asignación
        if (repository.existeAsignacion(dto.authUsuarioId(), dto.permisoId())) {
            throw new IllegalStateException("El usuario ya tiene asignado este permiso individualmente");
        }

        // Obtener el usuario que asigna
        AuthUsuario asignadoPor = authUsuarioRepository.findByIdOptional(asignadoPorId)
            .orElse(null);

        // Crear la asignación
        AuthUsuarioPermiso asignacion = new AuthUsuarioPermiso();
        asignacion.setAuthUsuario(usuario);
        asignacion.setPermiso(permiso);
        asignacion.setMotivo(dto.motivo());
        asignacion.setFechaExpiracion(dto.fechaExpiracion());
        asignacion.setAsignadoPor(asignadoPor);
        asignacion.setEsAdicion(dto.esAdicion() != null ? dto.esAdicion() : true);

        repository.persist(asignacion);
        return mapper.toDto(asignacion);
    }

    /**
     * Obtiene todos los permisos individuales de un usuario
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosPorUsuario(Long authUsuarioId) {
        return repository.findByUsuarioId(authUsuarioId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene solo los permisos individuales vigentes de un usuario
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosVigentes(Long authUsuarioId) {
        return repository.findPermisosVigentes(authUsuarioId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene permisos individuales expirados de un usuario
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosExpirados(Long authUsuarioId) {
        return repository.findPermisosExpirados(authUsuarioId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Verifica si un usuario tiene un permiso individual específico vigente
     */
    public boolean usuarioTienePermisoVigente(Long authUsuarioId, String codigoPermiso) {
        return repository.usuarioTienePermisoVigenteByCodigo(authUsuarioId, codigoPermiso);
    }

    /**
     * Obtiene todos los permisos asignados por un usuario específico
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosAsignadosPor(Long asignadoPorId) {
        return repository.findByAsignadoPor(asignadoPorId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene permisos que expiran en los próximos N días
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosPorExpirar(int diasAnticipacion) {
        return repository.findPorExpirar(diasAnticipacion)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene permisos temporales de un usuario
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosTemporales(Long authUsuarioId) {
        return repository.findPermisosTemporales(authUsuarioId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene permisos permanentes de un usuario
     */
    public List<AuthUsuarioPermisoResponseDTO> obtenerPermisosPermanentes(Long authUsuarioId) {
        return repository.findPermisosPermanentes(authUsuarioId)
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Revoca un permiso individual específico de un usuario
     */
    @Transactional
    public boolean revocarPermiso(Long authUsuarioId, Long permisoId) {
        long deleted = repository.revocarPermiso(authUsuarioId, permisoId);
        return deleted > 0;
    }

    /**
     * Revoca todos los permisos individuales de un usuario
     */
    @Transactional
    public long revocarTodosLosPermisos(Long authUsuarioId) {
        return repository.revocarTodosLosPermisos(authUsuarioId);
    }

    /**
     * Limpia permisos expirados del sistema (tarea de mantenimiento)
     */
    @Transactional
    public long limpiarPermisosExpirados() {
        return repository.eliminarPermisosExpirados();
    }

    /**
     * Extiende la fecha de expiración de un permiso temporal
     */
    @Transactional
    public AuthUsuarioPermisoResponseDTO extenderExpiracion(Long permisoUsuarioId, LocalDateTime nuevaFechaExpiracion) {
        AuthUsuarioPermiso permiso = repository.findByIdOptional(permisoUsuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Permiso individual no encontrado con ID: " + permisoUsuarioId));

        permiso.setFechaExpiracion(nuevaFechaExpiracion);
        repository.persist(permiso);
        return mapper.toDto(permiso);
    }

    /**
     * Convierte un permiso temporal en permanente
     */
    @Transactional
    public AuthUsuarioPermisoResponseDTO convertirAPermanente(Long permisoUsuarioId) {
        AuthUsuarioPermiso permiso = repository.findByIdOptional(permisoUsuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Permiso individual no encontrado con ID: " + permisoUsuarioId));

        permiso.setFechaExpiracion(null);
        repository.persist(permiso);
        return mapper.toDto(permiso);
    }

    /**
     * Cuenta permisos individuales vigentes por usuario
     */
    public long contarPermisosVigentes(Long authUsuarioId) {
        return repository.countPermisosVigentes(authUsuarioId);
    }

    /**
     * Obtiene el ID de un usuario por su username
     */
    public Long obtenerIdUsuarioPorUsername(String username) {
        return authUsuarioRepository.findByUsername(username)
            .map(AuthUsuario::getId)
            .orElse(null);
    }
}
