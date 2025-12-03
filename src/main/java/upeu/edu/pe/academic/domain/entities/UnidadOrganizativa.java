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
@Table(name = "unidad_organizativa", uniqueConstraints = {
    // Código debe ser único
    @UniqueConstraint(columnNames = {"codigo"}), 
    // Añadimos el nombre para evitar duplicidad de nombres de Facultades/Escuelas
    @UniqueConstraint(columnNames = {"nombre"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class UnidadOrganizativa extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id")
    private Localizacion localizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_unidad_id", nullable = false)
    private TipoUnidad tipoUnidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_padre_id")
    private UnidadOrganizativa unidadPadre; 

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "codigo", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "sigla", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String sigla;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;
}