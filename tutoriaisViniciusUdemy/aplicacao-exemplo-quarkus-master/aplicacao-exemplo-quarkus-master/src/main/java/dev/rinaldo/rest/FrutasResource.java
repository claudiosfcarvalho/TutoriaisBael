package dev.rinaldo.rest;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.slf4j.Logger;

import dev.rinaldo.config.FrutasConfig;
import dev.rinaldo.dao.FrutasDAO;
import dev.rinaldo.dominio.Fruta;
import dev.rinaldo.dto.FrutaDTO;
import dev.rinaldo.dto.mapper.FrutaMapper;

/**
 * Resource de Frutas.
 * 
 * Especificações utilizadas:
 * - JAX-RS e JSON-B através das anotações {@link Path}, {@link Produces}, {@link Consumes} e {@link GET}.
 * - CDI com {@link ApplicationScoped} e {@link Inject}.
 * - MicroProfile Fault Tolerance com {@link Timeout}, {@link Retry}, {@link CircuitBreaker} e {@link Fallback}.
 * - CDI/JTA com {@link Transactional}.
 * - OpenID Connect com {@link PermitAll}, {@link RolesAllowed}.
 * -- Poderia muito bem ser MicroProfile JWT. O código seria o mesmo, pois as roles estariam no token JWT.
 * -- Apenas preferi utilizar a extensão que já usa o protocolo OpenID Connect, que fornece mais recursos do que o JWT puro.
 * 
 * 
 * Aqui todas as dependências da classe são injetadas via construtor. Isso facilita a execução de teste unitários sem CDI, pois
 * basta criar uma nova instância da classe passando Mocks como parâmetros.
 * 
 * Essa é a classe que mais deve conter testes unitários. De preferência algo próximo a 100% de cobertura.
 * 
 * É possível criar DTOs específicas para operações específicas, removendo ou acrescentando atributos que não constam na DTO que
 * representa a entidade original. Isso pode inclusive diminuir significativamente o processamento de JSON e a quantidade de
 * dados trafegados. Ainda não está feito aqui, mas há uma Issue para isso.
 * 
 * @author rinaldodev
 *
 */
@Path("/frutas")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FrutasResource {

    /**
     * vai simular espera de 1 segundo de forma alternada: uma chamada sim, outra não. Veja a utilização nos métodos ao final.
     */
    private boolean simularEspera = false;

    /**
     * vai simular uma exceção de forma alternada: uma chamada sim, outra não. Veja a utilização nos métodos ao final.
     */
    private boolean simularExcecao = false;

    /**
     * Dependências injetadas no construtor.
     */
    private final Logger logger;
    private final FrutasDAO frutasDAO;
    private final FrutasConfig frutasConfig;
    private final FrutaMapper frutaMapper;

    @Inject
    public FrutasResource(
            FrutasDAO frutasDAO,
            Logger logger,
            FrutasConfig frutasConfig,
            FrutaMapper frutaMapper) {
        this.logger = logger;
        this.frutasDAO = frutasDAO;
        this.frutasConfig = frutasConfig;
        this.frutaMapper = frutaMapper;
    }

    /**
     * Exemplo de método com Fault Tolerance.
     * 
     * O {@link Timeout} garante que, caso o método não responsa em menos de 1 segundo, lança um erro.
     * 
     * O {@link Retry} garante que, em caso de erro, o método tentará ser executado mais uma vez.
     * 
     * O {@link CircuitBreaker} garante, em caso de muito erros consecutivos, o serviço para de responder imediatamente por
     * algum tempo, enquanto se recupera.
     * 
     * O {@link PermitAll} torna o método público, não requer autenticação.
     */
    @GET
    @Timeout(value = 1000)
    @Retry(maxRetries = 1)
    @CircuitBreaker
    @PermitAll
    public List<FrutaDTO> get() {
        talvezEspere1Seg();
        List<Fruta> frutas = frutasDAO.listAll();
        return frutaMapper.toResourceList(frutas);
    }

    /**
     * Exemplo de método com Fault Tolerance.
     * 
     * O {@link Retry} garante que, em caso de erro, o método tentará ser executado mais uma vez.
     * 
     * O {@link Fallback} garante que, em caso de erro no método, outro método será chamado no lugar. Isso ocorre apenas depois
     * do {@link Retry}.
     * 
     * O {@link CircuitBreaker} garante, em caso de muito erros consecutivos, o serviço passa a usar imediatamente o método de
     * Fallback, enquanto o método original não se recupera.
     * 
     * O {@link PermitAll} torna o método público, não requer autenticação.
     */
    @GET
    @Path("maisVotadas")
    @Retry(maxRetries = 1)
    @Fallback(fallbackMethod = "fallbackFrutasMaisVotadas")
    @CircuitBreaker
    @PermitAll
    public List<FrutaDTO> getMaisVotadas() {
        logger.trace("GET frutas mais votadas.");
        talvezLanceExcecao();
        List<Fruta> maisVotadas = frutasDAO.findMaisVotadas();
        return frutaMapper.toResourceList(maisVotadas);
    }

    public List<FrutaDTO> fallbackFrutasMaisVotadas() {
        // no fallback retornamos uma Ameixa porque, mesmo sem consultar a base, sabemos que é a melhor fruta :)
        Fruta frutaMaisVotada = new Fruta();
        frutaMaisVotada.setNome("Ameixa");
        FrutaDTO dto = frutaMapper.toResource(frutaMaisVotada);
        return List.of(dto);
    }

    /**
     * Exemplo de método com Autenticação e Transação.
     * 
     * O {@link RolesAllowed} informa quais Roles podem acessar esses recurso. No caso, apenas usuários com a role "user".
     * 
     * O {@link Transactional} informa que esse método deve conter uma transação, pois faz um DELETE na base de dados.
     * 
     * O {@link Path} diz que a URL deve ser /frutas/{id_da_fruta}.
     * 
     * Perceba que o retorno é diferente, pois depende se conseguimos ou não encontrar a entidade para apagar.
     */
    @DELETE
    @RolesAllowed("user")
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        long delete = frutasDAO.delete("id", id);
        Status status = delete == 0 ? Status.NOT_FOUND : Status.NO_CONTENT;
        return Response.status(status).build();
    }

    // --- Os métodos abaixo são somente para simulação de erros/timeout ---

    private void talvezLanceExcecao() {
        if (!frutasConfig.isSimularExcecao()) {
            return;
        }

        if (simularExcecao) {
            simularExcecao = false;
            logger.error("Simulando Excecao!");
            throw new RuntimeException("Erro!");
        } else {
            simularExcecao = true;
        }
    }

    private void talvezEspere1Seg() {
        if (!frutasConfig.isSimularEspera()) {
            return;
        }

        if (simularEspera) {
            simularEspera = false;
            logger.error("Simulando espera de 1 segundo!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
        } else {
            simularEspera = true;
        }
    }

}
