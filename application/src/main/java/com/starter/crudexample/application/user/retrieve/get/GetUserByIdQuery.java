package com.starter.crudexample.application.user.retrieve.get;

public record GetUserByIdQuery(String id) {
    public static GetUserByIdQuery with(final String id) { return new GetUserByIdQuery(id); }
}
