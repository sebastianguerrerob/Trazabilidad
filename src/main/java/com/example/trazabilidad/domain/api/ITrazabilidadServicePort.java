package com.example.trazabilidad.domain.api;

import com.example.trazabilidad.domain.model.Trazabilidad;

import java.util.List;

public interface ITrazabilidadServicePort {
    void registrarCambioEstado(Trazabilidad trazabilidad);
    List<Trazabilidad> obtenerHistorialPorPedido(Long idPedido);
}
