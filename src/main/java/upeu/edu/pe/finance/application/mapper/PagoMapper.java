package upeu.edu.pe.finance.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.domain.entities.Pago;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PagoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "montoAplicado", constant = "0")
    @Mapping(target = "montoPendienteAplicar", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE_APLICAR")
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "pagosDetalle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Pago toEntity(PagoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "estudianteId", source = "estudiante.id")
    @Mapping(target = "estudianteNombre", expression = "java(getNombreEstudiante(entity))")
    @Mapping(target = "estudianteCodigo", source = "estudiante.codigoEstudiante")
    @Mapping(target = "cantidadAplicaciones", ignore = true)
    PagoResponseDTO toResponseDTO(Pago entity);

    List<PagoResponseDTO> toResponseDTOList(List<Pago> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "montoAplicado", ignore = true)
    @Mapping(target = "montoPendienteAplicar", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "pagosDetalle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(PagoRequestDTO dto, @MappingTarget Pago entity);

    default String getNombreEstudiante(Pago pago) {
        if (pago == null || pago.getEstudiante() == null || pago.getEstudiante().getPersona() == null) {
            return null;
        }
        var p = pago.getEstudiante().getPersona();
        return String.format("%s %s %s", 
            p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "",
            p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "",
            p.getNombres() != null ? p.getNombres() : ""
        ).trim();
    }
}
