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
@Table(name = "tipo_localizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class TipoLocalizacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nombre; // SEDE, FILIAL, ANEXO, CAMPUS

    @Column(name = "descripcion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;
}
