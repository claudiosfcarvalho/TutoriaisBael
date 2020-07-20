package dev.rinaldo.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import dev.rinaldo.dao.FrutasDAO;

/**
 * Implementação da verificação de Readiness da aplicação.
 * 
 * Especificações utilizadas:
 * - CDI para a anotação de {@link ApplicationScoped} e {@link Inject}.
 * - MicroProfile Health através da anotação {@link Readiness}.
 * 
 * Essa é uma implementação que faz uma consulta simples na base de dados. Isso garante que a aplicação pelo menos está
 * acessando a base, então já pode ser chamada. Em produção talvez você precise de mais checks, caso utilize mais recursos
 * externos.
 * 
 * @author rinaldodev
 *
 */
@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    private final FrutasDAO fruitsDAO;

    @Inject
    public ReadinessCheck(FrutasDAO fruitsDAO) {
        this.fruitsDAO = fruitsDAO;
    }

    @Override
    public HealthCheckResponse call() {
        try {
            fruitsDAO.findById(1L);
            return HealthCheckResponse.up("Aplicação está pronta.");
        } catch (Exception e) {
            return HealthCheckResponse
                    .named("Aplicação não está pronta.")
                    .down()
                    .withData("erro", e.getMessage())
                    .build();
        }
    }
}
