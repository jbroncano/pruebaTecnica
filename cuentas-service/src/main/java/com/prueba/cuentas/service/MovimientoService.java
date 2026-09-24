package com.prueba.cuentas.service;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.domain.Movimiento;
import com.prueba.cuentas.domain.TipoMovimiento;
import com.prueba.cuentas.dto.MovimientoRequestDTO;
import com.prueba.cuentas.exception.ResourceNotFoundException;
import com.prueba.cuentas.exception.SaldoNoDisponibleException;
import com.prueba.cuentas.repository.CuentaRepository;
import com.prueba.cuentas.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    /**
     * F2/F3: registra un movimiento y actualiza el saldo disponible de forma
     * atomica. Se bloquea la fila de la cuenta (PESSIMISTIC_WRITE) para evitar
     * que dos movimientos concurrentes sobre la misma cuenta lean el mismo
     * saldo de partida y produzcan un resultado inconsistente.
     */
    @Transactional
    public Movimiento registrar(MovimientoRequestDTO request) {
        Cuenta cuenta = cuentaRepository.findByIdForUpdate(request.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + request.getCuentaId()));

        BigDecimal valorConSigno = request.getTipoMovimiento() == TipoMovimiento.RETIRO
                ? request.getValor().negate()
                : request.getValor();

        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(valorConSigno);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException();
        }

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(request.getTipoMovimiento())
                .valor(valorConSigno)
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        return movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public Movimiento obtenerPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
    }

    @Transactional
    public Movimiento actualizar(Long id, MovimientoRequestDTO request) {
        Movimiento movimiento = obtenerPorId(id);
        Cuenta cuenta = cuentaRepository.findByIdForUpdate(movimiento.getCuenta().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada: " + movimiento.getCuenta().getId()));

        BigDecimal nuevoValorConSigno = request.getTipoMovimiento() == TipoMovimiento.RETIRO
                ? request.getValor().negate()
                : request.getValor();

        BigDecimal saldoSinMovimientoOriginal = cuenta.getSaldoDisponible().subtract(movimiento.getValor());
        BigDecimal nuevoSaldo = saldoSinMovimientoOriginal.add(nuevoValorConSigno);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException();
        }

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(nuevoValorConSigno);
        movimiento.setSaldo(nuevoSaldo);

        return movimientoRepository.save(movimiento);
    }

    @Transactional
    public void eliminar(Long id) {
        Movimiento movimiento = obtenerPorId(id);
        Cuenta cuenta = cuentaRepository.findByIdForUpdate(movimiento.getCuenta().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada: " + movimiento.getCuenta().getId()));

        BigDecimal saldoRevertido = cuenta.getSaldoDisponible().subtract(movimiento.getValor());
        cuenta.setSaldoDisponible(saldoRevertido);
        cuentaRepository.save(cuenta);

        movimientoRepository.delete(movimiento);
    }
}
