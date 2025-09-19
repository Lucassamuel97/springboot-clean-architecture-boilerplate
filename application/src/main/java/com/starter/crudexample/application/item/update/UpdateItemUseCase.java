package com.starter.crudexample.application.item.update;

import com.starter.crudexample.application.UseCase;

public sealed abstract class UpdateItemUseCase
        extends UseCase<UpdateItemCommand, UpdateItemOutput>
        permits DefaultUpdateItemUseCase {
}
