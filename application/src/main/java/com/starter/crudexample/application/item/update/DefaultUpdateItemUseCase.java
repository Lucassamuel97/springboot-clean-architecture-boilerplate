package com.starter.crudexample.application.item.update;

import java.util.Objects;
import java.util.function.Supplier;

import com.starter.crudexample.domain.Identifier;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.validation.handler.Notification;

public non-sealed class DefaultUpdateItemUseCase extends UpdateItemUseCase {

    private final ItemGateway itemGateway;

    public DefaultUpdateItemUseCase(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Override
    public UpdateItemOutput execute(final UpdateItemCommand aCommand) {
        final var anId = ItemID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aPrice = aCommand.price();

        final var aItem = this.itemGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();

        notification.validate(() -> aItem.update(aName, aDescription, aPrice));

        if (notification.hasError()) {
            notify(anId, notification);
        }

        return UpdateItemOutput.from(this.itemGateway.update(aItem));
    }

    private void notify(final Identifier anId, final Notification notification) {
        throw new NotificationException("Could not update Aggregate Item %s".formatted(anId.getValue()), notification);
    }

    private Supplier<NotFoundException> notFound(final ItemID anId) {
        return () -> NotFoundException.with(Item.class, anId);
    }
}
