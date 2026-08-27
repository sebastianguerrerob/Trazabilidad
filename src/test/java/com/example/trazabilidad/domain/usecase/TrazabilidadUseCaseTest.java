package com.example.trazabilidad.domain.usecase;

import com.example.trazabilidad.domain.model.EficienciaPedido;
import com.example.trazabilidad.domain.model.RankingEmpleado;
import com.example.trazabilidad.domain.model.Trazabilidad;
import com.example.trazabilidad.domain.spi.ITrazabilidadPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrazabilidadUseCaseTest {

    @Mock
    private ITrazabilidadPersistencePort trazabilidadPersistencePort;

    @InjectMocks
    private TrazabilidadUseCase trazabilidadUseCase;

    private Trazabilidad trazabilidad;

    @BeforeEach
    void setUp() {
        trazabilidad = Trazabilidad.builder()
                .idPedido(1L)
                .idCliente(5L)
                .correoCliente("cliente@mail.com")
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("EN_PREPARACION")
                .idEmpleado(4L)
                .correoEmpleado("empleado@mail.com")
                .idRestaurante(10L)
                .build();
    }

    @Test
    void registrarCambioEstado_debeAsignarFechaYGuardar() {
        assertNull(trazabilidad.getFecha());

        trazabilidadUseCase.registrarCambioEstado(trazabilidad);

        assertNotNull(trazabilidad.getFecha());
        assertTrue(trazabilidad.getFecha().isBefore(LocalDateTime.now().plusSeconds(1)));
        verify(trazabilidadPersistencePort, times(1)).guardar(trazabilidad);
    }

    @Test
    void registrarCambioEstado_debeInvocarGuardarConElMismoObjeto() {
        trazabilidadUseCase.registrarCambioEstado(trazabilidad);

        verify(trazabilidadPersistencePort).guardar(trazabilidad);
        verifyNoMoreInteractions(trazabilidadPersistencePort);
    }

    @Test
    void obtenerHistorialPorPedido_debeRetornarListaDelPersistencePort() {
        Trazabilidad t1 = Trazabilidad.builder()
                .id("uuid-1")
                .idPedido(1L)
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("EN_PREPARACION")
                .fecha(LocalDateTime.now().minusHours(2))
                .build();

        Trazabilidad t2 = Trazabilidad.builder()
                .id("uuid-2")
                .idPedido(1L)
                .estadoAnterior("EN_PREPARACION")
                .estadoNuevo("LISTO")
                .fecha(LocalDateTime.now().minusHours(1))
                .build();

        when(trazabilidadPersistencePort.buscarPorIdPedido(1L)).thenReturn(List.of(t1, t2));

        List<Trazabilidad> resultado = trazabilidadUseCase.obtenerHistorialPorPedido(1L);

        assertEquals(2, resultado.size());
        assertEquals("uuid-1", resultado.get(0).getId());
        assertEquals("uuid-2", resultado.get(1).getId());
        verify(trazabilidadPersistencePort, times(1)).buscarPorIdPedido(1L);
    }

    @Test
    void obtenerHistorialPorPedido_debeRetornarListaVaciaSiNoHayRegistros() {
        when(trazabilidadPersistencePort.buscarPorIdPedido(99L)).thenReturn(List.of());

        List<Trazabilidad> resultado = trazabilidadUseCase.obtenerHistorialPorPedido(99L);

        assertTrue(resultado.isEmpty());
        verify(trazabilidadPersistencePort).buscarPorIdPedido(99L);
    }

    @Test
    void obtenerEficienciaPorRestaurante_debeCalcularTiempoEntrePendienteYEntregado() {
        LocalDateTime inicio = LocalDateTime.of(2025, 6, 1, 10, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2025, 6, 1, 10, 45, 0);

        Trazabilidad pendiente = Trazabilidad.builder()
                .idPedido(3L)
                .estadoNuevo("PENDIENTE")
                .fecha(inicio)
                .idEmpleado(4L)
                .correoEmpleado("pedro@mail.com")
                .idRestaurante(10L)
                .build();

        Trazabilidad entregado = Trazabilidad.builder()
                .idPedido(3L)
                .estadoNuevo("ENTREGADO")
                .fecha(fin)
                .idEmpleado(4L)
                .correoEmpleado("pedro@mail.com")
                .idRestaurante(10L)
                .build();

        when(trazabilidadPersistencePort.buscarPorIdRestaurante(10L))
                .thenReturn(List.of(pendiente, entregado));

        List<EficienciaPedido> resultado = trazabilidadUseCase.obtenerEficienciaPorRestaurante(10L);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getIdPedido());
        assertEquals(45L, resultado.get(0).getTiempoMinutos());
        assertEquals(4L, resultado.get(0).getIdEmpleado());
        assertEquals("pedro@mail.com", resultado.get(0).getCorreoEmpleado());
    }

    @Test
    void obtenerEficienciaPorRestaurante_debeExcluirPedidosSinEntrega() {
        Trazabilidad pendiente = Trazabilidad.builder()
                .idPedido(5L)
                .estadoNuevo("PENDIENTE")
                .fecha(LocalDateTime.now().minusHours(1))
                .idRestaurante(10L)
                .build();

        Trazabilidad enPreparacion = Trazabilidad.builder()
                .idPedido(5L)
                .estadoNuevo("EN_PREPARACION")
                .fecha(LocalDateTime.now().minusMinutes(30))
                .idRestaurante(10L)
                .build();

        when(trazabilidadPersistencePort.buscarPorIdRestaurante(10L))
                .thenReturn(List.of(pendiente, enPreparacion));

        List<EficienciaPedido> resultado = trazabilidadUseCase.obtenerEficienciaPorRestaurante(10L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerRankingEmpleados_debeOrdenarPorTiempoPromedioAscendente() {
        LocalDateTime base = LocalDateTime.of(2025, 6, 1, 10, 0, 0);

        // Empleado 4: pedido de 30 min y pedido de 45 min -> promedio 37.5
        Trazabilidad p1Inicio = Trazabilidad.builder().idPedido(1L).estadoNuevo("PENDIENTE")
                .fecha(base).idEmpleado(4L).correoEmpleado("pedro@mail.com").idRestaurante(10L).build();
        Trazabilidad p1Fin = Trazabilidad.builder().idPedido(1L).estadoNuevo("ENTREGADO")
                .fecha(base.plusMinutes(30)).idEmpleado(4L).correoEmpleado("pedro@mail.com").idRestaurante(10L).build();
        Trazabilidad p2Inicio = Trazabilidad.builder().idPedido(2L).estadoNuevo("PENDIENTE")
                .fecha(base).idEmpleado(4L).correoEmpleado("pedro@mail.com").idRestaurante(10L).build();
        Trazabilidad p2Fin = Trazabilidad.builder().idPedido(2L).estadoNuevo("ENTREGADO")
                .fecha(base.plusMinutes(45)).idEmpleado(4L).correoEmpleado("pedro@mail.com").idRestaurante(10L).build();

        // Empleado 6: pedido de 50 min -> promedio 50
        Trazabilidad p3Inicio = Trazabilidad.builder().idPedido(3L).estadoNuevo("PENDIENTE")
                .fecha(base).idEmpleado(6L).correoEmpleado("maria@mail.com").idRestaurante(10L).build();
        Trazabilidad p3Fin = Trazabilidad.builder().idPedido(3L).estadoNuevo("ENTREGADO")
                .fecha(base.plusMinutes(50)).idEmpleado(6L).correoEmpleado("maria@mail.com").idRestaurante(10L).build();

        when(trazabilidadPersistencePort.buscarPorIdRestaurante(10L))
                .thenReturn(List.of(p1Inicio, p1Fin, p2Inicio, p2Fin, p3Inicio, p3Fin));

        List<RankingEmpleado> ranking = trazabilidadUseCase.obtenerRankingEmpleados(10L);

        assertEquals(2, ranking.size());
        // Pedro primero (promedio 37.5)
        assertEquals(4L, ranking.get(0).getIdEmpleado());
        assertEquals(37.5, ranking.get(0).getTiempoPromedioMinutos());
        assertEquals(2, ranking.get(0).getPedidosCompletados());
        // Maria segundo (promedio 50)
        assertEquals(6L, ranking.get(1).getIdEmpleado());
        assertEquals(50.0, ranking.get(1).getTiempoPromedioMinutos());
        assertEquals(1, ranking.get(1).getPedidosCompletados());
    }

    @Test
    void obtenerRankingEmpleados_debeRetornarListaVaciaSinPedidosCompletados() {
        when(trazabilidadPersistencePort.buscarPorIdRestaurante(99L)).thenReturn(List.of());

        List<RankingEmpleado> ranking = trazabilidadUseCase.obtenerRankingEmpleados(99L);

        assertTrue(ranking.isEmpty());
    }
}
