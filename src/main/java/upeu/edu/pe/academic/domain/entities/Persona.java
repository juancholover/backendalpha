package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;
import upeu.edu.pe.shared.domain.valueobjects.Email;

import java.time.LocalDate;

@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "numero_documento", "universidad_id" }),
        @UniqueConstraint(columnNames = { "email", "universidad_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Persona extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "nombres", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String apellidoMaterno;

    @Column(name = "tipo_documento", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoDocumento; // DNI, CE, PASAPORTE

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String genero;

    @Column(name = "estado_civil", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoCivil;

    @Column(name = "direccion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String direccion;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "celular", length = 15)
    private String celular;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, length = 100))
    })
    private Email email;

    // ...

    // Factory Method
    public static Persona crear(Universidad universidad, String nombres, String apellidoPaterno, String apellidoMaterno,
            String tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento,
            String genero, String estadoCivil, String direccion, String telefono,
            String celular, String emailValue, String fotoUrl) {
        Persona persona = new Persona();
        persona.setUniversidad(universidad);
        persona.setNombres(nombres);
        persona.setApellidoPaterno(apellidoPaterno);
        persona.setApellidoMaterno(apellidoMaterno);
        persona.setTipoDocumento(tipoDocumento);
        persona.setNumeroDocumento(numeroDocumento);
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setGenero(genero);
        persona.setEstadoCivil(estadoCivil);
        persona.setDireccion(direccion);
        persona.setTelefono(telefono);
        persona.setCelular(celular);
        if (emailValue != null && !emailValue.isEmpty()) {
            persona.setEmail(new Email(emailValue));
        }
        persona.setFotoUrl(fotoUrl);
        persona.setActive(true); // Default active
        return persona;
    }

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

}
