package upeu.edu.pe.permissions.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuItem - Tabla jerárquica para Islas y Sidebar-Targets.
 * - id_padre = NULL → ISLA (se lista tras login)
 * - id_padre = isla.id → SIDEBAR-TARGET (opción del sidebar)
 */
@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class MenuItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padre")
    private MenuItem padre;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ruta_frontend", length = 255)
    private String rutaFrontend;

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "orden")
    private Integer orden = 0;

    // Hijos (sidebar-targets si este es una isla)
    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MenuItem> hijos = new ArrayList<>();

    // APIs asociadas a este menú
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApiPermiso> apis = new ArrayList<>();

    // Roles que tienen acceso a este menú
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RolMenu> roles = new ArrayList<>();

    /**
     * ¿Es una ISLA? (sin padre)
     */
    public boolean esIsla() {
        return padre == null;
    }

    /**
     * ¿Es un SIDEBAR-TARGET? (con padre)
     */
    public boolean esSidebarTarget() {
        return padre != null;
    }
}
