package com.prueba.personas.messaging;

import com.prueba.personas.domain.Cliente;

public record ClienteDomainEvent(Cliente cliente, Tipo tipo) {

    public enum Tipo {
        CREADO, ACTUALIZADO, ELIMINADO
    }
}
