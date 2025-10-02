package com.starter.crudexample.infrastructure.item.models;

public record UpdateItemRequest(
    String name,
    String description,
    Double price
) {

}
