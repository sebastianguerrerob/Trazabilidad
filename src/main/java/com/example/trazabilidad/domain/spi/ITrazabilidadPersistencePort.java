package com.example.trazabilidad.domain.spi;

import com.example.trazabilidad.domain.model.Trazabilidad;

import java.util.List;

public interface ITrazabilidadPersistencePort {
    void guardar(Trazabilidad trazabilidad);
    List<Trazabilidad> buscarPorIdPedido(Long idPedido);
    List<Trazabilidad> buscarPorIdRestaurante(Long idRestaurante);
}
