package com.starter.crudexample.application.item.retrieve.list;

import java.time.Instant;

import com.starter.crudexample.domain.item.Item;

public record ItemListOutput(
        String id,
        String name,
        String description,
        double price,
        Instant createdAt
) {
    public static ItemListOutput from(final Item item) {
        return new ItemListOutput(
                item.getId().getValue(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCreatedAt());
    }
}

