package com.starter.crudexample.domain.user;

import java.util.List;
import java.util.Optional;

import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;

public interface UserGateway {

    User create(User aUser);

    void deleteById(UserID anId);

    Optional<User> findById(UserID anId);

    User update(User aUser);

    Pagination<User> findAll(SearchQuery aQuery);

    List<UserID> existsByIds(Iterable<UserID> ids);
}
