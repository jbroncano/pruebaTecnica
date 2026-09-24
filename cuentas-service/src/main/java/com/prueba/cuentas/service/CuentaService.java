package com.prueba.cuentas.service;

import com.prueba.cuentas.domain.ClienteRef;
import com.prueba.cuentas.domain.Cuenta;
import com.prueba.cuentas.dto.CuentaRequestDTO;
import com.prueba.cuentas.dto.CuentaUpdateDTO;
import com.prueba.cuentas.exception.ClienteInvalidoException;
import com.prueba.cuentas.exception.DuplicateResourceException;
import com.prueba.cuentas.exception.ResourceNotFoundException;
import com.prueba.cuentas.repository.ClienteRefRepository;
import com.prueba.cuentas.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRefRepository clienteRefRepository;

    @Transactional
    public Cuenta crear(CuentaRequestDTO request) {
        if (cuentaRepository.existsByNumeroCuenta(request.getNumeroCuenta())) {
            throw new DuplicateResourceException(
                    "Ya existe una cuenta con numero " + request.getNumeroCuenta());
        }
        validarClienteActivo(request.getClienteId());

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(request.getNumeroCuenta())
                .tipoCuenta(request.getTipoCuenta())
                .saldoInicial(request.getSaldoInicial())
                .saldoDisponible(request.getSaldoInicial())
                .estado(request.getEstado())
                .clienteId(request.getClienteId())
                .build();

        return cuentaRepository.save(cuenta);
    }

    @Transactional(readOnly = true)
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Cuenta> listar(Pageable pageable) {
        return cuentaRepository.findAll(pageable);
    }

    @Transactional
    public Cuenta actualizar(Long id, CuentaUpdateDTO request) {
        Cuenta cuenta = obtenerPorId(id);
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setEstado(request.getEstado());
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        cuentaRepository.delete(cuenta);
    }

    private void validarClienteActivo(Long clienteId) {
        ClienteRef clienteRef = clienteRefRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteInvalidoException(
                        "El cliente " + clienteId + " no existe o aun no ha sido sincronizado"));
        if (!Boolean.TRUE.equals(clienteRef.getEstado())) {
            throw new ClienteInvalidoException("El cliente " + clienteId + " no esta activo");
        }
    }
}
