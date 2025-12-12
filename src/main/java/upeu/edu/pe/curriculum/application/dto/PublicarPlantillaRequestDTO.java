package upeu.edu.pe.curriculum.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PublicarPlantillaRequestDTO {
    
    @NotNull(message = "El ID de la plantilla es obligatorio")
    private Long plantillaId;
    
    @NotEmpty(message = "Debe especificar al menos un campus")
    private List<Long> localizacionesIds;
    
    @NotBlank(message = "El año académico es obligatorio")
    private String anioAcademico;
    
    private LocalDate fechaInicioCampus;
    
    private LocalDate fechaFinCampus;
}
