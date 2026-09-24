package com.prueba.cuentas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Proyeccion local (read model) de Cliente, mantenida de forma eventualmente
 * consistente mediante los eventos publicados por personas-service via RabbitMQ.
 * Permite a cuentas-service validar clientes sin depender sincronicamente del
 * otro microservicio.
 */
@Entity
@Table(name = "cliente_ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRef {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "estado", nullable = false)
    private Boolean estado;
}
