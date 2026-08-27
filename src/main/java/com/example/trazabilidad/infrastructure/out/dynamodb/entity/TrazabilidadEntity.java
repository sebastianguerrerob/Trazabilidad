package com.example.trazabilidad.infrastructure.out.dynamodb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrazabilidadEntity {

    private String id;
    private Long idPedido;
    private Long idCliente;
    private String correoCliente;
    private String fecha;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long idEmpleado;
    private String correoEmpleado;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "pedido-fecha-index")
    public Long getIdPedido() {
        return idPedido;
    }

    @DynamoDbSecondarySortKey(indexNames = "pedido-fecha-index")
    public String getFecha() {
        return fecha;
    }
}
