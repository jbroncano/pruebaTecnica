package com.prueba.personas.mapper;

import com.prueba.personas.domain.Cliente;
import com.prueba.personas.dto.ClienteResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponseDTO toResponseDTO(Cliente cliente);
}
