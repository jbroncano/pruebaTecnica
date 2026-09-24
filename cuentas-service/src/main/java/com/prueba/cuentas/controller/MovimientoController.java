package com.prueba.cuentas.controller;

import com.prueba.cuentas.dto.MovimientoRequestDTO;
import com.prueba.cuentas.dto.MovimientoResponseDTO;
import com.prueba.cuentas.mapper.MovimientoMapper;
import com.prueba.cuentas.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final MovimientoMapper movimientoMapper;

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrar(@Valid @RequestBody MovimientoRequestDTO request) {
        var movimiento = movimientoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoMapper.toResponseDTO(movimiento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoMapper.toResponseDTO(movimientoService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody MovimientoRequestDTO request) {
        var movimiento = movimientoService.actualizar(id, request);
        return ResponseEntity.ok(movimientoMapper.toResponseDTO(movimiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
