package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.LocalizacionRequestDTO;
import upeu.edu.pe.enrollment.application.dto.LocalizacionResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.LocalizacionMapper;
import upeu.edu.pe.enrollment.domain.commands.CrearLocalizacionCommand;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.services.LocalizacionService;
import upeu.edu.pe.enrollment.domain.usecases.CrearLocalizacionUseCase;
import upeu.edu.pe.enrollment.domain.usecases.EliminarLocalizacionUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para localizaciones.
 */
@Path("/api/v1/localizaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Localizaciones", description = "Gestión de aulas y espacios")
public class LocalizacionController {

    @Inject
    CrearLocalizacionUseCase crearUseCase;

    @Inject
    EliminarLocalizacionUseCase eliminarUseCase;

    @Inject
    LocalizacionService localizacionService;

    @Inject
    LocalizacionMapper localizacionMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todas las localizaciones")
    public Response getAll() {
        List<LocalizacionResponseDTO> localizaciones = localizacionService.findAll();
        return Response.ok(ApiResponse.success("Localizaciones obtenidas", localizaciones)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar localización por ID")
    public Response getById(@PathParam("id") Long id) {
        LocalizacionResponseDTO localizacion = localizacionService.findById(id);
        return Response.ok(ApiResponse.success("Localización encontrada", localizacion)).build();
    }

    @GET
    @Path("/tipo/{tipoId}")
    @Operation(summary = "Listar por tipo de localización")
    public Response getByTipoLocalizacion(@PathParam("tipoId") Long tipoId) {
        List<LocalizacionResponseDTO> localizaciones = localizacionService.findByTipoLocalizacion(tipoId);
        return Response.ok(ApiResponse.success("Localizaciones por tipo", localizaciones)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear localización")
    public Response create(@Valid LocalizacionRequestDTO dto) {
        CrearLocalizacionCommand command = new CrearLocalizacionCommand(
                dto.getTipoLocalizacionId(),
                dto.getCodigo(),
                dto.getNombre(),
                null, // direccion
                null, // telefono
                null, // email
                false // esPrincipal
        );

        Localizacion localizacion = crearUseCase.execute(command);
        LocalizacionResponseDTO response = localizacionMapper.toResponseDTO(localizacion);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Localización creada", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar localización")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Localización eliminada", null)).build();
    }
}
