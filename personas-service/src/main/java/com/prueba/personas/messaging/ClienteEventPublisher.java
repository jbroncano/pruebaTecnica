package com.prueba.personas.messaging;

import com.prueba.personas.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ClienteEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ClienteEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.exchange}")
    private String exchange;

    @Value("${app.messaging.routing-key-creado}")
    private String routingKeyCreado;

    @Value("${app.messaging.routing-key-actualizado}")
    private String routingKeyActualizado;

    @Value("${app.messaging.routing-key-eliminado}")
    private String routingKeyEliminado;

    /**
     * Se dispara solo despues del commit de la transaccion que origino el evento,
     * para no propagar cambios que terminen siendo revertidos.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClienteEvent(ClienteDomainEvent event) {
        String routingKey = switch (event.tipo()) {
            case CREADO -> routingKeyCreado;
            case ACTUALIZADO -> routingKeyActualizado;
            case ELIMINADO -> routingKeyEliminado;
        };
        Cliente cliente = event.cliente();
        ClienteEventDTO payload = ClienteEventDTO.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .estado(cliente.getEstado())
                .tipoEvento(event.tipo().name())
                .build();
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
            log.info("Evento {} publicado para clienteId={}", event.tipo(), cliente.getClienteId());
        } catch (Exception ex) {
            log.error("No se pudo publicar el evento {} para clienteId={}", event.tipo(), cliente.getClienteId(), ex);
        }
    }
}
