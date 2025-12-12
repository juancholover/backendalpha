package upeu.edu.pe.curriculum.application.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;
import upeu.edu.pe.curriculum.domain.services.DocxGeneratorService;

/**
 * Caso de uso para generar un documento DOCX del sílabo.
 */
@ApplicationScoped
public class GenerarDocxSilaboUseCase {

    @Inject
    SilaboRepository silaboRepository;

    @Inject
    DocxGeneratorService docxGeneratorService;

    /**
     * Genera un DOCX del sílabo.
     * 
     * @param silaboId ID del sílabo
     * @return bytes del documento DOCX
     */
    public byte[] ejecutar(Long silaboId) {
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
            .orElseThrow(() -> new IllegalArgumentException("Sílabo no encontrado con ID: " + silaboId));

        return docxGeneratorService.generarDocxSilabo(silabo);
    }
}
