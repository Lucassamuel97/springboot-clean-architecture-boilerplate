package com.starter.crudexample.application.item.retrieve.get;

import java.time.Instant;

import com.starter.crudexample.domain.item.Item;

public record ItemOutput(
                String id,
                String name,
                String description,
                double price,
                Instant createdAt,
                Instant updatedAt) {
        public static ItemOutput from(final Item item) {
                return new ItemOutput(
                                item.getId().getValue(),
                                item.getName(),
                                item.getDescription(),
                                item.getPrice(),
                                item.getCreatedAt(),
                                item.getUpdatedAt());
        }
}
