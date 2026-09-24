package com.prueba.personas.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * F5: prueba unitaria de la entidad de dominio Cliente.
 */
class ClienteTest {

    @Test
    void estaActivo_deberiaSerTrue_cuandoEstadoEsTrue() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Jose Lema")
                .estado(true)
                .build();

        assertThat(cliente.estaActivo()).isTrue();
    }

    @Test
    void estaActivo_deberiaSerFalse_cuandoEstadoEsFalseONulo() {
        Cliente inactivo = Cliente.builder().id(2L).estado(false).build();
        Cliente sinEstado = Cliente.builder().id(3L).build();

        assertThat(inactivo.estaActivo()).isFalse();
        assertThat(sinEstado.estaActivo()).isFalse();
    }

    @Test
    void getClienteId_deberiaRetornarElIdHeredadoDePersona() {
        Cliente cliente = Cliente.builder().id(42L).build();

        assertThat(cliente.getClienteId()).isEqualTo(42L);
    }
}
