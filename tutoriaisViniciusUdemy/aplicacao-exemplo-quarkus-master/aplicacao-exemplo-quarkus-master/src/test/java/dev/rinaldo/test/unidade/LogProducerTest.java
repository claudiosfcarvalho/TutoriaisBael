package dev.rinaldo.test.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Member;

import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import dev.rinaldo.config.LogProducer;

/**
 * Testes unitários de {@link LogProducer}.
 * 
 * Especificações utilizadas:
 * - Junit5/Jupiter através de {@link Test}.
 * 
 * @see FrutasResourceTest
 * 
 * @author rinaldodev
 */
@ExtendWith(MockitoExtension.class)
public class LogProducerTest {

    @Test
    public void logProducer_InjectionPoint() {
        // given
        InjectionPoint ipMock = Mockito.mock(InjectionPoint.class);
        Member memberMock = Mockito.mock(Member.class);
        Mockito.when(ipMock.getMember()).thenReturn(memberMock);
        Mockito.when(memberMock.getDeclaringClass()).thenAnswer((im) -> LogProducerTest.class);

        LogProducer logProducer = new LogProducer();

        // when
        Logger logger = logProducer.produceLog(ipMock);
        String actual = logger.getName();

        // then
        String expected = LogProducerTest.class.getName();
        assertEquals(expected, actual, "Log gerado é diferente do que foi pedido.");
    }

    @Test
    public void logProducer_Classe() {
        // given

        // when
        Logger logger = LogProducer.produceLog(LogProducerTest.class);
        String actual = logger.getName();

        // then
        String expected = LogProducerTest.class.getName();
        assertEquals(expected, actual, "Log gerado é diferente do que foi pedido.");
    }

    @Test
    public void logProducer_Nome() {
        // given
        String expected = "LogProducerTest";

        // when
        Logger logger = LogProducer.produceLog(expected);
        String actual = logger.getName();

        // then
        assertEquals(expected, actual, "Log gerado é diferente do que foi pedido.");
    }

}
