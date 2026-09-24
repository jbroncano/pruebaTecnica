package com.prueba.personas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.personas.dto.ClienteRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * F6: prueba de integracion end-to-end contra una base de datos Postgres real (Testcontainers).
 * No se excluye RabbitAutoConfiguration: ClienteEventPublisher depende de un
 * RabbitTemplate real. En su lugar se reduce el timeout de conexion para que,
 * si no hay broker disponible, el intento de publicar el evento (capturado
 * internamente) falle rapido en vez de bloquear la prueba.
 */
@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "spring.rabbitmq.connection-timeout=1000")
@AutoConfigureMockMvc
class ClienteIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("personas_db")
            .withUsername("personas_user")
            .withPassword("personas_pass");

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

    @Test
    void flujoCompleto_crearYConsultarCliente() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Marianela Montalvo")
                .genero("F")
                .edad(28)
                .identificacion("0987654321")
                .direccion("Amazonas y NNUU")
                .telefono("097548965")
                .contrasena("5678")
                .estado(true)
                .build();

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Marianela Montalvo"))
                .andReturn().getResponse().getContentAsString();

        Long clienteId = objectMapper.readTree(response).get("clienteId").asLong();

        mockMvc.perform(get("/clientes/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificacion").value("0987654321"));
    }

    @Test
    void crear_deberiaRetornarConflicto_cuandoIdentificacionYaExiste() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Juan Osorio")
                .genero("M")
                .edad(40)
                .identificacion("0999999999")
                .direccion("13 junio y Equinoccial")
                .telefono("098874587")
                .contrasena("1245")
                .estado(true)
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
