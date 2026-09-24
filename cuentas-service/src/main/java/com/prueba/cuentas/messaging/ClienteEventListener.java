package com.prueba.cuentas.messaging;

import com.prueba.cuentas.domain.ClienteRef;
import com.prueba.cuentas.repository.ClienteRefRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClienteEventListener {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventListener.class);

    private final ClienteRefRepository clienteRefRepository;

    @RabbitListener(queues = "${app.messaging.queue}")
    @Transactional
    public void onClienteEvent(ClienteEventDTO event) {
        log.info("Evento {} recibido para clienteId={}", event.getTipoEvento(), event.getClienteId());

        if ("ELIMINADO".equals(event.getTipoEvento())) {
            if (clienteRefRepository.existsById(event.getClienteId())) {
                clienteRefRepository.deleteById(event.getClienteId());
            }
            return;
        }

        ClienteRef clienteRef = ClienteRef.builder()
                .clienteId(event.getClienteId())
                .nombre(event.getNombre())
                .estado(event.getEstado())
                .build();
        clienteRefRepository.save(clienteRef);
    }
}
