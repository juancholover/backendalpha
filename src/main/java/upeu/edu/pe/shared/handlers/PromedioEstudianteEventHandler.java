package upeu.edu.pe.shared.handlers;

import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.shared.events.PromedioEstudianteActualizadoEvent;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.finance.domain.repositories.BecaOtorgadaRepository;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.entities.BecaOtorgada;
import upeu.edu.pe.people.domain.entities.Estudiante;
import org.jboss.logging.Logger;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Event Handler: Reacciona cuando se actualiza el promedio de un estudiante.
 * Aplica becas automáticamente sin consultar directamente la tabla Estudiante.
 */
@ApplicationScoped
public class PromedioEstudianteEventHandler {
    
    private static final Logger LOG = Logger.getLogger(PromedioEstudianteEventHandler.class);
    
    @Inject
    CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;
    
    @Inject
    BecaOtorgadaRepository becaOtorgadaRepository;
    
    /**
     * Escucha el evento "estudiante.promedio-actualizado" publicado desde Contexto Académico.
     * No hace JOIN directo a tabla Estudiante.
     */
    @ConsumeEvent("estudiante.promedio-actualizado")
    @Transactional
    public void onPromedioActualizado(PromedioEstudianteActualizadoEvent event) {
        LOG.infof("📩 Evento recibido: PromedioActualizado[estudianteId=%d, anterior=%.2f, nuevo=%.2f]",
            event.getEstudianteId(), event.getPromedioAnterior(), event.getPromedioNuevo());
        
        // Evaluar si merece beca por excelencia (promedio >= 16)
        if (event.mereceBecaExcelencia()) {
            // Buscar la primera cuenta activa del estudiante (generalmente solo hay una)
            var cuentas = cuentaCorrienteAlumnoRepository.findByEstudiante(event.getEstudianteId());
            
            if (!cuentas.isEmpty()) {
                CuentaCorrienteAlumno cuenta = cuentas.get(0); // Usar la más reciente
                
                // Aplicar 50% de descuento en saldo pendiente
                BigDecimal saldoActual = cuenta.getMontoPendiente();
                BigDecimal porcentajeBeca = new BigDecimal("50.00");
                BigDecimal descuento = saldoActual.multiply(porcentajeBeca.divide(new BigDecimal("100")));
                BigDecimal nuevoSaldo = saldoActual.subtract(descuento);
                
                cuenta.setMontoPendiente(nuevoSaldo);
                cuentaCorrienteAlumnoRepository.persist(cuenta);
                
                // Registrar beca otorgada para auditoría
                BecaOtorgada beca = new BecaOtorgada();
                
                // Crear Estudiante con solo el ID (evita cargar entidad completa)
                Estudiante estudianteRef = new Estudiante();
                estudianteRef.setId(event.getEstudianteId());
                
                beca.setEstudiante(estudianteRef);
                beca.setCuentaCorriente(cuenta);
                beca.setTipoBeca("EXCELENCIA_ACADEMICA");
                beca.setPromedioAlcanzado(event.getPromedioNuevo());
                beca.setPorcentajeBeca(porcentajeBeca);
                beca.setMontoBeneficio(descuento);
                beca.setSaldoAnterior(saldoActual);
                beca.setSaldoNuevo(nuevoSaldo);
                beca.setFechaOtorgamiento(LocalDate.now());
                beca.setEstado("APLICADA");
                beca.setObservaciones(String.format(
                    "Beca automática por promedio %.2f (anterior: %.2f). Descuento del 50%% aplicado.", 
                    event.getPromedioNuevo(), 
                    event.getPromedioAnterior()
                ));
                
                becaOtorgadaRepository.persist(beca);
                
                LOG.infof("🎓 Beca automática aplicada y registrada: EstudianteId=%d, Descuento=S/ %.2f, NuevoSaldo=S/ %.2f, BecaId=%d",
                    event.getEstudianteId(), descuento, nuevoSaldo, beca.getId());
            } else {
                LOG.warnf("⚠️  No se encontró cuenta corriente para estudianteId=%d", event.getEstudianteId());
            }
        } else {
            LOG.infof("ℹ️  Promedio %.2f no califica para beca (requiere >= 16.00)", event.getPromedioNuevo());
        }
    }
}
