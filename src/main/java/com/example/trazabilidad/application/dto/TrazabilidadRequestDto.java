package com.example.trazabilidad.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrazabilidadRequestDto {

    @NotNull(message = "El id del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El id del cliente es obligatorio")
    private Long idCliente;

    @NotBlank(message = "El correo del cliente es obligatorio")
    private String correoCliente;

    @NotBlank(message = "El estado anterior es obligatorio")
    private String estadoAnterior;

    @NotBlank(message = "El estado nuevo es obligatorio")
    private String estadoNuevo;

    private Long idEmpleado;
    private String correoEmpleado;
}
