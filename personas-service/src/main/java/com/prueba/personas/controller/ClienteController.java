package com.prueba.personas.controller;

import com.prueba.personas.domain.Cliente;
import com.prueba.personas.dto.ClienteRequestDTO;
import com.prueba.personas.dto.ClienteResponseDTO;
import com.prueba.personas.dto.ClienteUpdateDTO;
import com.prueba.personas.mapper.ClienteMapper;
import com.prueba.personas.service.ClienteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO request) {
        Cliente cliente = clienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toResponseDTO(cliente));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> obtener(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.obtenerPorId(clienteId);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(cliente));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @RequestParam(required = false) Boolean estado,
            Pageable pageable) {
        Page<ClienteResponseDTO> page = clienteService.listar(estado, pageable)
                .map(clienteMapper::toResponseDTO);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Long clienteId,
            @Valid @RequestBody ClienteUpdateDTO request) {
        Cliente cliente = clienteService.actualizar(clienteId, request);
        return ResponseEntity.ok(clienteMapper.toResponseDTO(cliente));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long clienteId) {
        clienteService.eliminar(clienteId);
        return ResponseEntity.noContent().build();
    }
}
