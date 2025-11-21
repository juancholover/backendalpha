package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaResponseDTO;
import upeu.edu.pe.academic.domain.entities.EvaluacionNota;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EvaluacionNotaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "criterio", ignore = true)
    @Mapping(target = "notaFinal", ignore = true)
    @Mapping(target = "fechaCalificacion", ignore = true)
    @Mapping(target = "fechaEvaluacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "observacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    EvaluacionNota toEntity(EvaluacionNotaRequestDTO dto);

    @Mapping(target = "matriculaId", source = "matricula.id")
    @Mapping(target = "estudianteId", source = "matricula.estudiante.id")
    @Mapping(target = "estudianteNombre", expression = "java(getNombreCompleto(entity))")
    @Mapping(target = "estudianteCodigo", source = "matricula.estudiante.codigoEstudiante")
    @Mapping(target = "criterioId", source = "criterio.id")
    @Mapping(target = "criterioNombre", source = "criterio.nombre")
    @Mapping(target = "criterioPeso", source = "criterio.peso")
    @Mapping(target = "observaciones", source = "observacion")
    @Mapping(target = "calificadoPor", source = "createdBy")
    @Mapping(target = "estaAprobado", ignore = true)
    EvaluacionNotaResponseDTO toResponseDTO(EvaluacionNota entity);

    List<EvaluacionNotaResponseDTO> toResponseDTOList(List<EvaluacionNota> entities);
    
    default String getNombreCompleto(EvaluacionNota nota) {
        if (nota == null || nota.getMatricula() == null || nota.getMatricula().getEstudiante() == null || nota.getMatricula().getEstudiante().getPersona() == null) {
            return null;
        }
        var persona = nota.getMatricula().getEstudiante().getPersona();
        return String.format("%s %s %s", 
            persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "",
            persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "",
            persona.getNombres() != null ? persona.getNombres() : ""
        ).trim();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    @Mapping(target = "criterio", ignore = true)
    @Mapping(target = "notaFinal", ignore = true)
    @Mapping(target = "fechaCalificacion", ignore = true)
    @Mapping(target = "fechaEvaluacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "observacion", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(EvaluacionNotaRequestDTO dto, @MappingTarget EvaluacionNota entity);
}
