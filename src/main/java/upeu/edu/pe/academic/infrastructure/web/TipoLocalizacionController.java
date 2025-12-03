package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import upeu.edu.pe.academic.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.academic.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.academic.domain.services.TipoLocalizacionService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/tipos-localizacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoLocalizacionController {

    @Inject
    TipoLocalizacionService tipoLocalizacionService;

    @GET
    public ApiResponse<List<TipoLocalizacionResponseDTO>> getAll() {
        return ApiResponse.success("Tipos de localización obtenidos exitosamente", tipoLocalizacionService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<TipoLocalizacionResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Tipo de localización obtenido exitosamente", tipoLocalizacionService.findById(id));
    }

    @POST
    public ApiResponse<TipoLocalizacionResponseDTO> create(TipoLocalizacionRequestDTO requestDTO) {
        return ApiResponse.success("Tipo de localización creado exitosamente", tipoLocalizacionService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<TipoLocalizacionResponseDTO> update(@PathParam("id") Long id, TipoLocalizacionRequestDTO requestDTO) {
        return ApiResponse.success("Tipo de localización actualizado exitosamente", tipoLocalizacionService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        tipoLocalizacionService.delete(id);
        return ApiResponse.success("Tipo de localización eliminado exitosamente");
    }
}
