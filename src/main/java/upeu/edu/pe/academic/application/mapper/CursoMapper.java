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
    @Mapping(target = "planAcademico", ignore = true) // Se asigna manualmente en el service
    @Mapping(target = "prerequisito", ignore = true) // Se asigna manualmente en el service
    Curso toEntity(CursoRequestDTO dto);

    /**
     * Actualiza una entidad Curso existente desde CursoRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "planAcademico", ignore = true)
    @Mapping(target = "prerequisito", ignore = true)
    void updateEntityFromDto(CursoRequestDTO dto, @MappingTarget Curso entity);

    /**
     * Convierte Curso a CursoResponseDTO
     */
    @Mapping(target = "planAcademicoId", source = "planAcademico.id")
    @Mapping(target = "planAcademicoCodigo", source = "planAcademico.codigo")
    @Mapping(target = "planAcademicoNombre", source = "planAcademico.nombre")
    @Mapping(target = "programaAcademicoNombre", source = "planAcademico.programaAcademico.nombre")
    @Mapping(target = "prerequisitoId", source = "prerequisito.id")
    @Mapping(target = "prerequisitoNombre", source = "prerequisito.nombre")
    @Mapping(target = "prerequisitoCodigo", source = "prerequisito.codigoCurso")
    CursoResponseDTO toResponseDTO(Curso entity);
}
