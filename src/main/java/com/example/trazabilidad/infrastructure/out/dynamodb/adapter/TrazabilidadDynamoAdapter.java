package com.example.trazabilidad.infrastructure.out.dynamodb.adapter;

import com.example.trazabilidad.domain.model.Trazabilidad;
import com.example.trazabilidad.domain.spi.ITrazabilidadPersistencePort;
import com.example.trazabilidad.infrastructure.out.dynamodb.entity.TrazabilidadEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrazabilidadDynamoAdapter implements ITrazabilidadPersistencePort {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    private DynamoDbTable<TrazabilidadEntity> table;

    @PostConstruct
    public void init() {
        table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(TrazabilidadEntity.class));
    }

    @Override
    public void guardar(Trazabilidad trazabilidad) {
        TrazabilidadEntity entity = new TrazabilidadEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setIdPedido(trazabilidad.getIdPedido());
        entity.setIdCliente(trazabilidad.getIdCliente());
        entity.setCorreoCliente(trazabilidad.getCorreoCliente());
        entity.setFecha(trazabilidad.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        entity.setEstadoAnterior(trazabilidad.getEstadoAnterior());
        entity.setEstadoNuevo(trazabilidad.getEstadoNuevo());
        entity.setIdEmpleado(trazabilidad.getIdEmpleado());
        entity.setCorreoEmpleado(trazabilidad.getCorreoEmpleado());
        entity.setIdRestaurante(trazabilidad.getIdRestaurante());

        table.putItem(entity);
    }

    @Override
    public List<Trazabilidad> buscarPorIdPedido(Long idPedido) {
        DynamoDbIndex<TrazabilidadEntity> index = table.index("pedido-fecha-index");

        QueryConditional queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(idPedido));

        return index.query(queryConditional).stream()
                .flatMap(page -> page.items().stream())
                .map(this::toModel)
                .toList();
    }

    @Override
    public List<Trazabilidad> buscarPorIdRestaurante(Long idRestaurante) {
        DynamoDbIndex<TrazabilidadEntity> index = table.index("restaurante-index");

        QueryConditional queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(idRestaurante));

        return index.query(queryConditional).stream()
                .flatMap(page -> page.items().stream())
                .map(this::toModel)
                .toList();
    }

    private Trazabilidad toModel(TrazabilidadEntity entity) {
        return Trazabilidad.builder()
                .id(entity.getId())
                .idPedido(entity.getIdPedido())
                .idCliente(entity.getIdCliente())
                .correoCliente(entity.getCorreoCliente())
                .fecha(LocalDateTime.parse(entity.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .estadoAnterior(entity.getEstadoAnterior())
                .estadoNuevo(entity.getEstadoNuevo())
                .idEmpleado(entity.getIdEmpleado())
                .correoEmpleado(entity.getCorreoEmpleado())
                .idRestaurante(entity.getIdRestaurante())
                .build();
    }
}
