package com.example.trazabilidad.domain.api;

import com.example.trazabilidad.domain.model.EficienciaPedido;
import com.example.trazabilidad.domain.model.RankingEmpleado;
import com.example.trazabilidad.domain.model.Trazabilidad;

import java.util.List;

public interface ITrazabilidadServicePort {
    void registrarCambioEstado(Trazabilidad trazabilidad);
    List<Trazabilidad> obtenerHistorialPorPedido(Long idPedido);
    List<EficienciaPedido> obtenerEficienciaPorRestaurante(Long idRestaurante);
    List<RankingEmpleado> obtenerRankingEmpleados(Long idRestaurante);
}
