package com.prueba.cuentas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.domain.TipoCuenta;
import com.prueba.cuentas.dto.CuentaRequestDTO;
import com.prueba.cuentas.dto.CuentaResponseDTO;
import com.prueba.cuentas.exception.ClienteInvalidoException;
import com.prueba.cuentas.mapper.CuentaMapper;
import com.prueba.cuentas.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de los endpoints de /cuentas (indicacion general: minimo 2).
 */
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CuentaService cuentaService;

    @MockBean
    private CuentaMapper cuentaMapper;

    @Test
    void crear_deberiaRetornar201_cuandoElRequestEsValido() throws Exception {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("2000"))
                .estado(true)
                .clienteId(1L)
                .build();

        Cuenta cuenta = Cuenta.builder().id(1L).numeroCuenta("478758").build();
        CuentaResponseDTO responseDTO = CuentaResponseDTO.builder()
                .id(1L).numeroCuenta("478758").saldoDisponible(new BigDecimal("2000")).build();

        when(cuentaService.crear(any(CuentaRequestDTO.class))).thenReturn(cuenta);
        when(cuentaMapper.toResponseDTO(cuenta)).thenReturn(responseDTO);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("478758"));
    }

    @Test
    void crear_deberiaRetornar422_cuandoElClienteNoEstaActivo() throws Exception {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("2000"))
                .estado(true)
                .clienteId(1L)
                .build();

        when(cuentaService.crear(any(CuentaRequestDTO.class)))
                .thenThrow(new ClienteInvalidoException("El cliente 1 no esta activo"));

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("El cliente 1 no esta activo"));
    }

    @Test
    void obtener_deberiaRetornar400_cuandoFaltaElNumeroDeCuenta() throws Exception {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("100"))
                .estado(true)
                .clienteId(1L)
                .build();

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_deberiaRetornar200() throws Exception {
        when(cuentaService.listar(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk());
    }
}
