package com.starter.crudexample.application.item.retrieve.list;

import com.starter.crudexample.application.UseCase;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;


public sealed abstract class ListItemsUseCase
            extends UseCase<SearchQuery, Pagination<ItemListOutput>>
            permits DefaultListItemsUseCase {
            }
