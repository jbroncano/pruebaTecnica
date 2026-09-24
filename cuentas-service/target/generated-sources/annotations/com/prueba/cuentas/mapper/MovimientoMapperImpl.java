package com.prueba.cuentas.mapper;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.domain.Movimiento;
import com.prueba.cuentas.dto.MovimientoResponseDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-24T13:28:57+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Eclipse Adoptium)"
)
@Component
public class MovimientoMapperImpl implements MovimientoMapper {

    @Override
    public MovimientoResponseDTO toResponseDTO(Movimiento movimiento) {
        if ( movimiento == null ) {
            return null;
        }

        MovimientoResponseDTO.MovimientoResponseDTOBuilder movimientoResponseDTO = MovimientoResponseDTO.builder();

        movimientoResponseDTO.cuentaId( movimientoCuentaId( movimiento ) );
        movimientoResponseDTO.id( movimiento.getId() );
        movimientoResponseDTO.fecha( movimiento.getFecha() );
        movimientoResponseDTO.tipoMovimiento( movimiento.getTipoMovimiento() );
        movimientoResponseDTO.valor( movimiento.getValor() );
        movimientoResponseDTO.saldo( movimiento.getSaldo() );

        return movimientoResponseDTO.build();
    }

    private Long movimientoCuentaId(Movimiento movimiento) {
        if ( movimiento == null ) {
            return null;
        }
        Cuenta cuenta = movimiento.getCuenta();
        if ( cuenta == null ) {
            return null;
        }
        Long id = cuenta.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
