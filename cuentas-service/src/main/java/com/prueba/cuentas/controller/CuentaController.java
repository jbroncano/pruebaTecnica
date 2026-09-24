package com.prueba.cuentas.controller;

import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.dto.CuentaRequestDTO;
import com.prueba.cuentas.dto.CuentaResponseDTO;
import com.prueba.cuentas.dto.CuentaUpdateDTO;
import com.prueba.cuentas.mapper.CuentaMapper;
import com.prueba.cuentas.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final CuentaMapper cuentaMapper;

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO request) {
        Cuenta cuenta = cuentaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaMapper.toResponseDTO(cuenta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaMapper.toResponseDTO(cuentaService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<Page<CuentaResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(cuentaService.listar(pageable).map(cuentaMapper::toResponseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CuentaUpdateDTO request) {
        return ResponseEntity.ok(cuentaMapper.toResponseDTO(cuentaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
