package com.example.trazabilidad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EficienciaPedido {
    private Long idPedido;
    private Long tiempoMinutos;
    private Long idEmpleado;
    private String correoEmpleado;
}
