package com.starter.crudexample.application.item.retrieve.list;

import java.util.Objects;

import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;

public non-sealed class DefaultListItemsUseCase extends ListItemsUseCase {

    private final ItemGateway itemGateway;

    public DefaultListItemsUseCase(final ItemGateway itemGateway) {
        this.itemGateway = Objects.requireNonNull(itemGateway);
    }

    @Override
    public Pagination<ItemListOutput> execute(final SearchQuery aQuery) {
        return this.itemGateway.findAll(aQuery)
                .map(ItemListOutput::from);
    }
}
