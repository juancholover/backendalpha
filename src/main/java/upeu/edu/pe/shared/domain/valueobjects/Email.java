package upeu.edu.pe.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Email implements Serializable {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    private String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
