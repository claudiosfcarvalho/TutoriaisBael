package dev.rinaldo.dominio;

import javax.persistence.Entity;

import org.hibernate.annotations.NaturalId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Exemplo de {@link Entity}.
 * 
 * Especificações utilizadas:
 * - JPA e Hibernate através das anotações {@link Entity} e {@link NaturalId}.
 * 
 * Veja a classe {@link AbstractEntidade} para mais informações sobre padrões de implementação.
 * 
 * Para este exemplo, consideramos que nome é um ID natural, e que não pode ser alterado.
 * 
 * @author rinaldodev
 *
 */
@Entity
@Data
@ToString
@EqualsAndHashCode(of = "nome", callSuper = false)
public class Fruta extends AbstractEntidade {

    @NaturalId
    private String nome;

    private Integer votos;

    public String getNome() {
        return nome;
    }

    public void setNome(String name) {
        this.nome = name;
    }

    public Integer getVotos() {
        return votos;
    }

    public void setVotos(Integer votos) {
        this.votos = votos;
    }

}
