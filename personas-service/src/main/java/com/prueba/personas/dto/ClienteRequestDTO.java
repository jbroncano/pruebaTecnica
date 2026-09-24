package com.prueba.personas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String nombre;

    @NotBlank(message = "El genero es obligatorio")
    @Size(max = 20)
    private String genero;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotBlank(message = "La identificacion es obligatoria")
    @Size(max = 20)
    private String identificacion;

    @Size(max = 200)
    private String direccion;

    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 4, message = "La contrasena debe tener al menos 4 caracteres")
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
