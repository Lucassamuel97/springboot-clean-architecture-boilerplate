package com.starter.crudexample.application.user.update;

import com.starter.crudexample.application.UseCase;

public sealed abstract class UpdateUserUseCase
    extends UseCase<UpdateUserCommand, UpdateUserOutput>
    permits DefaultUpdateUserUseCase {
}
