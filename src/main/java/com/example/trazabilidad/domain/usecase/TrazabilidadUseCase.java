package com.example.trazabilidad.domain.usecase;

import com.example.trazabilidad.domain.api.ITrazabilidadServicePort;
import com.example.trazabilidad.domain.model.Trazabilidad;
import com.example.trazabilidad.domain.spi.ITrazabilidadPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TrazabilidadUseCase implements ITrazabilidadServicePort {

    private final ITrazabilidadPersistencePort trazabilidadPersistencePort;

    @Override
    public void registrarCambioEstado(Trazabilidad trazabilidad) {
        trazabilidad.setFecha(LocalDateTime.now());
        trazabilidadPersistencePort.guardar(trazabilidad);
    }

    @Override
    public List<Trazabilidad> obtenerHistorialPorPedido(Long idPedido) {
        return trazabilidadPersistencePort.buscarPorIdPedido(idPedido);
    }
}
