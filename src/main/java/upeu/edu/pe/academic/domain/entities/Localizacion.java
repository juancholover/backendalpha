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
@Table(name = "localizacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Localizacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_localizacion_id", nullable = false)
    private TipoLocalizacion tipoLocalizacion;
    
    // Relación recursiva para jerarquías (ej: Edificio -> Aula)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_padre_id")
    private Localizacion localizacionPadre;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "codigo", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "direccion", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String direccion; // Incluye información geográfica completa (departamento, provincia, distrito, calle, número)

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String email;
}
