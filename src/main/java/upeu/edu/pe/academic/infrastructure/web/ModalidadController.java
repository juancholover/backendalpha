package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.ModalidadRequestDTO;
import upeu.edu.pe.academic.application.dto.ModalidadResponseDTO;
import upeu.edu.pe.academic.domain.services.ModalidadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de Modalidades
 * 
 * Endpoints:
 * - POST   /api/v1/modalidades                          - Crear modalidad
 * - GET    /api/v1/modalidades/{id}                     - Buscar por ID
 * - GET    /api/v1/modalidades/codigo/{codigo}          - Buscar por código
 * - GET    /api/v1/modalidades/universidad/{universidadId} - Listar por universidad
 * - GET    /api/v1/modalidades/requieren-aula           - Filtrar las que requieren aula
 * - GET    /api/v1/modalidades/requieren-plataforma     - Filtrar las que requieren plataforma
 * - GET    /api/v1/modalidades/search                   - Buscar por nombre
 * - PUT    /api/v1/modalidades/{id}                     - Actualizar modalidad
 * - DELETE /api/v1/modalidades/{id}                     - Eliminar modalidad
 */
@Path("/api/v1/modalidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Modalidades", description = "Gestión de modalidades de cursos (Presencial, Virtual, Semipresencial, Híbrida)")
public class ModalidadController {

    @Inject
    ModalidadService modalidadService;

    @POST
    @Operation(summary = "Crear modalidad", description = "Crea una nueva modalidad de curso")
    @APIResponse(responseCode = "201", description = "Modalidad creada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    @APIResponse(responseCode = "409", description = "Código de modalidad ya existe")
    public Response crear(@Valid ModalidadRequestDTO dto) {
        ModalidadResponseDTO modalidad = modalidadService.crear(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Modalidad creada exitosamente", modalidad))
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar modalidad por ID", description = "Obtiene una modalidad por su ID")
    @APIResponse(responseCode = "200", description = "Modalidad encontrada")
    @APIResponse(responseCode = "404", description = "Modalidad no encontrada")
    public Response buscarPorId(
            @Parameter(description = "ID de la modalidad") 
            @PathParam("id") Long id) {
        ModalidadResponseDTO modalidad = modalidadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Modalidad encontrada", modalidad)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar modalidad por código", description = "Obtiene una modalidad por su código")
    @APIResponse(responseCode = "200", description = "Modalidad encontrada")
    @APIResponse(responseCode = "404", description = "Modalidad no encontrada")
    public Response buscarPorCodigo(
            @Parameter(description = "Código de la modalidad (ej: PRES, VIRT, SEMI, HIBR)") 
            @PathParam("codigo") String codigo,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        ModalidadResponseDTO modalidad = modalidadService.buscarPorCodigo(codigo, universidadId);
        return Response.ok(ApiResponse.success("Modalidad encontrada", modalidad)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar modalidades por universidad", description = "Obtiene todas las modalidades de una universidad")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorUniversidad(
            @Parameter(description = "ID de la universidad") 
            @PathParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarPorUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Modalidades obtenidas exitosamente", modalidades)).build();
    }

    @GET
    @Path("/requieren-aula")
    @Operation(summary = "Listar modalidades que requieren aula", 
               description = "Obtiene modalidades que requieren infraestructura física (aula)")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarRequierenAula(
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarRequierenAula(universidadId);
        return Response.ok(ApiResponse.success("Modalidades que requieren aula", modalidades)).build();
    }

    @GET
    @Path("/requieren-plataforma")
    @Operation(summary = "Listar modalidades que requieren plataforma virtual", 
               description = "Obtiene modalidades que requieren plataforma digital")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarRequierenPlataforma(
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarRequierenPlataforma(universidadId);
        return Response.ok(ApiResponse.success("Modalidades que requieren plataforma", modalidades)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar modalidades por nombre", description = "Búsqueda parcial por nombre de modalidad")
    @APIResponse(responseCode = "200", description = "Resultados obtenidos exitosamente")
    public Response buscarPorNombre(
            @Parameter(description = "Término de búsqueda") 
            @QueryParam("q") String nombre,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.buscarPorNombre(nombre, universidadId);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", modalidades)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar modalidad", description = "Actualiza los datos de una modalidad existente")
    @APIResponse(responseCode = "200", description = "Modalidad actualizada exitosamente")
    @APIResponse(responseCode = "404", description = "Modalidad no encontrada")
    @APIResponse(responseCode = "409", description = "Código de modalidad ya existe")
    public Response actualizar(
            @Parameter(description = "ID de la modalidad") 
            @PathParam("id") Long id, 
            @Valid ModalidadRequestDTO dto) {
        ModalidadResponseDTO modalidad = modalidadService.actualizar(id, dto);
        return Response.ok(ApiResponse.success("Modalidad actualizada exitosamente", modalidad)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar modalidad", description = "Elimina (lógicamente) una modalidad")
    @APIResponse(responseCode = "200", description = "Modalidad eliminada exitosamente")
    @APIResponse(responseCode = "404", description = "Modalidad no encontrada")
    public Response eliminar(
            @Parameter(description = "ID de la modalidad") 
            @PathParam("id") Long id) {
        modalidadService.eliminar(id);
        return Response.ok(ApiResponse.success("Modalidad eliminada exitosamente")).build();
    }
}
