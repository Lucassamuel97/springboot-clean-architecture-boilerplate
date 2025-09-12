package com.starter.crudexample.domain.item;

import java.util.List;
import java.util.Optional;

public interface ItemGateway {

    Item create(Item anItem);

    void deleteById(ItemID anId);

    Optional<Item> findById(ItemID anId);

    Item update(Item anItem);

    List<Item> findAll(String aQuery);

    List<ItemID> existsByIds(Iterable<ItemID> ids);
}
