package com.prueba.personas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.personas.domain.Cliente;
import com.prueba.personas.dto.ClienteRequestDTO;
import com.prueba.personas.dto.ClienteResponseDTO;
import com.prueba.personas.exception.ResourceNotFoundException;
import com.prueba.personas.mapper.ClienteMapper;
import com.prueba.personas.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de los endpoints de /clientes (indicacion general: minimo 2).
 */
@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private ClienteMapper clienteMapper;

    @Test
    void crear_deberiaRetornar201_cuandoElRequestEsValido() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Jose Lema")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .contrasena("1234")
                .estado(true)
                .build();

        Cliente cliente = Cliente.builder().id(1L).nombre("Jose Lema").estado(true).build();
        ClienteResponseDTO responseDTO = ClienteResponseDTO.builder()
                .clienteId(1L).nombre("Jose Lema").estado(true).build();

        when(clienteService.crear(any(ClienteRequestDTO.class))).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jose Lema"));
    }

    @Test
    void crear_deberiaRetornar400_cuandoFaltanCamposObligatorios() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder().build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_deberiaRetornar404_cuandoElClienteNoExiste() throws Exception {
        when(clienteService.obtenerPorId(anyLong()))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado: 99"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado: 99"));
    }
}
