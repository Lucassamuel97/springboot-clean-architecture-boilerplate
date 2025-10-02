package com.starter.crudexample.application.item.update;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;

public record UpdateItemOutput(String id) {

    public static UpdateItemOutput from(final ItemID anId) {
        return new UpdateItemOutput(anId.getValue());
    }

    public static UpdateItemOutput from(final Item aItem) {
        return from(aItem.getId());
    }
}