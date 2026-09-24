package com.prueba.cuentas.mapper;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.dto.CuentaResponseDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-24T13:28:57+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Eclipse Adoptium)"
)
@Component
public class CuentaMapperImpl implements CuentaMapper {

    @Override
    public CuentaResponseDTO toResponseDTO(Cuenta cuenta) {
        if ( cuenta == null ) {
            return null;
        }

        CuentaResponseDTO.CuentaResponseDTOBuilder cuentaResponseDTO = CuentaResponseDTO.builder();

        cuentaResponseDTO.id( cuenta.getId() );
        cuentaResponseDTO.numeroCuenta( cuenta.getNumeroCuenta() );
        cuentaResponseDTO.tipoCuenta( cuenta.getTipoCuenta() );
        cuentaResponseDTO.saldoInicial( cuenta.getSaldoInicial() );
        cuentaResponseDTO.saldoDisponible( cuenta.getSaldoDisponible() );
        cuentaResponseDTO.estado( cuenta.getEstado() );
        cuentaResponseDTO.clienteId( cuenta.getClienteId() );

        return cuentaResponseDTO.build();
    }
}
