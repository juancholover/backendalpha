package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UniversidadResponseDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String dominio;
    private String ruc;
    private String tipo; // PUBLICA, PRIVADA, CONSORCIO
    private String website;
    private String logoUrl;
    
    private String zonaHoraria;
    private String locale;
    private String configuracion;

    private String plan; // FREE, BASIC, PREMIUM, ENTERPRISE
    private String estado; // ACTIVA, SUSPENDIDA, TRIAL, VENCIDA
    private java.time.LocalDate fechaVencimiento;
    private Integer maxEstudiantes;
    private Integer maxDocentes;
    private Integer totalEstudiantes;
    private Integer totalDocentes;

    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
