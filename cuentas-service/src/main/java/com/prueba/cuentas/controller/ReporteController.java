package com.prueba.cuentas.controller;

import com.prueba.cuentas.dto.ReporteMovimientoDTO;
import com.prueba.cuentas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * F4: GET /reportes?clienteId=..&fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd
 * El enunciado ilustra el endpoint como "/reportes?fecha=rango fechas"; se
 * modela el rango con dos parametros explicitos para que sea validable en
 * Postman sin ambiguedad de formato.
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteMovimientoDTO>> estadoCuenta(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin));
    }
}
