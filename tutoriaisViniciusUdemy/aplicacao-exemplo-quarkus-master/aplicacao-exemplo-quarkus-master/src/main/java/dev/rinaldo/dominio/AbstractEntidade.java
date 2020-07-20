package dev.rinaldo.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.NaturalId;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe base para todas as {@link Entity}.
 * 
 * Especificações utilizadas:
 * - JPA através das anotações {@link MappedSuperclass}, {@link Id} e {@link GeneratedValue}.
 * 
 * Gosto de usar o padrão de uma entidade abstrata que tenha sempre o campo ID. Dessa forma não é necessário declarar nas
 * subclasses. Não gosto de utilizar chaves de negócio, pois reduzem a flexibilidade de refactor e mudanças de regras de
 * negócio.
 * 
 * Essa classe também força a subclasses, no caso as entidades, a implementarem seu {@link Object#equals(Object)} e
 * {@link Object#hashCode()}.
 * 
 * No Hibernate, penso que o ideal é anotar os campos que seriam chaves negociais com {@link NaturalId} e utilizá-los no equals
 * e hashcode.
 * 
 * @author rinaldodev
 *
 */
@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class AbstractEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

}
