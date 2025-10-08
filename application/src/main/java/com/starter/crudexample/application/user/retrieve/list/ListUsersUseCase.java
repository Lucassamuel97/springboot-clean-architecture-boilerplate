package com.starter.crudexample.application.user.retrieve.list;

import com.starter.crudexample.application.UseCase;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;

public sealed abstract class ListUsersUseCase
    extends UseCase<SearchQuery, Pagination<UserListOutput>>
    permits DefaultListUsersUseCase {
}
