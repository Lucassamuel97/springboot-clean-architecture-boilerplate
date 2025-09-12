package com.starter.crudexample.application.item.create;

import com.starter.crudexample.application.UseCase;

public sealed abstract class CreateItemUseCase
        extends UseCase<CreateItemCommand, CreateItemOutput>
        permits DefaultCreateItemUseCase {
}
