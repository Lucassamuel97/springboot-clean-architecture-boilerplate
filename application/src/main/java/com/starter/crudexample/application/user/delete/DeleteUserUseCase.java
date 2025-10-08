package com.starter.crudexample.application.user.delete;

import com.starter.crudexample.application.UnitUseCase;

public sealed abstract class DeleteUserUseCase
    extends UnitUseCase<String>
    permits DefaultDeleteUserUseCase {
}
