package upeu.edu.pe.curriculum.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.CursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.CursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.CursoMapper;
import upeu.edu.pe.curriculum.domain.commands.CrearCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.services.CursoService;
import upeu.edu.pe.curriculum.domain.usecases.CrearCursoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarCursoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;


@Path("/api/v1/cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cursos", description = "API para gestión de cursos académicos")
public class CursoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearCursoUseCase crearCursoUseCase;

    @Inject
    EliminarCursoUseCase eliminarCursoUseCase;

    // Service para operaciones de lectura
    @Inject
    CursoService cursoService;

    @Inject
    CursoMapper cursoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todos los cursos")
    public Response findAll() {
        List<CursoResponseDTO> cursos = cursoService.findAll();
        return Response.ok(ApiResponse.success("Cursos listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener curso por ID")
    public Response findById(@PathParam("id") Long id) {
        CursoResponseDTO curso = cursoService.findById(id);
        return Response.ok(ApiResponse.success("Curso encontrado", curso)).build();
    }

    @GET
    @Path("/codigo/{codigoCurso}")
    @Operation(summary = "Buscar curso por código")
    public Response findByCodigoCurso(@PathParam("codigoCurso") String codigoCurso) {
        CursoResponseDTO curso = cursoService.findByCodigoCurso(codigoCurso);
        return Response.ok(ApiResponse.success("Curso encontrado", curso)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar cursos por universidad")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<CursoResponseDTO> cursos = cursoService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Cursos de la universidad listados", cursos)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear nuevo curso")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Curso creado exitosamente"),
            @APIResponse(responseCode = "409", description = "El curso ya existe")
    })
    public Response create(@Valid CursoRequestDTO dto) {

        // Convertir DTO a Command
        CrearCursoCommand command = new CrearCursoCommand(
                dto.getCodigoCurso(),
                dto.getNombre(),
                dto.getTipoCurso(),
                dto.getHorasTeoricas(),
                dto.getHorasPracticas(),
                dto.getHorasSemanales(),
                dto.getDescripcion());

        // Ejecutar Use Case
        Curso curso = crearCursoUseCase.execute(command);

        // Convertir a DTO
        CursoResponseDTO response = cursoMapper.toResponseDTO(curso);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Curso creado exitosamente", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar curso")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarCursoUseCase.execute(id);

        return Response.ok(ApiResponse.success("Curso eliminado exitosamente", null)).build();
    }
}
