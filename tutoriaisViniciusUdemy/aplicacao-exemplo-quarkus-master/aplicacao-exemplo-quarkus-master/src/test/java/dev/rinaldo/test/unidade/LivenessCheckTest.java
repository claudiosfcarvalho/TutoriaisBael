package dev.rinaldo.test.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.State;
import org.junit.jupiter.api.Test;

import dev.rinaldo.config.LivenessCheck;

/**
 * Testes unitários de {@link LivenessCheck}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através de {@link Test}.
 * 
 * @see FrutasResourceTest
 * 
 * @author rinaldodev
 */
public class LivenessCheckTest {

    @Test
    public void liveness() {
        // given
        LivenessCheck livenessCheck = new LivenessCheck();

        // when
        HealthCheckResponse liveness = livenessCheck.call();
        State actualState = liveness.getState();
        String name = liveness.getName();

        // then
        State expectedState = State.UP;
        assertEquals(expectedState, actualState, "Estado do livess está errado.");
        assertNotNull(name, "Liveness está sem nome.");
    }

}
