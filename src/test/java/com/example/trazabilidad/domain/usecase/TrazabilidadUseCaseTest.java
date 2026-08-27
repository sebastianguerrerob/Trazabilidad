package com.example.trazabilidad.domain.usecase;

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
}
