package com.prueba.personas.service;

import com.prueba.personas.domain.Cliente;
import com.prueba.personas.dto.ClienteRequestDTO;
import com.prueba.personas.dto.ClienteUpdateDTO;
import com.prueba.personas.exception.DuplicateResourceException;
import com.prueba.personas.exception.ResourceNotFoundException;
import com.prueba.personas.messaging.ClienteDomainEvent;
import com.prueba.personas.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Cliente crear(ClienteRequestDTO request) {
        if (clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente con identificacion " + request.getIdentificacion());
        }

        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .edad(request.getEdad())
                .identificacion(request.getIdentificacion())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .estado(request.getEstado())
                .build();

        Cliente guardado = clienteRepository.save(cliente);
        eventPublisher.publishEvent(new ClienteDomainEvent(guardado, ClienteDomainEvent.Tipo.CREADO));
        return guardado;
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));
    }

    @Transactional(readOnly = true)
    public Page<Cliente> listar(Boolean estado, Pageable pageable) {
        if (estado != null) {
            return clienteRepository.findByEstado(estado, pageable);
        }
        return clienteRepository.findAll(pageable);
    }

    @Transactional
    public Cliente actualizar(Long clienteId, ClienteUpdateDTO request) {
        Cliente cliente = obtenerPorId(clienteId);

        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEstado(request.getEstado());
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            cliente.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        Cliente actualizado = clienteRepository.save(cliente);
        eventPublisher.publishEvent(new ClienteDomainEvent(actualizado, ClienteDomainEvent.Tipo.ACTUALIZADO));
        return actualizado;
    }

    @Transactional
    public void eliminar(Long clienteId) {
        Cliente cliente = obtenerPorId(clienteId);
        clienteRepository.delete(cliente);
        eventPublisher.publishEvent(new ClienteDomainEvent(cliente, ClienteDomainEvent.Tipo.ELIMINADO));
    }
}
