package com.starter.crudexample.application.user.create;

import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserID;

public record CreateUserOutput(String id) {
    public static CreateUserOutput from(final UserID anId) {
        return new CreateUserOutput(anId.getValue());
    }

    public static CreateUserOutput from(final User aUser) {
        return from(aUser.getId());
    }
}
