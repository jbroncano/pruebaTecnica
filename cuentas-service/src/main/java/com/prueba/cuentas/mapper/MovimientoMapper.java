package com.prueba.cuentas.mapper;

import com.prueba.cuentas.domain.Movimiento;
import com.prueba.cuentas.dto.MovimientoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(source = "cuenta.id", target = "cuentaId")
    MovimientoResponseDTO toResponseDTO(Movimiento movimiento);
}
