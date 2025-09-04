package com.starter.crudexample.domain.item;

import java.time.Instant;

import com.starter.crudexample.domain.AggregateRoot;
import com.starter.crudexample.domain.utils.InstantUtils;
import com.starter.crudexample.domain.validation.ValidationHandler;

public class Item extends AggregateRoot<ItemID>{

    private String name;
    private String description;
    private Double price;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    
    protected Item(
        final ItemID anId,
        final String aName,
        final String aDescription,
        final Double aPrice,
        final Instant aCreatedAt,
        final Instant aUpdatedAt,
        final Instant aDeletedAt
    ) {
        super(anId);
        this.name = aName;
        this.description = aDescription;
        this.price = aPrice;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
        this.deletedAt = aDeletedAt;
    }

    public static Item newItem(final String aName, final String aDescription, final Double aPrice) {
        final var anId = ItemID.unique();
        final var now = InstantUtils.now();
        return new Item(anId, aName, aDescription, aPrice, now, now, null);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // TODO Auto-generated method stub
    }

    public ItemID getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
