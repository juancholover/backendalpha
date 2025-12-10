package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.curriculum.application.dto.CursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.CursoResponseDTO;
import upeu.edu.pe.curriculum.domain.entities.Curso;

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
    Curso toEntity(CursoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(CursoRequestDTO dto, @MappingTarget Curso entity);

    CursoResponseDTO toResponseDTO(Curso entity);
}

