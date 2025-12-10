package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.curriculum.application.dto.PlanCursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.PlanCursoResponseDTO;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PlanCursoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planAcademico", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    PlanCurso toEntity(PlanCursoRequestDTO dto);

    @Mapping(target = "planAcademicoId", source = "planAcademico.id")
    @Mapping(target = "planAcademicoCodigo", source = "planAcademico.codigo")
    @Mapping(target = "planAcademicoNombre", source = "planAcademico.nombre")
    @Mapping(target = "planAcademicoVersion", source = "planAcademico.version")
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "cursoCodigo", source = "curso.codigoCurso")
    @Mapping(target = "cursoNombre", source = "curso.nombre")
    PlanCursoResponseDTO toResponseDTO(PlanCurso entity);

    List<PlanCursoResponseDTO> toResponseDTOList(List<PlanCurso> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planAcademico", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(PlanCursoRequestDTO dto, @MappingTarget PlanCurso entity);
}

