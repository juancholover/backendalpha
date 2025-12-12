package upeu.edu.pe.curriculum.application.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;
import upeu.edu.pe.curriculum.domain.services.PdfGeneratorService;

/**
 * Caso de uso para generar un documento PDF del sílabo.
 */
@ApplicationScoped
public class GenerarPdfSilaboUseCase {

    @Inject
    SilaboRepository silaboRepository;

    @Inject
    PdfGeneratorService pdfGeneratorService;

    /**
     * Genera un PDF del sílabo.
     * 
     * @param silaboId ID del sílabo
     * @return bytes del documento PDF
     */
    public byte[] ejecutar(Long silaboId) {
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
            .orElseThrow(() -> new IllegalArgumentException("Sílabo no encontrado con ID: " + silaboId));

        return pdfGeneratorService.generarPdfSilabo(silabo);
    }
}
