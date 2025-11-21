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
    private Long localizacionPrincipalId;
    private String localizacionPrincipalNombre; // Denormalizado
    private String zonaHoraria;
    private String locale;
    private String configuracion;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
