package com.prueba.cuentas.exception;

/**
 * F3: se lanza cuando un retiro dejaria el saldo de la cuenta en negativo.
 */
public class SaldoNoDisponibleException extends RuntimeException {

    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
