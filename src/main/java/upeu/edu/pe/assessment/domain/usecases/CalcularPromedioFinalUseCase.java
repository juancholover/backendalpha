package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.entities.EvaluacionNota;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionNotaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Caso de Uso: Calcular el promedio final ponderado de un estudiante en un
 * curso.
 * 
 * Fórmula: Σ (nota_criterio × peso_criterio) / 100
 * 
 * Nota mínima aprobatoria institucional: 10.5 (se redondea a 11)
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class CalcularPromedioFinalUseCase {

    private static final BigDecimal NOTA_MINIMA_APROBATORIA = new BigDecimal("10.5");

    @Inject
    EvaluacionNotaRepository notaRepository;

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    /**
     * Resultado del cálculo del promedio.
     */
    public record ResultadoPromedio(
            BigDecimal promedioFinal,
            boolean estaAprobado,
            int criteriosEvaluados,
            int criteriosTotales,
            BigDecimal pesoEvaluado,
            String mensaje) {
    }

    /**
     * Ejecuta el caso de uso para calcular el promedio final.
     * 
     * @param estudianteId ID del estudiante
     * @param seccionId    ID de la sección (curso ofertado)
     * @return El resultado del cálculo con el promedio y estado de aprobación
     * @throws BusinessException si no hay criterios definidos
     */
    public ResultadoPromedio execute(Long estudianteId, Long seccionId) {

        // 1. Obtener todos los criterios activos de la sección
        List<EvaluacionCriterio> criterios = criterioRepository.findActivosBySeccion(seccionId);

        if (criterios.isEmpty()) {
            throw new BusinessException(
                    "No hay criterios de evaluación definidos para esta sección.");
        }

        // 2. Obtener las notas del estudiante en esta sección
        List<EvaluacionNota> notas = notaRepository.findByEstudianteAndSeccion(estudianteId, seccionId);

        // 3. Calcular promedio ponderado
        BigDecimal sumatoriaPonderada = BigDecimal.ZERO;
        BigDecimal pesoEvaluado = BigDecimal.ZERO;
        int criteriosEvaluados = 0;

        for (EvaluacionCriterio criterio : criterios) {
            // Buscar la nota de este criterio
            EvaluacionNota notaEncontrada = notas.stream()
                    .filter(n -> n.getCriterio().getId().equals(criterio.getId()))
                    .findFirst()
                    .orElse(null);

            if (notaEncontrada != null && notaEncontrada.getNotaFinal() != null) {
                // notaFinal × peso / 100
                BigDecimal ponderado = notaEncontrada.getNotaFinal()
                        .multiply(new BigDecimal(criterio.getPeso()))
                        .divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

                sumatoriaPonderada = sumatoriaPonderada.add(ponderado);
                pesoEvaluado = pesoEvaluado.add(new BigDecimal(criterio.getPeso()));
                criteriosEvaluados++;
            }
        }

        // 4. Calcular promedio final (redondeado a 2 decimales)
        BigDecimal promedioFinal = sumatoriaPonderada.setScale(2, RoundingMode.HALF_UP);
        boolean aprobado = promedioFinal.compareTo(NOTA_MINIMA_APROBATORIA) >= 0;

        // 5. Generar mensaje descriptivo
        String mensaje = generarMensaje(criteriosEvaluados, criterios.size(), pesoEvaluado, aprobado);

        return new ResultadoPromedio(
                promedioFinal,
                aprobado,
                criteriosEvaluados,
                criterios.size(),
                pesoEvaluado.setScale(0, RoundingMode.HALF_UP),
                mensaje);
    }

    /**
     * Genera un mensaje descriptivo del resultado.
     */
    private String generarMensaje(int criteriosEvaluados, int criteriosTotales,
            BigDecimal pesoEvaluado, boolean aprobado) {
        StringBuilder sb = new StringBuilder();

        if (criteriosEvaluados < criteriosTotales) {
            sb.append(String.format("Evaluación incompleta: %d de %d criterios calificados (%.0f%% del peso total). ",
                    criteriosEvaluados, criteriosTotales, pesoEvaluado));
        } else {
            sb.append("Evaluación completa. ");
        }

        sb.append(aprobado ? "Estado: APROBADO." : "Estado: DESAPROBADO.");

        return sb.toString();
    }
}
