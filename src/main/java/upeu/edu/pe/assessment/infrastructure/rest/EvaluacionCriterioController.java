package upeu.edu.pe.assessment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.assessment.application.dto.EvaluacionCriterioRequestDTO;
import upeu.edu.pe.assessment.application.dto.EvaluacionCriterioResponseDTO;
import upeu.edu.pe.assessment.application.mapper.EvaluacionCriterioMapper;
import upeu.edu.pe.assessment.domain.commands.CrearCriterioCommand;
import upeu.edu.pe.assessment.domain.commands.ActualizarCriterioCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.services.EvaluacionCriterioService;
import upeu.edu.pe.assessment.domain.usecases.CrearCriterioUseCase;
import upeu.edu.pe.assessment.domain.usecases.ActualizarCriterioUseCase;
import upeu.edu.pe.assessment.domain.usecases.EliminarCriterioUseCase;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de criterios de evaluación.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 * 
 * @author Sistema UPeU
 */
@Path("/api/v1/evaluacion-criterios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Criterios de Evaluación", description = "Gestión de criterios y rúbricas de evaluación")
public class EvaluacionCriterioController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearCriterioUseCase crearCriterioUseCase;

    @Inject
    ActualizarCriterioUseCase actualizarCriterioUseCase;

    @Inject
    EliminarCriterioUseCase eliminarCriterioUseCase;

    // Service para operaciones de lectura
    @Inject
    EvaluacionCriterioService criterioService;

    @Inject
    EvaluacionCriterioMapper mapper;

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear criterio de evaluación", description = "Crea un nuevo criterio para un curso ofertado")
    public Response create(@Valid EvaluacionCriterioRequestDTO requestDTO) {

        // Convertir DTO a Command
        CrearCriterioCommand command = new CrearCriterioCommand(
                requestDTO.getSeccionId(),
                requestDTO.getNombre(),
                requestDTO.getPeso(),
                requestDTO.getTipoEvaluacion(),
                requestDTO.getNotaMaxima(),
                requestDTO.getNotaMinimaAprobatoria(),
                requestDTO.getEsRecuperable(),
                requestDTO.getDescripcion());

        // Ejecutar Use Case
        EvaluacionCriterio criterio = crearCriterioUseCase.execute(command);

        // Convertir a DTO de respuesta
        EvaluacionCriterioResponseDTO response = mapper.toResponseDTO(criterio);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar criterio de evaluación", description = "Actualiza un criterio existente")
    public Response update(@PathParam("id") Long id, @Valid EvaluacionCriterioRequestDTO requestDTO) {

        // Convertir DTO a Command
        ActualizarCriterioCommand command = new ActualizarCriterioCommand(
                id,
                requestDTO.getSeccionId(),
                requestDTO.getNombre(),
                requestDTO.getPeso(),
                requestDTO.getTipoEvaluacion(),
                requestDTO.getNotaMaxima(),
                requestDTO.getNotaMinimaAprobatoria(),
                requestDTO.getEsRecuperable(),
                requestDTO.getOrden(),
                requestDTO.getEstado(),
                requestDTO.getDescripcion());

        // Ejecutar Use Case
        EvaluacionCriterio criterio = actualizarCriterioUseCase.execute(command);

        // Convertir a DTO de respuesta
        EvaluacionCriterioResponseDTO response = mapper.toResponseDTO(criterio);

        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar criterio de evaluación", description = "Elimina (soft delete) un criterio")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarCriterioUseCase.execute(id);

        return Response.noContent().build();
    }

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/seccion/{seccionId}")
    @Operation(summary = "Listar criterios por sección", description = "Obtiene todos los criterios de una sección")
    public Response findBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/activos")
    @Operation(summary = "Listar criterios activos por sección", description = "Obtiene solo los criterios activos de una sección")
    public Response findActivosBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findActivosBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/tipo/{tipo}")
    @Operation(summary = "Listar criterios por tipo", description = "Filtra criterios por tipo de evaluación (EXAMEN, PRACTICA, etc.)")
    public Response findByTipoAndSeccion(
            @PathParam("tipo") String tipo,
            @PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findByTipoAndSeccion(tipo, seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/recuperables")
    @Operation(summary = "Listar criterios recuperables", description = "Obtiene criterios que permiten recuperación")
    public Response findRecuperablesBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findRecuperablesBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener criterio por ID", description = "Obtiene los detalles de un criterio específico")
    public Response findById(@PathParam("id") Long id) {
        EvaluacionCriterioResponseDTO criterio = criterioService.findById(id);
        return Response.ok(criterio).build();
    }

    // =====================================================
    // OPERACIONES DE VALIDACIÓN
    // =====================================================

    @GET
    @Path("/seccion/{seccionId}/peso-total")
    @Operation(summary = "Obtener peso total", description = "Suma de todos los pesos de los criterios de la sección")
    public Response sumPesoBySeccion(@PathParam("seccionId") Long seccionId) {
        BigDecimal pesoTotal = criterioService.sumPesoBySeccion(seccionId);
        return Response.ok().entity("{\"pesoTotal\": " + pesoTotal + "}").build();
    }

    @GET
    @Path("/seccion/{seccionId}/peso-valido")
    @Operation(summary = "Validar peso total", description = "Verifica si el peso total es exactamente 100%")
    public Response isPesoTotalValido(@PathParam("seccionId") Long seccionId) {
        boolean valido = criterioService.isPesoTotalValido(seccionId);
        return Response.ok().entity("{\"valido\": " + valido + "}").build();
    }

    @GET
    @Path("/seccion/{seccionId}/count")
    @Operation(summary = "Contar criterios", description = "Cuenta la cantidad de criterios de una sección")
    public Response countBySeccion(@PathParam("seccionId") Long seccionId) {
        long count = criterioService.countBySeccion(seccionId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }
}
