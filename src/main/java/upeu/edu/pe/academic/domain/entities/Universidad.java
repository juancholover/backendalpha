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
@Table(name = "universidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Universidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "sigla", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String sigla;

    @Column(name = "ruc", unique = true, length = 11)
    private String ruc;

    @Column(name = "tipo", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipo; // PUBLICA, PRIVADA

    @Column(name = "direccion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String email;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "rector", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String rector;
}
