package com.starter.crudexample.application.item.delete;

import com.starter.crudexample.application.UnitUseCase;

public sealed abstract class DeleteItemUseCase
    extends UnitUseCase<String>
    permits DefaultDeleteItemUseCase {
}