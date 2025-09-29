package com.starter.crudexample.infrastructure.item.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateItemRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") Double price
) {
}