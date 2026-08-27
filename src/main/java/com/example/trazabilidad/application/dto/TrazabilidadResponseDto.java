package com.example.trazabilidad.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrazabilidadResponseDto {
    private String id;
    private Long idPedido;
    private Long idCliente;
    private String correoCliente;
    private LocalDateTime fecha;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long idEmpleado;
    private String correoEmpleado;
}
