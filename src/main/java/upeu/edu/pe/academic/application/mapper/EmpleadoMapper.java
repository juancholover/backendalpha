package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.EmpleadoRequestDTO;
import upeu.edu.pe.academic.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.academic.domain.entities.Empleado;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "cdi")
public interface EmpleadoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "unidadOrganizativa", source = "unidadOrganizativaId", qualifiedByName = "mapUnidadOrganizativa")
    Empleado toEntity(EmpleadoRequestDTO dto);

    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "nombreCompleto", expression = "java(getNombreCompleto(entity))")
    @Mapping(target = "unidadOrganizativaId", source = "unidadOrganizativa.id")
    @Mapping(target = "unidadOrganizativaNombre", source = "unidadOrganizativa.nombre")
    @Mapping(target = "aniosServicio", expression = "java(calculateAniosServicio(entity))")
    EmpleadoResponseDTO toResponseDTO(Empleado entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "unidadOrganizativa", source = "unidadOrganizativaId", qualifiedByName = "mapUnidadOrganizativa")
    void updateEntityFromDto(EmpleadoRequestDTO dto, @MappingTarget Empleado entity);

    /**
     * Convierte ID de unidad organizativa a entidad UnidadOrganizativa
     */
    @Named("mapUnidadOrganizativa")
    default UnidadOrganizativa mapUnidadOrganizativa(Long unidadOrganizativaId) {
        if (unidadOrganizativaId == null) {
            return null;
        }
        UnidadOrganizativa unidad = new UnidadOrganizativa();
        unidad.setId(unidadOrganizativaId);
        return unidad;
    }

    /**
     * Obtiene el nombre completo de la persona
     */
    default String getNombreCompleto(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) {
            return null;
        }
        Persona p = empleado.getPersona();
        return String.format("%s %s %s", 
            p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "", 
            p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "", 
            p.getNombres() != null ? p.getNombres() : ""
        ).trim();
    }

    /**
     * Calcula los años de servicio desde la fecha de ingreso
     */
    default Integer calculateAniosServicio(Empleado empleado) {
        if (empleado == null || empleado.getFechaIngreso() == null) {
            return null;
        }
        
        LocalDate fechaFin = empleado.getFechaCese() != null 
            ? empleado.getFechaCese() 
            : LocalDate.now();
            
        Period period = Period.between(empleado.getFechaIngreso(), fechaFin);
        return period.getYears();
    }
}
