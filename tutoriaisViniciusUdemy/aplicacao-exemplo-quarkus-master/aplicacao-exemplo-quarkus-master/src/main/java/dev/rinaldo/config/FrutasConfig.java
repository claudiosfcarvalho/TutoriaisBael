package dev.rinaldo.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * 
 * Essa classe guarda configurações do Resource de Frutas.
 * 
 * Especificações utilizadas:
 * - CDI para a anotação de {@link ApplicationScoped} e {@link Inject}.
 * - MicroProfile Config nas anotações de {@link ConfigProperty}.
 * 
 * É possível utilizar as anotações de {@link ConfigProperty} em outros locais, mas assim me parece que fica mais centralizada a
 * configuração de um Resource específico, além de ficar mais fácil de injetar em testes.
 * 
 * Esse formato também garante que as propriedades não serão alteradas em tempo de execução.
 * 
 * As propriedades são definidas no arquivo application.properties.
 * 
 * @author rinaldodev
 *
 */
@ApplicationScoped
public class FrutasConfig {

    private final boolean simularEspera;
    private final boolean simularExcecao;

    @Inject
    public FrutasConfig(
            @ConfigProperty(name = "frutas.simular-espera", defaultValue = "false") boolean simularEspera,
            @ConfigProperty(name = "frutas.simular-excecao", defaultValue = "false") boolean simularExcecao) {
        this.simularEspera = simularEspera;
        this.simularExcecao = simularExcecao;
    }

    public boolean isSimularEspera() {
        return simularEspera;
    }

    public boolean isSimularExcecao() {
        return simularExcecao;
    }

}
