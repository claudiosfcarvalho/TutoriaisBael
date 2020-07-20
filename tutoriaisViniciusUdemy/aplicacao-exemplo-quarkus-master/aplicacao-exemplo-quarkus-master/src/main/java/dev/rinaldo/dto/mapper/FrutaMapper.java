package dev.rinaldo.dto.mapper;

import java.util.List;

import javax.persistence.Entity;

import org.mapstruct.Mapper;

import dev.rinaldo.dominio.Fruta;
import dev.rinaldo.dto.FrutaDTO;

/**
 * Exemplo de Mapper de {@link Entity} para DTO.
 * 
 * Especificações utilizadas:
 * - MapStruct através da anotação {@link Mapper}.
 * 
 * Essa classe utiliza o MapStruct para facilitar o mapeamento de atributos entre classes Java. Nesse caso, entre {@link Fruta}
 * e {@link FrutaDTO}.
 * 
 * A classe pode ser injetada pelo CDI onde for utilizada, por conta do componentModel = "cdi".
 * 
 * @author rinaldodev
 *
 */
@Mapper(componentModel = "cdi")
public interface FrutaMapper {

    FrutaDTO toResource(Fruta fruta);

    List<FrutaDTO> toResourceList(List<Fruta> frutas);

}
