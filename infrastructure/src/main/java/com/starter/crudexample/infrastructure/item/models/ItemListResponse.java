package com.starter.crudexample.infrastructure.item.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("price") Double price,
        @JsonProperty("created_at") String createdAt) {
}
