package dev.rinaldo.test.integracao;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import dev.rinaldo.rest.FrutasResource;
import dev.rinaldo.test.unidade.FrutasResourceTest;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * Testes integrados de {@link FrutasResource}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através da maior parte das anotações.
 * - Quarkus com {@link QuarkusTest}.
 * - Testcontainers com {@link Testcontainers}.
 * 
 * Leia primeiro a documentação dos testes unitários: {@link FrutasResourceTest}.
 * 
 * Essa classe faz os testes de integração, e requer que o ambiente seja simulado. Estamos usando TestContainers para simular a
 * base de dados, e poderíamos simular também outros recursos externos que possuem imagem docker.
 * 
 * Se não for desejado/necessário testar alguns recursos externos, basta criar um Mock CDI com a anotação do Quarkus:
 * {@link Mock}. Lembre-se que o mesmo mock será utilizado em todos os testes.
 * 
 * A quantidade de testes integrados deve ser muito menos do que a de unitários, pois são mais lentos e caros de desenvolver.
 * Eles são executados em uma fase diferente da build.
 * 
 * A autenticação de usuários para testes fica no application.properties, nas propriedades "quarkus.security.users.file".
 * 
 * @author rinaldodev
 *
 */
@QuarkusTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FrutasResourceIT {

    @SuppressWarnings("rawtypes")
    @Container
    private static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer<>()
            .withDatabaseName("frutas")
            .withUsername("quarkus_it")
            .withPassword("quarkus_it")
            .withExposedPorts(5431);

    @BeforeAll
    private static void configure() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        System.setProperty("quarkus.datasource.url", postgresqlContainer.getJdbcUrl());
    }

    @AfterAll
    private static void cleanup() {
        System.clearProperty("quarkus.datasource.url");
    }

    @BeforeEach
    void setup() {
        assumeTrue(postgresqlContainer.isRunning(), "Banco não está no ar.");
    }

    @Test
    @Order(1)
    public void dado_FrutasNaBase_quando_BuscarFrutas_entao_DeveRetornar200ComIdsNomesVotos() {
        given()
                .when()
                .get("/frutas")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", hasItems(1, 2, 3, 4, 5, 6, 7))
                .body("nome", hasSize(greaterThanOrEqualTo(7)))
                .body("votos", hasSize(greaterThanOrEqualTo(7)));
    }

    @Test
    @Order(2)
    public void dado_FrutasNaBase_quando_BuscarFrutasMaisVotadas_entao_DeveRetornar200Com3Frutas() {
        given()
                .when()
                .get("/frutas/maisVotadas")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", contains(3, 6, 4))
                .body("nome", hasSize(3))
                .body("votos", hasSize(3));
    }

    @Test
    @Order(3)
    public void dado_Frutas1NaBase_quando_ApagarFruta1_entao_DeveRetornar204() {
        given()
                .auth().preemptive().basic("alice", "alice123")
                .when()
                .delete("/frutas/1")
                .then()
                .statusCode(204);
    }

}
