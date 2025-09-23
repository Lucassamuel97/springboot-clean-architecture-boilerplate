package com.starter.crudexample.configuration.usecases;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.starter.crudexample.application.item.create.CreateItemUseCase;
import com.starter.crudexample.application.item.create.DefaultCreateItemUseCase;
import com.starter.crudexample.domain.item.ItemGateway;

@Configuration
public class ItemUseCaseConfig {

    private final ItemGateway itemGateway;

    public ItemUseCaseConfig(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Bean
    public CreateItemUseCase createItemUseCase() {
        return new DefaultCreateItemUseCase(itemGateway);
    }
}
