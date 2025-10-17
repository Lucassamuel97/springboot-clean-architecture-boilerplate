package com.starter.crudexample.application.user.retrieve.list;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.domain.user.Role;
import com.starter.crudexample.domain.user.User;
import com.starter.crudexample.domain.user.UserGateway;
import com.starter.crudexample.infrastructure.user.persistence.UserJpaEntity;
import com.starter.crudexample.infrastructure.user.persistence.UserRepository;

@IntegrationTest
public class ListUsersUseCaseIT {
    @Autowired
    private ListUsersUseCase useCase;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private UserGateway userGateway;

    @Test
    public void givenAValidQuery_whenCallsListUsers_shouldReturnAll() {
        // Given
        final var aUsers = List.of(
                User.newUser("john", "john@example.com", "pass123", List.of(Role.USER), true),
                User.newUser("jane", "jane@example.com", "pass456", List.of(Role.ADMIN), true),
                User.newUser("bob", "bob@example.com", "pass789", List.of(Role.USER), true));

        this.userRepository.saveAllAndFlush(
                aUsers.stream()
                        .map(UserJpaEntity::from)
                        .toList());

        Assertions.assertEquals(3, this.userRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "username";
        final var expectedDirection = "asc";
        final var expectedTotal = 3;

        final var expectedUsers = aUsers.stream()
                .map(UserListOutput::from)
                .toList();

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        // When
        final var actualOutput = useCase.execute(aQuery);

        // Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedUsers.size(), actualOutput.items().size());
        
        for (final var expectedUser : expectedUsers) {
            Assertions.assertTrue(
                actualOutput.items().stream()
                    .anyMatch(actualUser -> 
                        actualUser.id().equals(expectedUser.id()) &&
                        actualUser.username().equals(expectedUser.username()) &&
                        actualUser.email().equals(expectedUser.email())
                    ),
                "Expected user not found: " + expectedUser.username()
            );
        }

        verify(userGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListUsersAndIsEmpty_shouldReturn() {
        // Given
        Assertions.assertEquals(0, this.userRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "username";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        // When
        final var actualOutput = useCase.execute(aQuery);

        // Then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertTrue(actualOutput.items().isEmpty());

        verify(userGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListUsersAndGatewayThrowsRandomException_shouldException() {
        // Given
        Assertions.assertEquals(0, this.userRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "username";
        final var expectedDirection = "asc";

        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(userGateway).findAll(any());

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        // when
        final var actualException = Assertions.assertThrows(IllegalStateException.class, () -> {
            useCase.execute(aQuery);
        });

        // Then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(userGateway).findAll(any());
    }
}
