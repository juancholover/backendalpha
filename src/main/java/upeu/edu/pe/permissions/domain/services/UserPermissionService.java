package upeu.edu.pe.permissions.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.permissions.application.dto.PermissionDTOs.*;
import upeu.edu.pe.permissions.domain.entities.MenuItem;
import upeu.edu.pe.permissions.domain.entities.UsuarioMenu;
import upeu.edu.pe.permissions.domain.repositories.MenuItemRepository;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.security.casbin.CasbinPolicyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para administración de permisos individuales de usuarios.
 */
@ApplicationScoped
public class UserPermissionService {

    @Inject
    MenuItemRepository menuItemRepository;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    CasbinPolicyService casbinPolicyService;

    @Inject
    jakarta.persistence.EntityManager em;

    /**
     * Buscar usuarios por nombre, email o teléfono.
     */
    /**
     * Buscar usuarios por nombre, email o teléfono.
     */
    public List<UsuarioPermisosDTO> searchUsuarios(String query) {
        String searchTerm = "%" + query.toLowerCase() + "%";

        List<Persona> personas = personaRepository.getEntityManager()
                .createQuery("""
                        SELECT p FROM Persona p
                        WHERE (LOWER(p.nombres) LIKE :term
                        OR LOWER(p.apellidoPaterno) LIKE :term
                        OR LOWER(p.apellidoMaterno) LIKE :term
                        OR LOWER(p.email) LIKE :term
                        OR p.telefono LIKE :term)
                        AND p.active = true
                        ORDER BY p.apellidoPaterno, p.nombres
                        """, Persona.class)
                .setParameter("term", searchTerm)
                .setMaxResults(20)
                .getResultList();

        return personas.stream()
                .map(p -> new UsuarioPermisosDTO(
                        p.getEmail(),
                        getNombreCompleto(p),
                        casbinPolicyService.getUserRoles(p.getEmail()),
                        getPermisosIndividuales(p.getEmail())))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene permisos individuales de un usuario.
     */
    public List<PermisoIndividualDTO> getPermisosIndividuales(String email) {
        List<UsuarioMenu> permisos = em.createQuery("""
                SELECT um FROM UsuarioMenu um
                JOIN FETCH um.menuItem mi
                LEFT JOIN FETCH mi.padre
                WHERE um.usuarioEmail = :email
                AND um.active = true
                AND (um.fechaExpiracion IS NULL OR um.fechaExpiracion > CURRENT_TIMESTAMP)
                """, UsuarioMenu.class)
                .setParameter("email", email)
                .getResultList();

        return permisos.stream()
                .map(um -> new PermisoIndividualDTO(
                        um.getId(),
                        um.getMenuItem().getId(),
                        um.getMenuItem().getCodigo(),
                        um.getMenuItem().getNombre(),
                        um.getMenuItem().getPadre() != null
                                ? um.getMenuItem().getPadre().getNombre()
                                : "Módulo Principal",
                        um.getOtorgadoPor(),
                        um.getRazon()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los permisos de un usuario (email).
     */
    public UsuarioPermisosDTO getUsuarioPermisos(String email) {
        Persona persona = personaRepository.findByEmail(email).orElse(null);
        String nombreCompleto = persona != null ? getNombreCompleto(persona) : email;

        return new UsuarioPermisosDTO(
                email,
                nombreCompleto,
                casbinPolicyService.getUserRoles(email),
                getPermisosIndividuales(email));
    }

    private String getNombreCompleto(Persona p) {
        StringBuilder sb = new StringBuilder();
        if (p.getNombres() != null)
            sb.append(p.getNombres()).append(" ");
        if (p.getApellidoPaterno() != null)
            sb.append(p.getApellidoPaterno()).append(" ");
        if (p.getApellidoMaterno() != null)
            sb.append(p.getApellidoMaterno());
        return sb.toString().trim();
    }

    /**
     * Asigna un permiso individual (menú/target) a un usuario.
     */
    @Transactional
    public void assignPermisoIndividual(String email, Long menuItemId, String razon, String otorgadoPor) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId);
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem no encontrado: " + menuItemId);
        }

        // Verificar si ya existe
        Long count = em.createQuery("""
                SELECT COUNT(um) FROM UsuarioMenu um
                WHERE um.usuarioEmail = :email
                AND um.menuItem.id = :menuItemId
                AND um.active = true
                """, Long.class)
                .setParameter("email", email)
                .setParameter("menuItemId", menuItemId)
                .getSingleResult();

        if (count > 0) {
            throw new IllegalStateException("El usuario ya tiene este permiso asignado");
        }

        UsuarioMenu usuarioMenu = new UsuarioMenu();
        usuarioMenu.setUsuarioEmail(email);
        usuarioMenu.setMenuItem(menuItem);
        usuarioMenu.setOtorgadoPor(otorgadoPor);
        usuarioMenu.setFechaOtorgamiento(LocalDateTime.now());
        usuarioMenu.setRazon(razon);
        usuarioMenu.setActive(true);

        em.persist(usuarioMenu);
    }

    /**
     * Quita un permiso individual de un usuario.
     */
    @Transactional
    public void removePermisoIndividual(Long permisoId) {
        UsuarioMenu um = em.find(UsuarioMenu.class, permisoId);
        if (um != null) {
            um.setActive(false);
            em.merge(um);
        }
    }

    /**
     * Asigna un módulo completo (todos sus targets) a un usuario.
     */
    @Transactional
    public void assignModuloToUsuario(String email, Long moduloId, String razon, String otorgadoPor) {
        List<MenuItem> targets = menuItemRepository.findHijosByPadreId(moduloId);

        for (MenuItem target : targets) {
            try {
                assignPermisoIndividual(email, target.getId(), razon, otorgadoPor);
            } catch (IllegalStateException e) {
                // Ya tiene el permiso, continuar
            }
        }
    }
}
