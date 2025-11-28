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
public class Ruc implements Serializable {

    private String value;

    public Ruc(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC no puede estar vacío");
        }
        if (!value.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener exactamente 11 dígitos numéricos: " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
