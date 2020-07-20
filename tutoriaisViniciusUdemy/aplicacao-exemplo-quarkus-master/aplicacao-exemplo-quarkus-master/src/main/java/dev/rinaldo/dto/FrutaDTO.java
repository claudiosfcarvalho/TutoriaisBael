package dev.rinaldo.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Exemplo de DTO.
 * 
 * Especificações utilizadas:
 * - Lombok com as anotações @Value e @Builder.
 * 
 * Essa classe utiliza o conceito de Imutabilidade, utilizando as anotações {@link Value} e {@link Builder} para tornar os
 * atributos private,
 * final, gerar getters, toString, equals, hashCode, criar um construtor privado com todos os argumentos e permitir que o
 * MapStruct crie essa classe a partir do Builder que será gerado.
 * 
 * @author rinaldodev
 *
 */
@Value
@Builder
public class FrutaDTO {

    Long id;
    String nome;
    Integer votos;

}
