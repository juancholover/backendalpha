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
@Table(name = "tipo_unidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class TipoUnidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nombre; // FACULTAD, ESCUELA, DEPARTAMENTO, INSTITUTO, etc.

    @Column(name = "descripcion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "nivel")
    private Integer nivel; // 1=Facultad, 2=Escuela, 3=Departamento
}
