package com.prueba.personas.mapper;

import com.prueba.personas.domain.Cliente;
import com.prueba.personas.dto.ClienteResponseDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-24T13:22:05+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteResponseDTO.ClienteResponseDTOBuilder clienteResponseDTO = ClienteResponseDTO.builder();

        clienteResponseDTO.clienteId( cliente.getClienteId() );
        clienteResponseDTO.nombre( cliente.getNombre() );
        clienteResponseDTO.genero( cliente.getGenero() );
        clienteResponseDTO.edad( cliente.getEdad() );
        clienteResponseDTO.identificacion( cliente.getIdentificacion() );
        clienteResponseDTO.direccion( cliente.getDireccion() );
        clienteResponseDTO.telefono( cliente.getTelefono() );
        clienteResponseDTO.estado( cliente.getEstado() );

        return clienteResponseDTO.build();
    }
}
