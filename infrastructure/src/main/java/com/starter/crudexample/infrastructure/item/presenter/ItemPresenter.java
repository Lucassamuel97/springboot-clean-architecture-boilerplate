package com.starter.crudexample.infrastructure.item.presenter;

import com.starter.crudexample.application.item.retrieve.get.ItemOutput;
import com.starter.crudexample.application.item.retrieve.list.ItemListOutput;
import com.starter.crudexample.infrastructure.item.models.ItemListResponse;
import com.starter.crudexample.infrastructure.item.models.ItemResponse;

public interface ItemPresenter {

    static ItemResponse present(final ItemOutput aitem) {
        return new ItemResponse(
            aitem.id(),
            aitem.name(),
            aitem.description(),
            aitem.price(),
            aitem.createdAt().toString(),
            aitem.updatedAt().toString()
        );
    }

    static ItemListResponse present(final ItemListOutput aItem) {
        return new ItemListResponse(
            aItem.id(),
            aItem.name(),
            aItem.description(),
            aItem.price(),
            aItem.createdAt().toString()
        );
    }
}
