package com.starter.crudexample.application.item.retrieve.get;

import java.util.Objects;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.exceptions.NotFoundException;

public non-sealed class DefaultGetItemByIdUseCase extends GetItemByIdUseCase {

    private final ItemGateway itemGateway;

    public DefaultGetItemByIdUseCase(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Override
    public ItemOutput execute(final String anIn) {
        final var aItemId = ItemID.from(anIn);
        return this.itemGateway.findById(aItemId)
                .map(ItemOutput::from)
                .orElseThrow(() -> NotFoundException.with(Item.class, aItemId));
    }
}
