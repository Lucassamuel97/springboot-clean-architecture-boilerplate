package com.starter.crudexample.application.item.update;

public record UpdateItemCommand(
        String id,
        String name,
        String description,
        double price
) {
    public static UpdateItemCommand with(
            final String id,
            final String name,
            final String description,
            final double price) {
        return new UpdateItemCommand(id, name, description, price);
    }
}
