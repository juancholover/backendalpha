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
@Table(name = "tipo_autoridad", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class TipoAutoridad extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad; 

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre; // Ej: "Rector", "Decano", "Director General"

    @Column(name = "codigo", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo; // Ej: "RECTOR", "DECANO", "DIRECTOR" (para consultas)

    @Column(name = "nivel_jerarquia")
    private Integer nivelJerarquia; // 1=Rector, 2=Vicerrector, 3=Decano, etc.

    @Column(name = "descripcion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion; // Descripción del cargo
}
