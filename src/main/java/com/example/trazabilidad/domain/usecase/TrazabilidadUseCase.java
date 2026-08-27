package com.example.trazabilidad.domain.usecase;

import com.example.trazabilidad.domain.api.ITrazabilidadServicePort;
import com.example.trazabilidad.domain.model.EficienciaPedido;
import com.example.trazabilidad.domain.model.RankingEmpleado;
import com.example.trazabilidad.domain.model.Trazabilidad;
import com.example.trazabilidad.domain.spi.ITrazabilidadPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<EficienciaPedido> obtenerEficienciaPorRestaurante(Long idRestaurante) {
        List<Trazabilidad> registros = trazabilidadPersistencePort.buscarPorIdRestaurante(idRestaurante);

        Map<Long, List<Trazabilidad>> porPedido = registros.stream()
                .collect(Collectors.groupingBy(Trazabilidad::getIdPedido));

        List<EficienciaPedido> resultado = new ArrayList<>();

        for (Map.Entry<Long, List<Trazabilidad>> entry : porPedido.entrySet()) {
            List<Trazabilidad> eventos = entry.getValue();

            Optional<Trazabilidad> inicio = eventos.stream()
                    .filter(t -> "PENDIENTE".equals(t.getEstadoNuevo()))
                    .findFirst();

            Optional<Trazabilidad> fin = eventos.stream()
                    .filter(t -> "ENTREGADO".equals(t.getEstadoNuevo()))
                    .findFirst();

            if (inicio.isPresent() && fin.isPresent()) {
                long minutos = Duration.between(inicio.get().getFecha(), fin.get().getFecha()).toMinutes();
                resultado.add(new EficienciaPedido(
                        entry.getKey(),
                        minutos,
                        fin.get().getIdEmpleado(),
                        fin.get().getCorreoEmpleado()
                ));
            }
        }

        return resultado;
    }

    @Override
    public List<RankingEmpleado> obtenerRankingEmpleados(Long idRestaurante) {
        List<EficienciaPedido> eficiencia = obtenerEficienciaPorRestaurante(idRestaurante);

        return eficiencia.stream()
                .collect(Collectors.groupingBy(EficienciaPedido::getIdEmpleado))
                .entrySet().stream()
                .map(entry -> {
                    List<EficienciaPedido> pedidos = entry.getValue();
                    double promedio = pedidos.stream()
                            .mapToLong(EficienciaPedido::getTiempoMinutos)
                            .average()
                            .orElse(0);
                    return new RankingEmpleado(
                            entry.getKey(),
                            pedidos.get(0).getCorreoEmpleado(),
                            promedio,
                            pedidos.size()
                    );
                })
                .sorted(Comparator.comparingDouble(RankingEmpleado::getTiempoPromedioMinutos))
                .toList();
    }
}
