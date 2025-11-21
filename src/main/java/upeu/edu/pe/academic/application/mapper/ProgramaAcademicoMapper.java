package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoResponseDTO;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProgramaAcademicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "unidadOrganizativa", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    ProgramaAcademico toEntity(ProgramaAcademicoRequestDTO dto);

    @Mapping(target = "unidadOrganizativaId", source = "unidadOrganizativa.id")
    @Mapping(target = "unidadOrganizativaNombre", source = "unidadOrganizativa.nombre")
    @Mapping(target = "unidadOrganizativaCodigo", source = "unidadOrganizativa.codigo")
    ProgramaAcademicoResponseDTO toResponseDTO(ProgramaAcademico entity);

    List<ProgramaAcademicoResponseDTO> toResponseDTOList(List<ProgramaAcademico> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "unidadOrganizativa", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(ProgramaAcademicoRequestDTO dto, @MappingTarget ProgramaAcademico entity);
}
