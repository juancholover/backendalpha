package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

/**
 * Caso de uso: Rechazar un sílabo en revisión
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo esté EN_REVISION
 * - Rechazar el sílabo (vuelve a BORRADOR)
 * - Registrar en historial con motivo
 */
@ApplicationScoped
public class RechazarSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Transactional
    public Silabo execute(Long silaboId, String usuarioRevisor, String motivoRechazo) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + silaboId
                ));
        
        // Validar que esté EN_REVISION
        if (!"EN_REVISION".equals(silabo.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden rechazar sílabos en revisión. Estado actual: " + silabo.getEstado()
            );
        }
        
        // Validar que se proporcione motivo
        if (motivoRechazo == null || motivoRechazo.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar un motivo de rechazo");
        }
        
        // Rechazar
        silabo.rechazar();
        
        // Actualizar observaciones
        silabo.setObservaciones(motivoRechazo);
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "RECHAZO",
            usuarioRevisor != null ? usuarioRevisor : "SYSTEM",
            motivoRechazo
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
