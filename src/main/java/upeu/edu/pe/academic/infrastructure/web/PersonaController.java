package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.PersonaRequestDTO;
import upeu.edu.pe.academic.application.dto.PersonaResponseDTO;
import upeu.edu.pe.academic.domain.services.PersonaService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Personas", description = "API para gestión de personas")
public class PersonaController {

    @Inject
    PersonaService personaService;

    @GET
    @Operation(summary = "Listar todas las personas", description = "Obtiene una lista de todas las personas activas en el sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de personas obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Response findAll() {
        List<PersonaResponseDTO> personas = personaService.findAll();
        return Response.ok(ApiResponse.success("Personas listadas exitosamente", personas)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener persona por ID", description = "Obtiene los detalles de una persona específica por su ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Persona encontrada",
                     content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response findById(
            @Parameter(description = "ID de la persona", required = true)
            @PathParam("id") Long id) {
        PersonaResponseDTO persona = personaService.findById(id);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/documento/{numeroDocumento}")
    @Operation(summary = "Buscar persona por número de documento", description = "Busca una persona por su número de documento (DNI, CE, PASAPORTE)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Persona encontrada"),
        @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response findByNumeroDocumento(
            @Parameter(description = "Número de documento", required = true)
            @PathParam("numeroDocumento") String numeroDocumento) {
        PersonaResponseDTO persona = personaService.findByNumeroDocumento(numeroDocumento);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/email/{email}")
    @Operation(summary = "Buscar persona por email", description = "Busca una persona por su dirección de email")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Persona encontrada"),
        @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response findByEmail(
            @Parameter(description = "Email de la persona", required = true)
            @PathParam("email") String email) {
        PersonaResponseDTO persona = personaService.findByEmail(email);
        return Response.ok(ApiResponse.success("Persona encontrada", persona)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar personas por nombre", description = "Busca personas que coincidan con el término de búsqueda en nombres o apellidos")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Búsqueda completada")
    })
    public Response searchByNombres(
            @Parameter(description = "Término de búsqueda", required = true)
            @QueryParam("q") String searchTerm) {
        List<PersonaResponseDTO> personas = personaService.searchByNombres(searchTerm);
        return Response.ok(ApiResponse.success("Búsqueda completada", personas)).build();
    }

    @POST
    @Operation(summary = "Crear nueva persona", description = "Registra una nueva persona en el sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Persona creada exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @APIResponse(responseCode = "409", description = "La persona ya existe (documento o email duplicado)")
    })
    public Response create(
            @Parameter(description = "Datos de la persona a crear", required = true)
            @Valid PersonaRequestDTO dto) {
        PersonaResponseDTO persona = personaService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Persona creada exitosamente", persona))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar persona", description = "Actualiza los datos de una persona existente")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Persona actualizada exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @APIResponse(responseCode = "404", description = "Persona no encontrada"),
        @APIResponse(responseCode = "409", description = "Conflicto con datos existentes")
    })
    public Response update(
            @Parameter(description = "ID de la persona", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Datos actualizados de la persona", required = true)
            @Valid PersonaRequestDTO dto) {
        PersonaResponseDTO persona = personaService.update(id, dto);
        return Response.ok(ApiResponse.success("Persona actualizada exitosamente", persona)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar persona", description = "Elimina lógicamente una persona del sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Persona eliminada exitosamente"),
        @APIResponse(responseCode = "404", description = "Persona no encontrada")
    })
    public Response delete(
            @Parameter(description = "ID de la persona", required = true)
            @PathParam("id") Long id) {
        personaService.delete(id);
        return Response.ok(ApiResponse.success("Persona eliminada exitosamente")).build();
    }
}
