package com.starter.crudexample.application.user.create;

import com.starter.crudexample.application.UseCase;

public sealed abstract class CreateUserUseCase
    extends UseCase<CreateUserCommand, CreateUserOutput>
    permits DefaultCreateUserUseCase {
}
