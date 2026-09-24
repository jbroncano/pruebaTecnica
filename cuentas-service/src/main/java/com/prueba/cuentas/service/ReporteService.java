package com.prueba.cuentas.service;

import com.prueba.cuentas.domain.ClienteRef;
import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.domain.Movimiento;
import com.prueba.cuentas.dto.ReporteMovimientoDTO;
import com.prueba.cuentas.exception.ClienteInvalidoException;
import com.prueba.cuentas.repository.ClienteRefRepository;
import com.prueba.cuentas.repository.CuentaRepository;
import com.prueba.cuentas.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ClienteRefRepository clienteRefRepository;

    /**
     * F4: reporte de estado de cuenta para un cliente en un rango de fechas.
     * Cada fila representa un movimiento, con la cuenta y el cliente asociados.
     */
    @Transactional(readOnly = true)
    public List<ReporteMovimientoDTO> generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        ClienteRef clienteRef = clienteRefRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteInvalidoException(
                        "El cliente " + clienteId + " no existe o aun no ha sido sincronizado"));

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        if (cuentas.isEmpty()) {
            return List.of();
        }

        Map<Long, Cuenta> cuentasPorId = cuentas.stream()
                .collect(Collectors.toMap(Cuenta::getId, cuenta -> cuenta));

        LocalDateTime desde = fechaInicio.atStartOfDay();
        LocalDateTime hasta = fechaFin.atTime(23, 59, 59);

        List<Movimiento> movimientos = movimientoRepository
                .findByCuentaIdInAndFechaBetweenOrderByFechaAsc(cuentasPorId.keySet().stream().toList(), desde, hasta);

        return movimientos.stream()
                .map(movimiento -> {
                    Cuenta cuenta = cuentasPorId.get(movimiento.getCuenta().getId());
                    return ReporteMovimientoDTO.builder()
                            .fecha(movimiento.getFecha())
                            .cliente(clienteRef.getNombre())
                            .numeroCuenta(cuenta.getNumeroCuenta())
                            .tipo(cuenta.getTipoCuenta())
                            .saldoInicial(cuenta.getSaldoInicial())
                            .estado(cuenta.getEstado())
                            .movimiento(movimiento.getValor())
                            .saldoDisponible(movimiento.getSaldo())
                            .build();
                })
                .toList();
    }
}
