package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando el grado académico no es válido para la categoría
 * docente.
 */
public class GradoAcademicoInvalidoException extends BusinessException {

    private final String gradoAcademico;
    private final String categoriaDocente;

    public GradoAcademicoInvalidoException(String gradoAcademico, String categoriaDocente) {
        super(String.format("El grado académico '%s' no es válido para la categoría docente '%s'",
                gradoAcademico, categoriaDocente));
        this.gradoAcademico = gradoAcademico;
        this.categoriaDocente = categoriaDocente;
    }

    public String getGradoAcademico() {
        return gradoAcademico;
    }

    public String getCategoriaDocente() {
        return categoriaDocente;
    }
}
