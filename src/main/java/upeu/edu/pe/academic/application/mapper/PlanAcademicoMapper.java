package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.PlanAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PlanAcademicoResponseDTO;
import upeu.edu.pe.academic.domain.entities.PlanAcademico;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PlanAcademicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "programaAcademico", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    PlanAcademico toEntity(PlanAcademicoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "programaAcademicoId", source = "programaAcademico.id")
    @Mapping(target = "programaAcademicoCodigo", source = "programaAcademico.codigo")
    @Mapping(target = "programaAcademicoNombre", source = "programaAcademico.nombre")
    PlanAcademicoResponseDTO toResponseDTO(PlanAcademico entity);

    List<PlanAcademicoResponseDTO> toResponseDTOList(List<PlanAcademico> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "programaAcademico", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(PlanAcademicoRequestDTO dto, @MappingTarget PlanAcademico entity);
}
