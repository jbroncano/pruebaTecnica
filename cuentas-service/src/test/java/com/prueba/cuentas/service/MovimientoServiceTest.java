package com.prueba.cuentas.service;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.domain.Movimiento;
import com.prueba.cuentas.domain.TipoCuenta;
import com.prueba.cuentas.domain.TipoMovimiento;
import com.prueba.cuentas.dto.MovimientoRequestDTO;
import com.prueba.cuentas.exception.ResourceNotFoundException;
import com.prueba.cuentas.exception.SaldoNoDisponibleException;
import com.prueba.cuentas.repository.CuentaRepository;
import com.prueba.cuentas.repository.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * F2/F3: pruebas unitarias del calculo de saldo al registrar movimientos.
 */
@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    void registrar_deberiaLanzarSaldoNoDisponible_cuandoElRetiroSuperaElSaldo() {
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("100.00"))
                .saldoDisponible(new BigDecimal("100.00"))
                .estado(true)
                .clienteId(10L)
                .build();

        when(cuentaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cuenta));

        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .cuentaId(1L)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575.00"))
                .build();

        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");
    }

    @Test
    void registrar_deberiaActualizarSaldo_cuandoElDepositoEsValido() {
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("225487")
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("100.00"))
                .saldoDisponible(new BigDecimal("100.00"))
                .estado(true)
                .clienteId(20L)
                .build();

        when(cuentaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .cuentaId(1L)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("600.00"))
                .build();

        Movimiento resultado = movimientoService.registrar(request);

        assertThat(resultado.getValor()).isEqualByComparingTo("600.00");
        assertThat(resultado.getSaldo()).isEqualByComparingTo("700.00");
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("700.00");

        ArgumentCaptor<Cuenta> cuentaCaptor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().getSaldoDisponible()).isEqualByComparingTo("700.00");
    }

    @Test
    void registrar_deberiaLanzarNotFound_cuandoLaCuentaNoExiste() {
        when(cuentaRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .cuentaId(99L)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void registrar_deberiaPermitirRetiroQueDejaSaldoExactoEnCero() {
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("496825")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("540.00"))
                .saldoDisponible(new BigDecimal("540.00"))
                .estado(true)
                .clienteId(30L)
                .build();

        when(cuentaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .cuentaId(1L)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("540.00"))
                .build();

        Movimiento resultado = movimientoService.registrar(request);

        assertThat(resultado.getSaldo()).isEqualByComparingTo("0.00");
    }
}
