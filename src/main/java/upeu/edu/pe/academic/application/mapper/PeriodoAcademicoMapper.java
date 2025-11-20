package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PeriodoAcademicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "cursosOfertados", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    PeriodoAcademico toEntity(PeriodoAcademicoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "cantidadSecciones", ignore = true)
    @Mapping(target = "cantidadMatriculas", ignore = true)
    PeriodoAcademicoResponseDTO toResponseDTO(PeriodoAcademico entity);

    List<PeriodoAcademicoResponseDTO> toResponseDTOList(List<PeriodoAcademico> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "cursosOfertados", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(PeriodoAcademicoRequestDTO dto, @MappingTarget PeriodoAcademico entity);
}
