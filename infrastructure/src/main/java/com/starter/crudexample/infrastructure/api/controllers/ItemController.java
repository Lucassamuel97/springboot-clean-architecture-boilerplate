package com.starter.crudexample.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.starter.crudexample.application.item.create.CreateItemCommand;
import com.starter.crudexample.application.item.create.CreateItemUseCase;
import com.starter.crudexample.application.item.retrieve.get.GetItemByIdUseCase;
import com.starter.crudexample.infrastructure.api.ItemAPI;
import com.starter.crudexample.infrastructure.item.models.CreateItemRequest;
import com.starter.crudexample.infrastructure.item.models.ItemResponse;
import com.starter.crudexample.infrastructure.item.presenter.ItemPresenter;

@RestController
public class ItemController implements ItemAPI {
    
    private final CreateItemUseCase createItemUseCase;
    private final GetItemByIdUseCase getItemByIdUseCase;

    public ItemController(
        final CreateItemUseCase createItemUseCase, 
        final GetItemByIdUseCase getItemByIdUseCase) {
        this.createItemUseCase = Objects.requireNonNull(createItemUseCase);
        this.getItemByIdUseCase = Objects.requireNonNull(getItemByIdUseCase);
    }
    
    @Override
    public ResponseEntity<?> create(final CreateItemRequest input) {
        final var aCommand = CreateItemCommand.with(
            input.name(),
            input.description(),
            input.price()
        );
        
        final var output = this.createItemUseCase.execute(aCommand);
        
        return ResponseEntity.created(URI.create("/items/" + output.id())).body(output);
    }

    @Override
    public ItemResponse getById(final String id) {
        return ItemPresenter.present(this.getItemByIdUseCase.execute(id));
    }
}
