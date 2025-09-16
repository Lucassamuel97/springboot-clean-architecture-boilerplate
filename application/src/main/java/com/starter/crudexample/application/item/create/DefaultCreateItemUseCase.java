package com.starter.crudexample.application.item.create;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.validation.handler.Notification;

import java.util.Objects;

public non-sealed class DefaultCreateItemUseCase extends CreateItemUseCase {

    private final ItemGateway itemGateway;

    public DefaultCreateItemUseCase(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Override
    public CreateItemOutput execute(final CreateItemCommand aCommand) {
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var aPrice = aCommand.price();

        final var notification = Notification.create();

        final var aItem = notification.validate(() -> Item.newItem(aName, aDescription, aPrice));

        if (notification.hasError()) {
            notify(notification);
        }

        return CreateItemOutput.from(this.itemGateway.create(aItem));
    }

    private void notify(Notification notification) {
        throw new NotificationException("Could not create Aggregate Item", notification);
    }
}