package com.example.trazabilidad.infrastructure.input.rest;

import com.example.trazabilidad.application.dto.TrazabilidadRequestDto;
import com.example.trazabilidad.application.dto.TrazabilidadResponseDto;
import com.example.trazabilidad.application.mapper.ITrazabilidadMapper;
import com.example.trazabilidad.domain.api.ITrazabilidadServicePort;
import com.example.trazabilidad.domain.model.EficienciaPedido;
import com.example.trazabilidad.domain.model.RankingEmpleado;
import com.example.trazabilidad.domain.model.Trazabilidad;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrazabilidadController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrazabilidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ITrazabilidadServicePort trazabilidadServicePort;

    @MockBean
    private ITrazabilidadMapper trazabilidadMapper;

    @Test
    void registrarCambioEstado_debeRetornar201() throws Exception {
        TrazabilidadRequestDto requestDto = new TrazabilidadRequestDto();
        requestDto.setIdPedido(1L);
        requestDto.setIdCliente(5L);
        requestDto.setCorreoCliente("cliente@mail.com");
        requestDto.setEstadoAnterior("PENDIENTE");
        requestDto.setEstadoNuevo("EN_PREPARACION");
        requestDto.setIdEmpleado(4L);
        requestDto.setCorreoEmpleado("empleado@mail.com");
        requestDto.setIdRestaurante(10L);

        Trazabilidad modelo = Trazabilidad.builder().idPedido(1L).build();
        when(trazabilidadMapper.toModel(any(TrazabilidadRequestDto.class))).thenReturn(modelo);

        mockMvc.perform(post("/trazabilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(trazabilidadMapper).toModel(any(TrazabilidadRequestDto.class));
        verify(trazabilidadServicePort).registrarCambioEstado(modelo);
    }

    @Test
    void registrarCambioEstado_debeRetornar400SiFaltanCampos() throws Exception {
        TrazabilidadRequestDto requestDto = new TrazabilidadRequestDto();

        mockMvc.perform(post("/trazabilidad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trazabilidadServicePort);
    }

    @Test
    void obtenerHistorial_debeRetornar200ConLista() throws Exception {
        LocalDateTime fecha = LocalDateTime.of(2025, 1, 15, 10, 30, 0);

        Trazabilidad t1 = Trazabilidad.builder()
                .id("uuid-1")
                .idPedido(3L)
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("EN_PREPARACION")
                .fecha(fecha)
                .build();

        TrazabilidadResponseDto responseDto = new TrazabilidadResponseDto(
                "uuid-1", 3L, 5L, "cliente@mail.com", fecha,
                "PENDIENTE", "EN_PREPARACION", 4L, "empleado@mail.com", 10L);

        when(trazabilidadServicePort.obtenerHistorialPorPedido(3L)).thenReturn(List.of(t1));
        when(trazabilidadMapper.toResponseDtoList(any())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/trazabilidad/pedido/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].idPedido").value(3))
                .andExpect(jsonPath("$[0].estadoAnterior").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].estadoNuevo").value("EN_PREPARACION"));

        verify(trazabilidadServicePort).obtenerHistorialPorPedido(3L);
        verify(trazabilidadMapper).toResponseDtoList(any());
    }

    @Test
    void obtenerHistorial_debeRetornar200ConListaVacia() throws Exception {
        when(trazabilidadServicePort.obtenerHistorialPorPedido(99L)).thenReturn(List.of());
        when(trazabilidadMapper.toResponseDtoList(any())).thenReturn(List.of());

        mockMvc.perform(get("/trazabilidad/pedido/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void obtenerEficiencia_debeRetornar200ConLista() throws Exception {
        EficienciaPedido eficiencia = new EficienciaPedido(3L, 45L, 4L, "pedro@mail.com");

        when(trazabilidadServicePort.obtenerEficienciaPorRestaurante(10L))
                .thenReturn(List.of(eficiencia));

        mockMvc.perform(get("/trazabilidad/eficiencia/restaurante/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPedido").value(3))
                .andExpect(jsonPath("$[0].tiempoMinutos").value(45))
                .andExpect(jsonPath("$[0].idEmpleado").value(4))
                .andExpect(jsonPath("$[0].correoEmpleado").value("pedro@mail.com"));

        verify(trazabilidadServicePort).obtenerEficienciaPorRestaurante(10L);
    }

    @Test
    void obtenerRanking_debeRetornar200ConListaOrdenada() throws Exception {
        RankingEmpleado r1 = new RankingEmpleado(4L, "pedro@mail.com", 37.5, 2);
        RankingEmpleado r2 = new RankingEmpleado(6L, "maria@mail.com", 50.0, 1);

        when(trazabilidadServicePort.obtenerRankingEmpleados(10L))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/trazabilidad/eficiencia/restaurante/10/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEmpleado").value(4))
                .andExpect(jsonPath("$[0].tiempoPromedioMinutos").value(37.5))
                .andExpect(jsonPath("$[0].pedidosCompletados").value(2))
                .andExpect(jsonPath("$[1].idEmpleado").value(6))
                .andExpect(jsonPath("$[1].tiempoPromedioMinutos").value(50.0))
                .andExpect(jsonPath("$[1].pedidosCompletados").value(1));

        verify(trazabilidadServicePort).obtenerRankingEmpleados(10L);
    }
}
