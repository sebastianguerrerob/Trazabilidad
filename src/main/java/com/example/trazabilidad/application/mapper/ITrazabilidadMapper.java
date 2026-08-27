package com.example.trazabilidad.application.mapper;

import com.example.trazabilidad.application.dto.TrazabilidadRequestDto;
import com.example.trazabilidad.application.dto.TrazabilidadResponseDto;
import com.example.trazabilidad.domain.model.Trazabilidad;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ITrazabilidadMapper {

    Trazabilidad toModel(TrazabilidadRequestDto dto);

    TrazabilidadResponseDto toResponseDto(Trazabilidad trazabilidad);

    List<TrazabilidadResponseDto> toResponseDtoList(List<Trazabilidad> trazabilidades);
}
