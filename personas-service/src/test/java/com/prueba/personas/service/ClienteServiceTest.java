package com.prueba.personas.service;

import com.prueba.personas.domain.Cliente;
import com.prueba.personas.messaging.ClienteDomainEvent;
import com.prueba.personas.dto.ClienteRequestDTO;
import com.prueba.personas.exception.DuplicateResourceException;
import com.prueba.personas.exception.ResourceNotFoundException;
import com.prueba.personas.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void crear_deberiaLanzarExcepcion_cuandoIdentificacionYaExiste() {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Jose Lema")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .contrasena("1234")
                .estado(true)
                .build();

        when(clienteRepository.existsByIdentificacion("0102030405")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("0102030405");
    }

    @Test
    void crear_deberiaGuardarClienteConContrasenaEncriptada() {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Jose Lema")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .contrasena("1234")
                .estado(true)
                .build();

        when(clienteRepository.existsByIdentificacion("0102030405")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("HASH");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = clienteService.crear(request);

        assertThat(resultado.getContrasena()).isEqualTo("HASH");
        assertThat(resultado.getIdentificacion()).isEqualTo("0102030405");
        verify(eventPublisher).publishEvent(any(ClienteDomainEvent.class));
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
