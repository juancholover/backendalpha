package upeu.edu.pe.catalog.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import upeu.edu.pe.catalog.application.dto.TipoUnidadRequestDTO;
import upeu.edu.pe.catalog.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.catalog.domain.services.TipoUnidadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/tipos-unidad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoUnidadController {

    @Inject
    TipoUnidadService tipoUnidadService;

    @GET
    public ApiResponse<List<TipoUnidadResponseDTO>> getAll() {
        return ApiResponse.success("Tipos de unidad obtenidos exitosamente", tipoUnidadService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<TipoUnidadResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Tipo de unidad obtenido exitosamente", tipoUnidadService.findById(id));
    }

    @POST
    public ApiResponse<TipoUnidadResponseDTO> create(TipoUnidadRequestDTO requestDTO) {
        return ApiResponse.success("Tipo de unidad creado exitosamente", tipoUnidadService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<TipoUnidadResponseDTO> update(@PathParam("id") Long id, TipoUnidadRequestDTO requestDTO) {
        return ApiResponse.success("Tipo de unidad actualizado exitosamente", tipoUnidadService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        tipoUnidadService.delete(id);
        return ApiResponse.success("Tipo de unidad eliminado exitosamente");
    }
}
