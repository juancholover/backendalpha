package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.academic.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.academic.domain.entities.Matricula;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MatriculaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Matricula toEntity(MatriculaRequestDTO dto);

    @Mapping(target = "estudianteId", source = "estudiante.id")
    @Mapping(target = "estudianteNombre", source = "estudiante.persona.nombres")
    @Mapping(target = "estudianteApellido", source = "estudiante.persona.apellidoPaterno")
    @Mapping(target = "estudianteCodigo", source = "estudiante.codigoEstudiante")
    @Mapping(target = "seccionId", source = "cursoOfertado.id")
    @Mapping(target = "seccionCodigo", source = "cursoOfertado.codigoSeccion")
    @Mapping(target = "cursoId", source = "cursoOfertado.planAcademico.id")
    @Mapping(target = "cursoNombre", source = "cursoOfertado.planAcademico.nombre")
    @Mapping(target = "cursoCodigo", source = "cursoOfertado.planAcademico.codigo")
    @Mapping(target = "periodoAcademicoId", source = "cursoOfertado.periodoAcademico.id")
    @Mapping(target = "periodoAcademicoNombre", source = "cursoOfertado.periodoAcademico.nombre")
    @Mapping(target = "periodoAcademicoCodigo", source = "cursoOfertado.periodoAcademico.nombre")
    @Mapping(target = "profesorId", source = "cursoOfertado.profesor.id")
    @Mapping(target = "profesorNombre", source = "cursoOfertado.profesor.persona.nombres")
    @Mapping(target = "profesorApellido", source = "cursoOfertado.profesor.persona.apellidoPaterno")
    MatriculaResponseDTO toResponseDTO(Matricula entity);

    List<MatriculaResponseDTO> toResponseDTOList(List<Matricula> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(MatriculaRequestDTO dto, @MappingTarget Matricula entity);
}
