package dev.rinaldo.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Um gerador de instâncias de loggers.
 * 
 * Especificações utilizadas:
 * - CDI para a anotação de {@link ApplicationScoped} e {@link Produces}
 * 
 * Sempre que alguém injetar um logger com a anotação {@link Inject}, o CDI usa esse produtor para gerar uma nova instância.
 * 
 * O SLF4J recomenda que o logger não seja estático (ou, no caso do CDI, Application Scoped):
 * http://www.slf4j.org/faq.html#declared_static
 * 
 * @author rinaldodev
 *
 */
@ApplicationScoped
public class LogProducer {

    @Produces
    public Logger produceLog(InjectionPoint ip) {
        return produceLog(ip.getMember().getDeclaringClass());
    }

    public static Logger produceLog(Class<?> declaringClass) {
        return produceLog(declaringClass.getName());
    }

    public static Logger produceLog(String className) {
        return LoggerFactory.getLogger(className);
    }

}
