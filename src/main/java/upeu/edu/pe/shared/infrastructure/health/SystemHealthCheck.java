package upeu.edu.pe.shared.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

/**
 * Health Check para métricas generales del sistema (CPU, Memoria, Threads).
 * Solo verifica liveness - que la aplicación está funcionando.
 */
@Liveness
@ApplicationScoped
public class SystemHealthCheck implements HealthCheck {

    private static final long MEMORY_THRESHOLD_PERCENT = 90; // Alerta si uso de memoria > 90%

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("System resources health check");

        try {
            // Información de memoria
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            
            long maxMemory = heapUsage.getMax();
            long usedMemory = heapUsage.getUsed();
            long freeMemory = maxMemory - usedMemory;
            long memoryUsagePercent = (usedMemory * 100) / maxMemory;

            // Información del sistema operativo
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            int availableProcessors = osBean.getAvailableProcessors();
            double systemLoadAverage = osBean.getSystemLoadAverage();

            // Información de threads
            int activeThreads = Thread.activeCount();

            // Determinar el estado
            boolean isHealthy = memoryUsagePercent < MEMORY_THRESHOLD_PERCENT;

            if (isHealthy) {
                responseBuilder.up();
            } else {
                responseBuilder.down()
                    .withData("warning", "Memory usage above threshold");
            }

            // Agregar métricas
            responseBuilder
                .withData("memory_used_mb", usedMemory / (1024 * 1024))
                .withData("memory_max_mb", maxMemory / (1024 * 1024))
                .withData("memory_free_mb", freeMemory / (1024 * 1024))
                .withData("memory_usage_percent", memoryUsagePercent)
                .withData("processors", availableProcessors)
                .withData("system_load_average", systemLoadAverage >= 0 ? String.valueOf(systemLoadAverage) : "N/A")
                .withData("active_threads", activeThreads)
                .withData("jvm_version", System.getProperty("java.version"))
                .withData("os_name", System.getProperty("os.name"))
                .withData("os_version", System.getProperty("os.version"));

        } catch (Exception e) {
            return responseBuilder.down()
                .withData("status", "error")
                .withData("error", e.getMessage())
                .build();
        }

        return responseBuilder.build();
    }
}
