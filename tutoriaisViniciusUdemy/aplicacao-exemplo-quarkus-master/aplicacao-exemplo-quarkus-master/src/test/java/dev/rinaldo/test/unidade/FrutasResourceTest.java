package dev.rinaldo.test.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import dev.rinaldo.config.FrutasConfig;
import dev.rinaldo.config.LogProducer;
import dev.rinaldo.dao.FrutasDAO;
import dev.rinaldo.dominio.Fruta;
import dev.rinaldo.dto.FrutaDTO;
import dev.rinaldo.dto.mapper.FrutaMapper;
import dev.rinaldo.rest.FrutasResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Testes unitários de {@link FrutasResource}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através da maior parte das anotações.
 * - Mockito com {@link MockitoExtension} e {@link Mock}.
 * 
 * Percebam que não há a anotação do {@link QuarkusTest} ou qualquer outra coisa que inicie um contexto. Isso é proposital, e
 * garante que a única dependência dos testes é o Mockito. Isso faz com que os testes sejam executados de forma extremamente
 * rápida, facilitando o uso de TDD e a implementação de vários testes de unidade sem demora, até mesmo para a própria build do
 * projeto também ser rápida.
 * 
 * Basta criar uma instância de {@link FrutasResource} passando as dependências como parâmetro. Podem ser mocks ou não. Para
 * coisas simples e que não afetam no nosso teste, não há necessidade de criar mocks. Para acesso à base de dados, por exemplo,
 * que é o caso da DAO, recomendo o uso de mocks.
 * 
 * Perceba o padrão de given/when/then. Isso garante o entendimento do que está sendo testado.
 * 
 * - given é basicamente o setup do teste, realizando as pré-condições para executar o teste, criar mocks, etc.
 * - when é a sua funcionalidade em si, o que você quer testar.
 * - then é a validação do que foi feito, seus asserts.
 * 
 * @author rinaldodev
 *
 */
@ExtendWith(MockitoExtension.class)
public class FrutasResourceTest {

    private final FrutasConfig frutasConfigVazio = new FrutasConfig(false, false);
    private final FrutasConfig frutasConfigComEspera = new FrutasConfig(true, false);
    private final FrutasConfig frutasConfigComExcecao = new FrutasConfig(false, true);

    private final Logger logger = LogProducer.produceLog(getClass());

    private final FrutaMapper frutaMapper = Mappers.getMapper(FrutaMapper.class);

    @Mock
    private FrutasDAO frutasDAO;

    @BeforeEach
    public void assumptions() {
        assumeTrue(logger != null, "logger não foi inicializado.");
        assumeTrue(frutasDAO != null, "mock DAO de frutas não foi inicializado.");
    }

    @Test
    public void listarTodasAsFrutas_PoucasFrutas() {
        // given
        Fruta fruta1 = new Fruta();
        fruta1.setId(1L);
        Fruta fruta2 = new Fruta();
        fruta2.setId(2L);
        Fruta fruta3 = new Fruta();
        fruta3.setId(3L);

        final List<Fruta> frutasList = Arrays.asList(fruta1, fruta2, fruta3);
        when(frutasDAO.listAll()).thenReturn(frutasList);

        final FrutasResource frutasResource = newFrutasResource();

        // when
        final List<FrutaDTO> actual = frutasResource.get();

        // then
        final List<FrutaDTO> expected = frutaMapper.toResourceList(frutasList);
        verify(frutasDAO, times(1)).listAll();
        assertEquals(expected.size(), actual.size(), "O tamanho da lista retornada é diferente do que foi colocado no mock.");
        assertEquals(Set.copyOf(expected), Set.copyOf(actual), "As listas contém itens diferentes, mas deveriam ser iguais."); // compara um SET para que a ordem seja ignorada
    }

    @Test
    public void listarTodasAsFrutas_SimularEspera_PrimeiraChamada() {
        // given
        FrutasResource frutasResource = newFrutasResource(frutasConfigComEspera);

        // when
        Executable executable = () -> frutasResource.get();

        // then
        Duration timeout = Duration.ofSeconds(1L);
        assertTimeout(timeout, executable,
                "Primeira execução demorou mais de 1 segundo, mas não deveria não deve haver espera de 1 segundo.");
    }

    @Test
    public void listarTodasAsFrutas_SimularEspera_SegundaChamada() {
        // given
        FrutasResource frutasResource = newFrutasResource(frutasConfigComEspera);

        // when
        frutasResource.get();
        Instant inicio = Instant.now();
        frutasResource.get();
        Instant fim = Instant.now();

        // then
        long duracaoAtual = Duration.between(inicio, fim).toMillis();
        long duracaoMaxima = 1000;
        assertTrue(duracaoAtual >= duracaoMaxima,
                "Segunda execução demorou menos de 1 segundo, quer dizer que não esperou 1 segundo: " + duracaoAtual);
    }

