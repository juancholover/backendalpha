package upeu.edu.pe.people.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.people.application.dto.EmpleadoRequestDTO;
import upeu.edu.pe.people.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "cdi", imports = {java.math.BigDecimal.class})
public interface EmpleadoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "unidadOrganizativa", source = "unidadOrganizativaId", qualifiedByName = "mapUnidadOrganizativa")
    @Mapping(target = "salario", source = "salario", qualifiedByName = "doubleToBigDecimal")
    Empleado toEntity(EmpleadoRequestDTO dto);

    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "nombreCompleto", expression = "java(getNombreCompleto(entity))")
    @Mapping(target = "unidadOrganizativaId", source = "unidadOrganizativa.id")
    @Mapping(target = "unidadOrganizativaNombre", source = "unidadOrganizativa.nombre")
    @Mapping(target = "aniosServicio", expression = "java(calculateAniosServicio(entity))")
    @Mapping(target = "salario", source = "salario", qualifiedByName = "bigDecimalToDouble")
    EmpleadoResponseDTO toResponseDTO(Empleado entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "unidadOrganizativa", source = "unidadOrganizativaId", qualifiedByName = "mapUnidadOrganizativa")
    @Mapping(target = "salario", source = "salario", qualifiedByName = "doubleToBigDecimal")
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

    /**
     * Convierte Double a BigDecimal
     */
    @Named("doubleToBigDecimal")
    default java.math.BigDecimal doubleToBigDecimal(Double value) {
        if (value == null) {
            return null;
        }
        return java.math.BigDecimal.valueOf(value);
    }

    /**
     * Convierte BigDecimal a Double
     */
    @Named("bigDecimalToDouble")
    default Double bigDecimalToDouble(java.math.BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.doubleValue();
    }
}

