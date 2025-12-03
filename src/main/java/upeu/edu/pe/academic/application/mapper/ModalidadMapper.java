package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.ModalidadRequestDTO;
import upeu.edu.pe.academic.application.dto.ModalidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.Modalidad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModalidadMapper {

    /**
     * Convierte ModalidadRequestDTO a Modalidad (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    Modalidad toEntity(ModalidadRequestDTO dto);

    /**
     * Actualiza una entidad Modalidad existente desde DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    void updateEntityFromDto(ModalidadRequestDTO dto, @MappingTarget Modalidad entity);

    /**
     * Convierte Modalidad a ModalidadResponseDTO
     */
    @Mapping(target = "universidadId", source = "universidad.id")
    ModalidadResponseDTO toResponseDTO(Modalidad entity);
}
