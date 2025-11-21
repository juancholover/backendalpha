package upeu.edu.pe.finance.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CuentaCorrienteAlumnoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "periodoAcademico", ignore = true)
    @Mapping(target = "numeroCuota", ignore = true)
    @Mapping(target = "montoPagado", constant = "0")
    @Mapping(target = "montoPendiente", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "pagosDetalle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    CuentaCorrienteAlumno toEntity(CuentaCorrienteAlumnoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "estudianteId", source = "estudiante.id")
    @Mapping(target = "estudianteNombre", expression = "java(getNombreEstudiante(entity))")
    @Mapping(target = "estudianteCodigo", source = "estudiante.codigoEstudiante")
    @Mapping(target = "numeroDocumento", source = "numeroCuota")
    @Mapping(target = "estaVencida", ignore = true)
    @Mapping(target = "cantidadPagos", ignore = true)
    CuentaCorrienteAlumnoResponseDTO toResponseDTO(CuentaCorrienteAlumno entity);

    List<CuentaCorrienteAlumnoResponseDTO> toResponseDTOList(List<CuentaCorrienteAlumno> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "periodoAcademico", ignore = true)
    @Mapping(target = "numeroCuota", ignore = true)
    @Mapping(target = "montoPagado", ignore = true)
    @Mapping(target = "montoPendiente", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "pagosDetalle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(CuentaCorrienteAlumnoRequestDTO dto, @MappingTarget CuentaCorrienteAlumno entity);
    
    default String getNombreEstudiante(CuentaCorrienteAlumno cuenta) {
        if (cuenta == null || cuenta.getEstudiante() == null || cuenta.getEstudiante().getPersona() == null) {
            return null;
        }
        var p = cuenta.getEstudiante().getPersona();
        return String.format("%s %s %s", 
            p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "",
            p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "",
            p.getNombres() != null ? p.getNombres() : ""
        ).trim();
    }
}
