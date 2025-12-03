package upeu.edu.pe.academic.domain.commands;

import java.util.Objects;


public record CrearUniversidadCommand(
    String codigo,
    String nombre,
    String ruc,
    String tipo,
    String plan,
    String dominio,
    String website,
    Integer maxEstudiantes,
    Integer maxDocentes
) {

    public CrearUniversidadCommand {
        Objects.requireNonNull(codigo, "Código es requerido");
        Objects.requireNonNull(nombre, "Nombre es requerido");
        Objects.requireNonNull(ruc, "RUC es requerido");
        
        if (codigo.isBlank()) {
            throw new IllegalArgumentException("Código no puede estar vacío");
        }
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (ruc.isBlank()) {
            throw new IllegalArgumentException("RUC no puede estar vacío");
        }
    }
}
