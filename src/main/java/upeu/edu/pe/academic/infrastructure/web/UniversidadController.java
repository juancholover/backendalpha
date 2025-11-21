package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.domain.services.UniversidadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/universidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Universidades", description = "Gestión de universidades del sistema académico")
public class UniversidadController {

    @Inject
    UniversidadService universidadService;

    @POST
    @Operation(summary = "Crear universidad", description = "Crea una nueva universidad en el sistema")
    @APIResponse(responseCode = "201", description = "Universidad creada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    @APIResponse(responseCode = "409", description = "RUC ya existe")
    public Response create(@Valid UniversidadRequestDTO dto) {
        UniversidadResponseDTO universidad = universidadService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Universidad creada exitosamente", universidad))
                .build();
    }

    @GET
    @Operation(summary = "Listar universidades", description = "Obtiene todas las universidades")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAll() {
        List<UniversidadResponseDTO> universidades = universidadService.findAll();
        return Response.ok(ApiResponse.success("Universidades obtenidas exitosamente", universidades)).build();
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Listar universidades activas", description = "Obtiene solo las universidades activas")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAllActive() {
        List<UniversidadResponseDTO> universidades = universidadService.findAllActive();
        return Response.ok(ApiResponse.success("Universidades activas obtenidas exitosamente", universidades)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar universidad por ID", description = "Obtiene una universidad por su ID")
    @APIResponse(responseCode = "200", description = "Universidad encontrada")
    @APIResponse(responseCode = "404", description = "Universidad no encontrada")
    public Response findById(@PathParam("id") Long id) {
        UniversidadResponseDTO universidad = universidadService.findById(id);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar universidad por código", description = "Obtiene una universidad por su código")
    @APIResponse(responseCode = "200", description = "Universidad encontrada")
    @APIResponse(responseCode = "404", description = "Universidad no encontrada")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        UniversidadResponseDTO universidad = universidadService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/dominio/{dominio}")
    @Operation(summary = "Buscar universidad por dominio", description = "Obtiene una universidad por su dominio")
    @APIResponse(responseCode = "200", description = "Universidad encontrada")
    @APIResponse(responseCode = "404", description = "Universidad no encontrada")
    public Response findByDominio(@PathParam("dominio") String dominio) {
        UniversidadResponseDTO universidad = universidadService.findByDominio(dominio);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar universidades", description = "Búsqueda general por nombre")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response search(@QueryParam("q") String query) {
        List<UniversidadResponseDTO> universidades = universidadService.search(query);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", universidades)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar universidad", description = "Actualiza los datos de una universidad")
    @APIResponse(responseCode = "200", description = "Universidad actualizada exitosamente")
    @APIResponse(responseCode = "404", description = "Universidad no encontrada")
    public Response update(@PathParam("id") Long id, @Valid UniversidadRequestDTO dto) {
        UniversidadResponseDTO universidad = universidadService.update(id, dto);
        return Response.ok(ApiResponse.success("Universidad actualizada exitosamente", universidad)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar universidad", description = "Elimina lógicamente una universidad")
    @APIResponse(responseCode = "200", description = "Universidad eliminada exitosamente")
    @APIResponse(responseCode = "404", description = "Universidad no encontrada")
    public Response delete(@PathParam("id") Long id) {
        universidadService.delete(id);
        return Response.ok(ApiResponse.success("Universidad eliminada exitosamente", null)).build();
    }
}
