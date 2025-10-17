package com.starter.crudexample.infrastructure.user.presenter;

import com.starter.crudexample.application.user.retrieve.get.GetUserByIdOutput;
import com.starter.crudexample.application.user.retrieve.list.UserListOutput;
import com.starter.crudexample.infrastructure.user.models.UserListResponse;
import com.starter.crudexample.infrastructure.user.models.UserResponse;

public interface UserPresenter {

    static UserResponse present(final GetUserByIdOutput aUser) {
        return UserResponse.from(aUser);
    }

    static UserListResponse present(final UserListOutput aUser) {
        return new UserListResponse(
            aUser.id(),
            aUser.username(),
            aUser.email(),
            aUser.roles(),
            aUser.active(),
            aUser.createdAt().toString()
        );
    }
}
