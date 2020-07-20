package dev.rinaldo.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

/**
 * Implementação da verificação de Liveness da aplicação.
 * 
 * Especificações utilizadas:
 * - CDI para a anotação de {@link ApplicationScoped}.
 * - MicroProfile Health através da anotação {@link Liveness}.
 * 
 * Essa é uma implementação bem simples que apenas garante que a aplicação deu uma resposta. Isso é básico, mas já é suficiente
 * pra saber que a aplicação está rodando.
 * 
 * @author rinaldodev
 *
 */
@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Aplicação está rodando.");
    }
}
