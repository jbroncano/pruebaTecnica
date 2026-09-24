package com.prueba.cuentas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prueba.cuentas.domain.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Una fila del reporte de "Estado de cuenta" (F4), con el mismo esquema
 * ilustrado en el enunciado (Fecha, Cliente, Numero Cuenta, Tipo, Saldo Inicial,
 * Estado, Movimiento, Saldo Disponible).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteMovimientoDTO {

    @JsonProperty("Fecha")
    private LocalDateTime fecha;

    @JsonProperty("Cliente")
    private String cliente;

    @JsonProperty("NumeroCuenta")
    private String numeroCuenta;

    @JsonProperty("Tipo")
    private TipoCuenta tipo;

    @JsonProperty("SaldoInicial")
    private BigDecimal saldoInicial;

    @JsonProperty("Estado")
    private Boolean estado;

    @JsonProperty("Movimiento")
    private BigDecimal movimiento;

    @JsonProperty("SaldoDisponible")
    private BigDecimal saldoDisponible;
}
