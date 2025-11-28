package upeu.edu.pe.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Dni implements Serializable {

    private String value;

    public Dni(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        if (!value.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos: " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
