package com.starter.crudexample.application.item.create;

public record CreateItemCommand(
        String name,
        String description,
        Double price) {

    public static CreateItemCommand with(final String aName, final String aDescription, final Double aPrice) {
        return new CreateItemCommand(aName, aDescription, aPrice);
    }
}
