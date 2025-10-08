package com.starter.crudexample.application.user.retrieve.list;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
// removed unused wildcard import

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;

public class ListUsersUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListUsersUseCase useCase;

    @Mock
    private UserGateway userGateway;

    @Override
    protected List<Object> getMocks() { return List.of(userGateway); }

    @Test
    public void givenAValidQuery_whenCallsListUsers_shouldReturnAll() {
        // given
        final var users = List.of(
            User.newUser("john","john@example.com","p1", List.of(Role.USER), true),
            User.newUser("ana","ana@example.com","p2", List.of(Role.ADMIN), true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "username";
        final var expectedDirection = "asc";
        final var expectedTotal = users.size();

        final var expectedItems = users.stream()
            .map(UserListOutput::from)
            .toList();

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedTotal,
            users
        );

        when(userGateway.findAll(any())).thenReturn(expectedPagination);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(userGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListUsersAndResultIsEmpty_shouldReturn() {
        // given
        final var users = List.<User>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "any";
        final var expectedSort = "username";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<UserListOutput>of();

        final var expectedPagination = new Pagination<>(
            expectedPage,
            expectedPerPage,
            expectedTotal,
            users
        );

        when(userGateway.findAll(any())).thenReturn(expectedPagination);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(userGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListUsersAndGatewayThrowsRandomException_shouldReturnException() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "john";
        final var expectedSort = "username";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";

        when(userGateway.findAll(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualException = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        verify(userGateway).findAll(eq(aQuery));
    }
}
