package dev.rinaldo.test.unidade;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dev.rinaldo.config.FrutasConfig;

/**
 * Testes unitários de {@link FrutasConfig}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através de {@link Test}.
 * 
 * @see FrutasResourceTest
 * 
 * @author rinaldodev
 */
public class FrutasConfigTest {

    @Test
    public void criarConfigFrutas_simularEspera() {
        // given
        FrutasConfig frutasConfig = new FrutasConfig(true, false);

        // when
        boolean simularEspera = frutasConfig.isSimularEspera();

        // then
        assertTrue(simularEspera, "Simular espera deve ser true");
    }

    @Test
    public void criarConfigFrutas_simularExcecao() {
        // given
        FrutasConfig frutasConfig = new FrutasConfig(false, true);

        // when
        boolean simularExcecao = frutasConfig.isSimularExcecao();

        // then
        assertTrue(simularExcecao, "Simular excecao deve ser true");
    }

}
