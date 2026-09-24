package com.prueba.personas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Cliente hereda de Persona (estrategia JOINED): el id de Persona es tambien
 * la clave primaria de Cliente, expuesta como "clienteId".
 */
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "cliente_id")
@Getter
@Setter
@ToString(callSuper = true, exclude = "contrasena")
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Persona {

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    public Long getClienteId() {
        return getId();
    }

    public boolean estaActivo() {
        return Boolean.TRUE.equals(estado);
    }
}
