package dev.rinaldo.test.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.rinaldo.config.ReadinessCheck;
import dev.rinaldo.dao.FrutasDAO;
import dev.rinaldo.dominio.Fruta;

/**
 * Testes unitários de {@link ReadinessCheck}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através de {@link Test}.
 * 
 * @see FrutasResourceTest
 * 
 * @author rinaldodev
 */
@ExtendWith(MockitoExtension.class)
public class ReadinessCheckTest {

    @Mock
    private FrutasDAO frutasDAO;

    @BeforeEach
    public void assumptions() {
        assumeTrue(frutasDAO != null, "frutasDAO não foi inicializado.");
    }

    @Test
    public void readiness_UP() {
        // given
        when(frutasDAO.findById(anyLong())).thenReturn(new Fruta());
        ReadinessCheck readinessCheck = new ReadinessCheck(frutasDAO);

        // when
        HealthCheckResponse readiness = readinessCheck.call();
        State actualState = readiness.getState();
        String name = readiness.getName();

        // then
        State expectedState = State.UP;
        assertEquals(expectedState, actualState, "Estado do readiness está errado.");
        assertNotNull(name, "O readiness está sem nome.");
    }

    @Test
    public void readiness_DOWN() {
        // given
        when(frutasDAO.findById(anyLong())).thenThrow(RuntimeException.class);
        ReadinessCheck readinessCheck = new ReadinessCheck(frutasDAO);

        // when
        HealthCheckResponse readiness = readinessCheck.call();
        State actualState = readiness.getState();
        String name = readiness.getName();

        // then
        State expectedState = State.DOWN;
        assertEquals(expectedState, actualState, "Estado do readiness está errado.");
        assertNotNull(name, "O readiness está sem nome.");
    }

}
