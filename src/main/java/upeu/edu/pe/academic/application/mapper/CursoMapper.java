package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.CursoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoResponseDTO;
import upeu.edu.pe.academic.domain.entities.Curso;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CursoMapper {

    /**
     * Convierte CursoRequestDTO a Curso (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "planAcademico", ignore = true) 
    Curso toEntity(CursoRequestDTO dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "planAcademico", ignore = true)
    void updateEntityFromDto(CursoRequestDTO dto, @MappingTarget Curso entity);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "planAcademicoId", source = "planAcademico.id")
    @Mapping(target = "planAcademicoCodigo", source = "planAcademico.codigo")
    @Mapping(target = "planAcademicoNombre", source = "planAcademico.nombre")
    @Mapping(target = "programaAcademicoNombre", source = "planAcademico.programaAcademico.nombre")
    CursoResponseDTO toResponseDTO(Curso entity);
}
