package com.prueba.cuentas.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Debe mantenerse compatible con el evento publicado por personas-service.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEventDTO implements Serializable {

    private Long clienteId;
    private String nombre;
    private Boolean estado;
    private String tipoEvento;
}