    @Test
    public void listarTodasAsFrutas_SimularEspera_TerceiraChamada() {
        // setup
        FrutasResource frutasResource = newFrutasResource(frutasConfigComEspera);

        // when
        frutasResource.get();
        frutasResource.get();
        Executable executable = () -> frutasResource.get();

        // then
        Duration timeout = Duration.ofSeconds(1L);
        assertTimeout(timeout, executable,
                "Terceira execução demorou mais de 1 segundo, mas não deveria não deve haver espera de 1 segundo.");
    }

    @Test
    public void listarMaisVotadas() {
        // setup
        Fruta fruta1 = new Fruta();
        fruta1.setId(1L);
        Fruta fruta2 = new Fruta();
        fruta2.setId(2L);
        Fruta fruta3 = new Fruta();
        fruta3.setId(3L);

        List<Fruta> frutas = Arrays.asList(fruta1, fruta2, fruta3);
        when(frutasDAO.findMaisVotadas()).thenReturn(frutas);

        FrutasResource frutasResource = newFrutasResource();

        // when
        List<FrutaDTO> actual = frutasResource.getMaisVotadas();

        // then
        List<FrutaDTO> expected = frutaMapper.toResourceList(frutas);
        verify(frutasDAO, times(1)).findMaisVotadas();
        assertEquals(expected.size(), actual.size(), "O tamanho da lista retornada é diferente do que foi colocado no mock.");
        assertEquals(Set.copyOf(expected), Set.copyOf(actual), "As listas contém itens diferentes, mas deveriam ser iguais."); // compara um SET para que a ordem seja ignorada
    }

    @Test
    public void listarMaisVotadas_SimularExcecao_PrimeiraChamada() {
        // setup
        FrutasResource frutasResource = newFrutasResource(frutasConfigComExcecao);

        // when
        List<FrutaDTO> maisVotadas = frutasResource.getMaisVotadas();

        // then
        assertNotNull(maisVotadas, "Não retornou nenhuma lista, mas deveria ter retornado algo.");
    }

    @Test
    public void listarMaisVotadas_SimularExcecao_SegundaChamada() {
        // setup
        FrutasResource frutasResource = newFrutasResource(frutasConfigComExcecao);

        // when
        frutasResource.getMaisVotadas();
        Executable executable = () -> frutasResource.getMaisVotadas();

        // then
        assertThrows(RuntimeException.class, executable,
                "Não lançou exceção na segunda vez, mas deveria porque estamos simulando exceção.");
    }

    @Test
    public void listarMaisVotadas_SimularExcecao_TerceiraChamada() {
        // setup
        FrutasResource frutasResource = newFrutasResource(frutasConfigComExcecao);

        // when
        try {
            frutasResource.getMaisVotadas();
            frutasResource.getMaisVotadas();
        } catch (RuntimeException e) {
        }
        List<FrutaDTO> maisVotadas = frutasResource.getMaisVotadas();

        // then
        assertNotNull(maisVotadas, "Não retornou nenhuma lista na terceira chamada, mas deveria ter retornado algo.");
    }

    @Test
    public void fallbackFrutasMaisVotadas() {
        // setup
        FrutasResource frutasResource = newFrutasResource(frutasConfigComExcecao);

        // when
        List<FrutaDTO> maisVotadas = frutasResource.fallbackFrutasMaisVotadas();

        // then
        assertNotNull(maisVotadas, "Não retornou nenhuma lista no fallback, mas deveria ter retornado algo.");
        assertFalse(maisVotadas.isEmpty(), "Retornou uma lista vazia no fallback.");
        String expectedNomeFruta = "Ameixa";
        assertEquals(expectedNomeFruta, maisVotadas.get(0).getNome(), "Nome da fruta não era o esperado.");
    }

    @Test
    public void apagarFruta_Existente() {
        // setup
        FrutasResource frutasResource = newFrutasResource();
        when(frutasDAO.delete(anyString(), anyLong())).thenReturn(1L);
        int expectedStatus = Status.NO_CONTENT.getStatusCode();

        // when
        Response deleteResponse = frutasResource.delete(1L);

        // then
        assertNotNull(deleteResponse, "Não retornou nenhuma response ao deletar uma fruta existente, mas deveria.");
        assertEquals(expectedStatus, deleteResponse.getStatus(), "Retornou status errado ao apagar uma fruta existente.");
    }

    @Test
    public void apagarFruta_Inexistente() {
        // setup
        FrutasResource frutasResource = newFrutasResource();
        when(frutasDAO.delete(anyString(), anyLong())).thenReturn(0L);
        int expectedStatus = Status.NOT_FOUND.getStatusCode();

        // when
        Response deleteResponse = frutasResource.delete(1L);

        // then
        assertNotNull(deleteResponse, "Não retornou nenhuma response ao deletar uma fruta inexistente, mas deveria.");
        assertEquals(expectedStatus, deleteResponse.getStatus(), "Retornou status errado ao apagar uma fruta inexistente.");
    }

    private FrutasResource newFrutasResource() {
        return newFrutasResource(frutasConfigVazio);
    }

    private FrutasResource newFrutasResource(FrutasConfig frutasConfig) {
        FrutasResource frutasResource = new FrutasResource(frutasDAO, logger, frutasConfig, frutaMapper);
        return frutasResource;
    }

}
