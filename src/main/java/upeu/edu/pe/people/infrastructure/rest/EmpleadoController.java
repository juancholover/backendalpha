package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.EmpleadoRequestDTO;
import upeu.edu.pe.people.application.dto.EmpleadoResponseDTO;
import upeu.edu.pe.people.application.mapper.EmpleadoMapper;
import upeu.edu.pe.people.domain.commands.CrearEmpleadoCommand;
import upeu.edu.pe.people.domain.commands.ActualizarEmpleadoCommand;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.services.EmpleadoService;
import upeu.edu.pe.people.domain.usecases.CrearEmpleadoUseCase;
import upeu.edu.pe.people.domain.usecases.ActualizarEmpleadoUseCase;
import upeu.edu.pe.people.domain.usecases.EliminarEmpleadoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de empleados.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Empleados", description = "Gestión de empleados del sistema académico")
public class EmpleadoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearEmpleadoUseCase crearEmpleadoUseCase;

    @Inject
    ActualizarEmpleadoUseCase actualizarEmpleadoUseCase;

    @Inject
    EliminarEmpleadoUseCase eliminarEmpleadoUseCase;

    // Service para operaciones de lectura
    @Inject
    EmpleadoService empleadoService;

    @Inject
    EmpleadoMapper empleadoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar empleados")
    public Response findAll() {
        List<EmpleadoResponseDTO> empleados = empleadoService.findAll();
        return Response.ok(ApiResponse.success("Empleados obtenidos exitosamente", empleados)).build();
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Listar empleados activos")
    public Response findAllActive() {
        List<EmpleadoResponseDTO> empleados = empleadoService.findAllActive();
        return Response.ok(ApiResponse.success("Empleados activos obtenidos", empleados)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar empleado por ID")
    public Response findById(@PathParam("id") Long id) {
        EmpleadoResponseDTO empleado = empleadoService.findById(id);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar empleado por código")
    public Response findByCodigoEmpleado(@PathParam("codigo") String codigo) {
        EmpleadoResponseDTO empleado = empleadoService.findByCodigoEmpleado(codigo);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/persona/{personaId}")
    @Operation(summary = "Buscar empleado por persona")
    public Response findByPersona(@PathParam("personaId") Long personaId) {
        EmpleadoResponseDTO empleado = empleadoService.findByPersona(personaId);
        return Response.ok(ApiResponse.success("Empleado encontrado", empleado)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Buscar empleados por estado laboral")
    public Response findByEstadoLaboral(@PathParam("estado") String estado) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByEstadoLaboral(estado);
        return Response.ok(ApiResponse.success("Empleados filtrados por estado", empleados)).build();
    }

    @GET
    @Path("/contrato/{tipo}")
    @Operation(summary = "Buscar empleados por tipo de contrato")
    public Response findByTipoContrato(@PathParam("tipo") String tipo) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByTipoContrato(tipo);
        return Response.ok(ApiResponse.success("Empleados filtrados por contrato", empleados)).build();
    }

    @GET
    @Path("/unidad/{unidadId}")
    @Operation(summary = "Buscar empleados por unidad organizativa")
    public Response findByUnidadOrganizativa(@PathParam("unidadId") Long unidadId) {
        List<EmpleadoResponseDTO> empleados = empleadoService.findByUnidadOrganizativa(unidadId);
        return Response.ok(ApiResponse.success("Empleados de la unidad obtenidos", empleados)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar empleados")
    public Response search(@QueryParam("q") String query) {
        List<EmpleadoResponseDTO> empleados = empleadoService.search(query);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", empleados)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear empleado")
    @APIResponse(responseCode = "201", description = "Empleado creado exitosamente")
    public Response create(@Valid EmpleadoRequestDTO dto) {

        // Convertir DTO a Command
        CrearEmpleadoCommand command = new CrearEmpleadoCommand(
                dto.personaId(),
                dto.unidadOrganizativaId(),
                dto.codigoEmpleado(),
                dto.cargo(),
                dto.tipoContrato(),
                dto.fechaIngreso(),
                dto.estadoLaboral());

        // Ejecutar Use Case
        Empleado empleado = crearEmpleadoUseCase.execute(command);

        // Convertir a DTO
        EmpleadoResponseDTO response = empleadoMapper.toResponseDTO(empleado);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Empleado creado exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar empleado")
    public Response update(@PathParam("id") Long id, @Valid EmpleadoRequestDTO dto) {

        // Convertir DTO a Command
        ActualizarEmpleadoCommand command = new ActualizarEmpleadoCommand(
                id,
                dto.unidadOrganizativaId(),
                dto.codigoEmpleado(),
                dto.cargo(),
                dto.tipoContrato(),
                dto.fechaIngreso(),
                dto.fechaCese(),
                dto.estadoLaboral());

        // Ejecutar Use Case
        Empleado empleado = actualizarEmpleadoUseCase.execute(command);

        // Convertir a DTO
        EmpleadoResponseDTO response = empleadoMapper.toResponseDTO(empleado);

        return Response.ok(ApiResponse.success("Empleado actualizado exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar empleado")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarEmpleadoUseCase.execute(id);

        return Response.ok(ApiResponse.success("Empleado eliminado exitosamente", null)).build();
    }
}
