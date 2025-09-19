package com.starter.crudexample.application.item.delete;

import java.util.Objects;

import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;

public non-sealed class DefaultDeleteItemUseCase extends DeleteItemUseCase {

    private final ItemGateway itemGateway;

    public DefaultDeleteItemUseCase(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Override
    public void execute(final String anIn) {
        this.itemGateway.deleteById(ItemID.from(anIn));
    }
}
