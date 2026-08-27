package com.example.trazabilidad.infrastructure.input.rest;

import com.example.trazabilidad.application.dto.TrazabilidadRequestDto;
import com.example.trazabilidad.application.dto.TrazabilidadResponseDto;
import com.example.trazabilidad.application.mapper.ITrazabilidadMapper;
import com.example.trazabilidad.domain.api.ITrazabilidadServicePort;
import com.example.trazabilidad.domain.model.EficienciaPedido;
import com.example.trazabilidad.domain.model.RankingEmpleado;
import com.example.trazabilidad.domain.model.Trazabilidad;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trazabilidad")
@RequiredArgsConstructor
public class TrazabilidadController {

    private final ITrazabilidadServicePort trazabilidadServicePort;
    private final ITrazabilidadMapper trazabilidadMapper;

    @PostMapping
    public ResponseEntity<Void> registrarCambioEstado(@Valid @RequestBody TrazabilidadRequestDto requestDto) {
        Trazabilidad trazabilidad = trazabilidadMapper.toModel(requestDto);
        trazabilidadServicePort.registrarCambioEstado(trazabilidad);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<TrazabilidadResponseDto>> obtenerHistorial(@PathVariable Long idPedido) {
        List<Trazabilidad> historial = trazabilidadServicePort.obtenerHistorialPorPedido(idPedido);
        return ResponseEntity.ok(trazabilidadMapper.toResponseDtoList(historial));
    }

    @GetMapping("/eficiencia/restaurante/{idRestaurante}")
    public ResponseEntity<List<EficienciaPedido>> obtenerEficiencia(@PathVariable Long idRestaurante) {
        return ResponseEntity.ok(trazabilidadServicePort.obtenerEficienciaPorRestaurante(idRestaurante));
    }

    @GetMapping("/eficiencia/restaurante/{idRestaurante}/ranking")
    public ResponseEntity<List<RankingEmpleado>> obtenerRanking(@PathVariable Long idRestaurante) {
        return ResponseEntity.ok(trazabilidadServicePort.obtenerRankingEmpleados(idRestaurante));
    }
}
