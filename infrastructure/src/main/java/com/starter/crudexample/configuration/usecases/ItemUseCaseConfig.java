package com.starter.crudexample.configuration.usecases;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.starter.crudexample.application.item.create.CreateItemUseCase;
import com.starter.crudexample.application.item.create.DefaultCreateItemUseCase;
import com.starter.crudexample.application.item.delete.DefaultDeleteItemUseCase;
import com.starter.crudexample.application.item.delete.DeleteItemUseCase;
import com.starter.crudexample.application.item.retrieve.get.DefaultGetItemByIdUseCase;
import com.starter.crudexample.application.item.retrieve.get.GetItemByIdUseCase;
import com.starter.crudexample.application.item.retrieve.list.DefaultListItemsUseCase;
import com.starter.crudexample.application.item.retrieve.list.ListItemsUseCase;
import com.starter.crudexample.application.item.update.DefaultUpdateItemUseCase;
import com.starter.crudexample.application.item.update.UpdateItemUseCase;
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

    @Bean
    public GetItemByIdUseCase getItemByIdUseCase() {
        return new DefaultGetItemByIdUseCase(itemGateway);
    }

    @Bean
    public DeleteItemUseCase deleteItemUseCase() {
        return new DefaultDeleteItemUseCase(itemGateway);
    }

    @Bean
    public UpdateItemUseCase updateItemUseCase() {
        return new DefaultUpdateItemUseCase(itemGateway);
    }

    @Bean
    public ListItemsUseCase listItemUseCase() {
        return new DefaultListItemsUseCase(itemGateway);
    }
}
