package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.EmpleadoRequestDTO;
import upeu.edu.pe.academic.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.academic.domain.services.EmpleadoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Empleados", description = "Gestión de empleados del sistema académico")
public class EmpleadoController {

    @Inject
    EmpleadoService empleadoService;

    @POST
    @Operation(summary = "Crear empleado", description = "Crea un nuevo empleado en el sistema")
    @APIResponse(responseCode = "201", description = "Empleado creado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    @APIResponse(responseCode = "409", description = "Empleado duplicado")
    public Response create(@Valid EmpleadoRequestDTO dto) {
        EmpleadoResponseDTO empleado = empleadoService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Empleado creado exitosamente", empleado))
                .build();
    }

    @GET
    @Operation(summary = "Listar empleados", description = "Obtiene todos los empleados")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAll() {
        List<EmpleadoResponseDTO> empleados = empleadoService.findAll();
        return Response.ok(ApiResponse.success("Empleados obtenidos exitosamente", empleados)).build();
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Listar empleados activos", description = "Obtiene solo empleados con estado laboral ACTIVO")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAllActive() {
        List<EmpleadoResponseDTO> empleados = empleadoService.findAllActive();
        return Response.ok(ApiResponse.success("Empleados activos obtenidos exitosamente", empleados)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar empleado por ID", description = "Obtiene un empleado por su ID")
    @APIResponse(responseCode = "200", description = "Empleado encontrado")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response findById(@PathParam("id") Long id) {
        EmpleadoResponseDTO empleado = empleadoService.findById(id);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar empleado por código", description = "Obtiene un empleado por su código")
    @APIResponse(responseCode = "200", description = "Empleado encontrado")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response findByCodigoEmpleado(@PathParam("codigo") String codigo) {
        EmpleadoResponseDTO empleado = empleadoService.findByCodigoEmpleado(codigo);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/persona/{personaId}")
    @Operation(summary = "Buscar empleado por persona", description = "Obtiene un empleado por el ID de persona")
    @APIResponse(responseCode = "200", description = "Empleado encontrado")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response findByPersona(@PathParam("personaId") Long personaId) {
        EmpleadoResponseDTO empleado = empleadoService.findByPersona(personaId);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Buscar empleados por estado laboral", description = "Filtra empleados por estado (ACTIVO/CESADO/SUSPENDIDO/LICENCIA)")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findByEstadoLaboral(@PathParam("estado") String estado) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByEstadoLaboral(estado);
        return Response.ok(ApiResponse.success("Empleados filtrados por estado", empleados)).build();
    }

    @GET
    @Path("/contrato/{tipo}")
    @Operation(summary = "Buscar empleados por tipo de contrato", description = "Filtra empleados por tipo (NOMBRADO/CONTRATADO/TEMPORAL)")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findByTipoContrato(@PathParam("tipo") String tipo) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByTipoContrato(tipo);
        return Response.ok(ApiResponse.success("Empleados filtrados por tipo de contrato", empleados)).build();
    }

    @GET
    @Path("/unidad/{unidadId}")
    @Operation(summary = "Buscar empleados por unidad organizativa", description = "Obtiene empleados de una unidad organizativa")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findByUnidadOrganizativa(@PathParam("unidadId") Long unidadId) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByUnidadOrganizativa(unidadId);
        return Response.ok(ApiResponse.success("Empleados de la unidad obtenidos", empleados)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar empleados", description = "Búsqueda general por nombre o código")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response search(@QueryParam("q") String query) {
        List<EmpleadoResponseDTO> empleados = empleadoService.search(query);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", empleados)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado")
    @APIResponse(responseCode = "200", description = "Empleado actualizado exitosamente")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response update(@PathParam("id") Long id, @Valid EmpleadoRequestDTO dto) {
        EmpleadoResponseDTO empleado = empleadoService.update(id, dto);
        return Response.ok(ApiResponse.success("Empleado actualizado exitosamente", empleado)).build();
    }

    @PATCH
    @Path("/{id}/estado")
    @Operation(summary = "Cambiar estado laboral", description = "Cambia el estado laboral de un empleado")
    @APIResponse(responseCode = "200", description = "Estado cambiado exitosamente")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response cambiarEstadoLaboral(
            @PathParam("id") Long id,
            @QueryParam("nuevoEstado") String nuevoEstado) {
        EmpleadoResponseDTO empleado = empleadoService.cambiarEstadoLaboral(id, nuevoEstado);
        return Response.ok(ApiResponse.success("Estado laboral actualizado", empleado)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar empleado", description = "Elimina lógicamente un empleado (marca como CESADO)")
    @APIResponse(responseCode = "200", description = "Empleado eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Empleado no encontrado")
    public Response delete(@PathParam("id") Long id) {
        empleadoService.delete(id);
        return Response.ok(ApiResponse.success("Empleado eliminado exitosamente", null)).build();
    }
}
