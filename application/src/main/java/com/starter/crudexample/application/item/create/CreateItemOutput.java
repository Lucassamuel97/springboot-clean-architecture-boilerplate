package com.starter.crudexample.application.item.create;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;

public record CreateItemOutput(
        String id
) {

    public static CreateItemOutput from(final ItemID anId) {
        return new CreateItemOutput(anId.getValue());
    }

    public static CreateItemOutput from(final Item aItem) {
        return from(aItem.getId());
    }
}