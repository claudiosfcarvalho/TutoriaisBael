[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/rinaldodev/aplicacao-exemplo-quarkus/javaci?style=for-the-badge)](https://github.com/rinaldodev/aplicacao-exemplo-quarkus/actions)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/rinaldodev_aplicacao-exemplo-quarkus?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/dashboard?id=rinaldodev_aplicacao-exemplo-quarkus)
[![GitHub last commit](https://img.shields.io/github/last-commit/rinaldodev/aplicacao-exemplo-quarkus?style=for-the-badge)](https://github.com/rinaldodev/aplicacao-exemplo-quarkus/commits/master)
[![GitHub](https://img.shields.io/github/license/rinaldodev/aplicacao-exemplo-quarkus?style=for-the-badge)](https://github.com/rinaldodev/aplicacao-exemplo-quarkus/blob/master/LICENSE)

# Aplicação de Exemplo Quarkus

Este projeto é uma base de exemplos para iniciar novos projetos utilizando Quarkus.

Algumas escolhas são baseadas na minha opinião sobre alguns conceitos, como:

- Focar na facilidade de executar testes unitários de verdade, separados dos testes de Quarkus.
- Focar em um design que facilite a execução de testes unitários com Mocks.
- Entre outros.

Então não entenda como "a melhor forma" de criar um projeto Quarkus, mas sim como um exemplo de utilização de:

- Extensões do Quarkus.
- Especificações do MicroProfile.
- Bibliotecas que acho que "encaixam bem" com o Quarkus.
- Padrões que considero mais razoáveis.

# Especificações MicroProfile, Extensões e Bibliotecas

A seguir é uma lista do que está sendo exemplificado neste projeto até o momento.

Especificação MicroProfile:
- JAX-RS
- JSON-B
- CDI
- MicroProfile Fault Tolerance
- MicroProfile Health Check
- MicroProfile Config

Extensões Quarkus (fora do MicroProfile):
- Hibernate Panache
- JDBC PostgreSQL
- OpenID Connect Adapter
	- Da forma como está implementada no código, poderia ser MicroProfile JWT. Apenas preferi utilizar a extensão de OpenID pois possui mais recursos e existe uma grande chance de você precisar utilizar em produção.

Bibliotecas externas:
- Log com SLF4J
- MapStruct
- Lombok

Testes Unitários:
- JUnit5/Jupiter
- Mockito
- JaCoCo

Testes de Mutação:
- Pitest (PIT)

Testes de Quarkus:
- Quarkus 
- Rest Assured
- TestContainers
- Elytron

Infra:
- Keycloak
- PostgreSQL

Aceito sugestões de novas extensões/bibliotecas para exemplificar. :)

# Configuração de IDE

- Esse projeto usa Maven, então importe como projeto Maven na sua IDE.
- Pelo uso de MapStruct, [o projeto requer que Java Annotation Processing esteja habilitado na IDE](https://mapstruct.org/documentation/ide-support/).
- Pelo uso de Lombok, [é necessário instalar o lombok como plugin na sua IDE](https://projectlombok.org/setup/overview).

# Executando

Esse projeto é fortemente contruído ao redor do [Quarkus](https://quarkus.io/)!

## Dependências

- Docker: É necessário ter o Docker instalado para rodar os testes integrados ou executar a aplicação, pois vários exemplos utilizam imagens do docker. [Instalação do Docker](https://docs.docker.com/install/).
- JDK 10~13: É necessário ter uma JDK entre as versões 10 e 13 disponível localmente e configurada no JAVA_HOME.

## Testes

Para executar todos os testes (de unidade, de mutação, e de quarkus):
```
./mvnw clean tests
```

### Testes de unidade

Para executar somente os testes de unidade:
```
./mvnw clean test -DskipMutationTests -DskipQuarkusTests
```

### Testes de mutação

Para executar somente os testes de mutação:
```
./mvnw clean test -DskipUnitTests -DskipQuarkusTests
```

### Testes de quarkus

Para executar somente os testes de quarkus:
```
./mvnw clean test -DskipMutationTests -DskipUnitTests
```

## Rodando a aplicação no modo dev

Execute todos os comandos na raiz do projeto.

1. Inicie um banco PostgreSQL em um container Docker com o comando:
```
docker run -e POSTGRES_PASSWORD=quarkus_dev -e POSTGRES_USER=quarkus_dev -e POSTGRES_DB=frutas -p 127.0.0.1:5432:5432/tcp postgres:9.6.12
```
2. Inicie o Keycloak em um container Docker com o comando (no windows talvez a variável ${PWD} seja diferente):
```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -e KEYCLOAK_IMPORT=/tmp/quarkus-realm.json -v ${PWD}/quarkus-realm.json:/tmp/quarkus-realm.json -p 8180:8080 jboss/keycloak
```
3. Execute a aplicação no modo dev, que permite live coding (salvou, tá visível), com o comando abaixo:
```
./mvnw quarkus:dev
```

Se tiver algum problema de classe/método/construtor inexistente, pode ser que as anotações do Lombok ou MapStruct não tenham sido processadas corretamente. Nesse caso, experimente fazer um `./mvnw compile quarkus:dev`.

## Empacotando

Para empacotar a aplicação utilize o comando `./mvnw clean package -DskipTests=true`.
Esse comando irá criar o jar `quarkus-example-app-X.X.X-runner.jar` no diretório `/target`.

Para executar a aplicação empacotada utilize o comando `java -jar target/quarkus-example-app-X.X.X-runner.jar`.

Perceba que a aplicação não precisa ser implantada em um servidor.

## Executável nativo

Com o Quarkus é possível criar imagens nativas, super leves e com tempo de inicialização super rápido. 
A princípio, esse não é o objetivo desse projeto de exemplo. Caso queira saber mais, [veja aqui](https://quarkus.io/guides/building-native-image-guide).

# Dúvidas de utilização

A maior parte do que está sendo usado contém documentação própria, então não convém explicar a utilização nesse projeto, apenas exemplificar. Abaixo estão apresentadas a maiorias das biliotecas utilizadas, suas funções básicas e suas respectivas documentações.

## Dúvidas de Quarkus ou MicroProfile

- [Quarkus](https://quarkus.io/): Framework poderoso para criar aplicações Java cloud-ready.
- [MicroProfile](https://microprofile.io/): Conjunto de especificações que facilitam a criação de Microsserviços.

## Dúvidas nos Testes de unidade

- [JUnit5/Jupiter](https://junit.org/junit5/docs/current/user-guide/): Framework para implementação e execução de testes.
- [Mockito](https://javadoc.io/static/org.mockito/mockito-core/3.2.4/org/mockito/Mockito.html): Framework para criar Mocks dentro dos testes.
- [JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/): Ferramenta para gerar relatórios de cobertura dos testes.

## Dúvidas nos Testes de mutação

- [Pitest (PIT)](https://pitest.org/): Ferramenta para gerar e executar testes de mutação.

## Dúvidas nos Testes de Quarkus

- [Quarkus](https://quarkus.io/guides/getting-started-testing): Quarkus também pode ser utilizado para rodar testes.
- [Rest Assured](https://github.com/rest-assured/rest-assured/wiki/usage): Framework para validação de endpoints rest.
- [TestContainers](https://www.testcontainers.org/quickstart/junit_5_quickstart/): Framework para criação de containers docker durante o teste.

# Quero contribuir!

Ótimo! [Veja como aqui](https://github.com/rinaldodev/aplicacao-exemplo-quarkus/blob/master/CONTRIBUTING.md).
