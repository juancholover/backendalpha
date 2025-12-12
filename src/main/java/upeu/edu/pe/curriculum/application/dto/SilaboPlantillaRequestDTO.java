package upeu.edu.pe.curriculum.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SilaboPlantillaRequestDTO {
    
    @NotNull(message = "El ID del sílabo es obligatorio")
    private Long silaboId;
    
    private LocalDate fechaInicioVigencia;
    
    private LocalDate fechaFinVigencia;
    
    @NotBlank(message = "El nivel de flexibilidad es obligatorio")
    private String nivelFlexibilidad = "FECHAS_Y_PONDERACION"; // SOLO_FECHAS, FECHAS_Y_PONDERACION, SIN_MODIFICACION
    
    private String notas;
}
