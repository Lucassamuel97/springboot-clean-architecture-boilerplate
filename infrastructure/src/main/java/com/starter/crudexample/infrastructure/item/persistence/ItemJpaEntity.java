package com.starter.crudexample.infrastructure.item.persistence;

import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity(name = "Item")
@Table(name = "items")
public class ItemJpaEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    public ItemJpaEntity() {
    }

    private ItemJpaEntity(
            final String id,
            final String name,
            final String description,
            final Double price,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static ItemJpaEntity from(final Item aItem){
        return new ItemJpaEntity(
                aItem.getId().getValue(),
                aItem.getName(),
                aItem.getDescription(),
                aItem.getPrice(),
                aItem.getCreatedAt(),
                aItem.getUpdatedAt(),
                aItem.getDeletedAt()
        );
    }

    public Item toAggregate(){
        return Item.with(
                ItemID.from(getId()),
                getName(),
                getDescription(),
                getPrice(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt()
        );
    }
    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Instant getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }


}
