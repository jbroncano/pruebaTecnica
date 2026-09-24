package com.prueba.cuentas.repository;

import com.prueba.cuentas.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaIdInAndFechaBetweenOrderByFechaAsc(
            List<Long> cuentaIds, LocalDateTime desde, LocalDateTime hasta);

    List<Movimiento> findByCuentaIdOrderByFechaAsc(Long cuentaId);
}
