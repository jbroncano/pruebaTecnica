package com.prueba.cuentas.exception;

/**
 * Se lanza cuando el clienteId referenciado no existe o no esta activo en la
 * proyeccion local (cliente_ref), mantenida via eventos asincronos.
 */
public class ClienteInvalidoException extends RuntimeException {

    public ClienteInvalidoException(String message) {
        super(message);
    }
}
