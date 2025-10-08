package com.starter.crudexample.application.user.update;

import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserID;

public record UpdateUserOutput(String id) {
    public static UpdateUserOutput from(final UserID anId) { return new UpdateUserOutput(anId.getValue()); }
    public static UpdateUserOutput from(final User aUser) { return from(aUser.getId()); }
}
