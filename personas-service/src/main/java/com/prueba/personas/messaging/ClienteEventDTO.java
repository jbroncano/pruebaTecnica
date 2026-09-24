package com.prueba.personas.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Contrato de evento publicado hacia cuentas-service. Debe mantenerse
 * compatible con el listener del otro microservicio.
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
