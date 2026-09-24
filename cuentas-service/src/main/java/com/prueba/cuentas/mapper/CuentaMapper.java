package com.prueba.cuentas.mapper;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.dto.CuentaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaResponseDTO toResponseDTO(Cuenta cuenta);
}
