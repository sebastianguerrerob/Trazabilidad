package com.example.trazabilidad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingEmpleado {
    private Long idEmpleado;
    private String correoEmpleado;
    private Double tiempoPromedioMinutos;
    private Integer pedidosCompletados;
}
