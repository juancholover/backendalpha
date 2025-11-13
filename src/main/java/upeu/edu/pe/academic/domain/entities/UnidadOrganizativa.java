package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "unidad_organizativa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class UnidadOrganizativa extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id", nullable = false)
    private Localizacion localizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_unidad_id", nullable = false)
    private TipoUnidad tipoUnidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_padre_id")
    private UnidadOrganizativa unidadPadre; // Auto-referencia para jerarquía

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "codigo", unique = true, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "sigla", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String sigla;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "director_decano", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String directorDecano;

    @Column(name = "email", length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;
}
