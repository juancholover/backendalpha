package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

/**
 * Caso de uso: Aprobar un sílabo
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo esté EN_REVISION
 * - Aprobar el sílabo
 * - Registrar en historial
 */
@ApplicationScoped
public class AprobarSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Transactional
    public Silabo execute(Long silaboId, String usuarioAprobador, String observaciones) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + silaboId
                ));
        
        // Validar que esté EN_REVISION
        if (!"EN_REVISION".equals(silabo.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden aprobar sílabos en revisión. Estado actual: " + silabo.getEstado()
            );
        }
        
        // Aprobar
        silabo.aprobar();
        
        // Actualizar observaciones si se proporcionan
        if (observaciones != null && !observaciones.isBlank()) {
            silabo.setObservaciones(observaciones);
        }
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "APROBACION",
            usuarioAprobador != null ? usuarioAprobador : "SYSTEM",
            observaciones != null ? observaciones : "Sílabo aprobado"
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
