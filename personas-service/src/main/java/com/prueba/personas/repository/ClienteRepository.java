package com.prueba.personas.repository;

import com.prueba.personas.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByIdentificacion(String identificacion);

    Optional<Cliente> findByIdentificacion(String identificacion);

    Page<Cliente> findByEstado(Boolean estado, Pageable pageable);
}
