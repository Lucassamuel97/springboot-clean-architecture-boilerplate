package com.starter.crudexample.domain.item;

import java.util.List;
import java.util.Optional;

import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;

public interface ItemGateway {

    Item create(Item anItem);

    void deleteById(ItemID anId);

    Optional<Item> findById(ItemID anId);

    Item update(Item anItem);

    Pagination<Item> findAll(SearchQuery aQuery);

    List<ItemID> existsByIds(Iterable<ItemID> ids);
}
