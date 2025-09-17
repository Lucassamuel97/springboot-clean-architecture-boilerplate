package com.starter.crudexample.application.item.retrieve.get;

import com.starter.crudexample.application.UseCase;

public sealed abstract class GetItemByIdUseCase
        extends UseCase<String, ItemOutput>
        permits DefaultGetItemByIdUseCase {
}