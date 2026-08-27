package com.example.trazabilidad.infrastructure.configuration;

import com.example.trazabilidad.domain.api.ITrazabilidadServicePort;
import com.example.trazabilidad.domain.spi.ITrazabilidadPersistencePort;
import com.example.trazabilidad.domain.usecase.TrazabilidadUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ITrazabilidadServicePort trazabilidadServicePort(ITrazabilidadPersistencePort persistencePort) {
        return new TrazabilidadUseCase(persistencePort);
    }
}
