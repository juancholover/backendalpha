package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.PersonaRequestDTO;
import upeu.edu.pe.core.application.dto.PersonaResponseDTO;
import upeu.edu.pe.core.application.mapper.PersonaMapper;
import upeu.edu.pe.core.domain.commands.CrearPersonaCommand;
import upeu.edu.pe.core.domain.commands.ActualizarPersonaCommand;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.services.PersonaService;
import upeu.edu.pe.core.domain.usecases.CrearPersonaUseCase;
import upeu.edu.pe.core.domain.usecases.ActualizarPersonaUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarPersonaUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de personas.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Personas", description = "API para gestión de personas")
public class PersonaController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearPersonaUseCase crearPersonaUseCase;

    @Inject
    ActualizarPersonaUseCase actualizarPersonaUseCase;

    @Inject
    EliminarPersonaUseCase eliminarPersonaUseCase;

    // Service para operaciones de lectura
    @Inject
    PersonaService personaService;

    @Inject
    PersonaMapper personaMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todas las personas", description = "Obtiene una lista de todas las personas activas")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Lista de personas obtenida exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Response findAll() {
        List<PersonaResponseDTO> personas = personaService.findAll();
        return Response.ok(ApiResponse.success("Personas listadas exitosamente", personas)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener persona por ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Persona encontrada"),
            @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response findById(@PathParam("id") Long id) {
        PersonaResponseDTO persona = personaService.findById(id);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/documento/{numeroDocumento}")
    @Operation(summary = "Buscar persona por número de documento")
    public Response findByNumeroDocumento(@PathParam("numeroDocumento") String numeroDocumento) {
        PersonaResponseDTO persona = personaService.findByNumeroDocumento(numeroDocumento);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/email/{email}")
    @Operation(summary = "Buscar persona por email")
    public Response findByEmail(@PathParam("email") String email) {
        PersonaResponseDTO persona = personaService.findByEmail(email);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar personas por nombre")
    public Response searchByNombres(@QueryParam("q") String searchTerm) {
        List<PersonaResponseDTO> personas = personaService.searchByNombres(searchTerm);
        return Response.ok(ApiResponse.success("Búsqueda completada", personas)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear nueva persona")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Persona creada exitosamente"),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "409", description = "Documento o email duplicado")
    })
    public Response create(@Valid PersonaRequestDTO dto) {

        // Convertir DTO a Command
        CrearPersonaCommand command = new CrearPersonaCommand(
                dto.getNombres(),
                dto.getApellidoPaterno(),
                dto.getApellidoMaterno(),
                dto.getTipoDocumento(),
                dto.getNumeroDocumento(),
                dto.getEmail(),
                dto.getTelefono(),
                dto.getDireccion(),
                dto.getGenero(),
                dto.getFechaNacimiento());

        // Ejecutar Use Case
        Persona persona = crearPersonaUseCase.execute(command);

        // Convertir a DTO de respuesta
        PersonaResponseDTO response = personaMapper.toResponseDTO(persona);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Persona creada exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar persona")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Persona actualizada exitosamente"),
            @APIResponse(responseCode = "404", description = "Persona no encontrada"),
            @APIResponse(responseCode = "409", description = "Conflicto con datos existentes")
    })
    public Response update(@PathParam("id") Long id, @Valid PersonaRequestDTO dto) {

        // Convertir DTO a Command
        ActualizarPersonaCommand command = new ActualizarPersonaCommand(
                id,
                dto.getNombres(),
                dto.getApellidoPaterno(),
                dto.getApellidoMaterno(),
                dto.getTipoDocumento(),
                dto.getNumeroDocumento(),
                dto.getEmail(),
                dto.getTelefono(),
                dto.getDireccion(),
                dto.getGenero(),
                dto.getFechaNacimiento());

        // Ejecutar Use Case
        Persona persona = actualizarPersonaUseCase.execute(command);

        // Convertir a DTO de respuesta
        PersonaResponseDTO response = personaMapper.toResponseDTO(persona);

        return Response.ok(ApiResponse.success("Persona actualizada exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar persona")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Persona eliminada exitosamente"),
            @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarPersonaUseCase.execute(id);

        return Response.ok(ApiResponse.success("Persona eliminada exitosamente")).build();
    }
}
