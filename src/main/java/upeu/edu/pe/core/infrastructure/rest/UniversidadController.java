package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.core.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.core.application.mapper.UniversidadMapper;
import upeu.edu.pe.core.domain.commands.ActualizarUniversidadCommand;
import upeu.edu.pe.core.domain.commands.CrearUniversidadCommand;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.services.UniversidadService;
import upeu.edu.pe.core.domain.usecases.ActualizarUniversidadUseCase;
import upeu.edu.pe.core.domain.usecases.CrearUniversidadUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarUniversidadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para universidades.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/universidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Universidades", description = "Gestión de universidades del sistema académico")
public class UniversidadController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearUniversidadUseCase crearUseCase;

    @Inject
    ActualizarUniversidadUseCase actualizarUseCase;

    @Inject
    EliminarUniversidadUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    UniversidadService universidadService;

    @Inject
    UniversidadMapper universidadMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar universidades")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAll() {
        List<UniversidadResponseDTO> universidades = universidadService.findAll();
        return Response.ok(ApiResponse.success("Universidades obtenidas", universidades)).build();
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Listar universidades activas")
    public Response findAllActive() {
        List<UniversidadResponseDTO> universidades = universidadService.findAllActive();
        return Response.ok(ApiResponse.success("Universidades activas obtenidas", universidades)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar universidad por ID")
    public Response findById(@PathParam("id") Long id) {
        UniversidadResponseDTO universidad = universidadService.findById(id);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar universidad por código")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        UniversidadResponseDTO universidad = universidadService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar universidades")
    public Response search(@QueryParam("q") String query) {
        List<UniversidadResponseDTO> universidades = universidadService.search(query);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", universidades)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear universidad")
    @APIResponse(responseCode = "201", description = "Universidad creada exitosamente")
    public Response create(@Valid UniversidadRequestDTO dto) {
        CrearUniversidadCommand command = new CrearUniversidadCommand(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getRuc(),
                dto.getTipo(),
                dto.getPlan(),
                dto.getDominio(),
                dto.getWebsite(),
                dto.getMaxEstudiantes(),
                dto.getMaxDocentes());

        Universidad universidad = crearUseCase.execute(command);
        UniversidadResponseDTO response = universidadMapper.toResponseDTO(universidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Universidad creada", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar universidad")
    public Response update(@PathParam("id") Long id, @Valid UniversidadRequestDTO dto) {
        ActualizarUniversidadCommand command = new ActualizarUniversidadCommand(
                id,
                dto.getNombre(),
                dto.getTipo(),
                dto.getPlan(),
                dto.getDominio(),
                dto.getWebsite(),
                dto.getEstado(),
                dto.getMaxEstudiantes(),
                dto.getMaxDocentes());

        Universidad universidad = actualizarUseCase.execute(command);
        UniversidadResponseDTO response = universidadMapper.toResponseDTO(universidad);

        return Response.ok(ApiResponse.success("Universidad actualizada", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar universidad")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Universidad eliminada", null)).build();
    }
}
