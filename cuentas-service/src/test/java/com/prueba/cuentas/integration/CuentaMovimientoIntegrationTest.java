package com.prueba.cuentas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.cuentas.domain.ClienteRef;
import com.prueba.cuentas.domain.TipoCuenta;
import com.prueba.cuentas.domain.TipoMovimiento;
import com.prueba.cuentas.dto.CuentaRequestDTO;
import com.prueba.cuentas.dto.MovimientoRequestDTO;
import com.prueba.cuentas.repository.ClienteRefRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * F6: prueba de integracion end-to-end contra Postgres real (Testcontainers).
 * La mensajeria (sincronizacion de clientes via RabbitMQ) no es objeto de esta
 * prueba: el cliente de referencia se inserta directamente en la BD, simulando
 * que el evento ya fue consumido.
 */
@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration")
@AutoConfigureMockMvc
class CuentaMovimientoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cuentas_db")
            .withUsername("cuentas_user")
            .withPassword("cuentas_pass");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRefRepository clienteRefRepository;

    @BeforeEach
    void seedClienteActivo() {
        clienteRefRepository.save(ClienteRef.builder()
                .clienteId(1L)
                .nombre("Jose Lema")
                .estado(true)
                .build());
    }

    @Test
    void flujoCompleto_crearCuentaYRegistrarMovimientos() throws Exception {
        CuentaRequestDTO cuentaRequest = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("2000"))
                .estado(true)
                .clienteId(1L)
                .build();

        String cuentaResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cuentaId = objectMapper.readTree(cuentaResponse).get("id").asLong();

        MovimientoRequestDTO retiro = MovimientoRequestDTO.builder()
                .cuentaId(cuentaId)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retiro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(1425.00));
    }

    @Test
    void registrarMovimiento_deberiaRetornar422_cuandoSaldoNoDisponible() throws Exception {
        CuentaRequestDTO cuentaRequest = CuentaRequestDTO.builder()
                .numeroCuenta("495878")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.ZERO)
                .estado(true)
                .clienteId(1L)
                .build();

        String cuentaResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cuentaId = objectMapper.readTree(cuentaResponse).get("id").asLong();

        MovimientoRequestDTO retiro = MovimientoRequestDTO.builder()
                .cuentaId(cuentaId)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("50"))
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retiro)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
}
