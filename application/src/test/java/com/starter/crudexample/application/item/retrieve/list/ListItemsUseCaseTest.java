package com.starter.crudexample.application.item.retrieve.list;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.pagination.Pagination;
import com.starter.crudexample.domain.pagination.SearchQuery;

public class ListItemsUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListItemsUseCase useCase;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(itemGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListCastitems_shouldReturnAll() {
        // given
        final var items = List.of(
                Item.newItem("Smartphone", "A smartphone description", 100.0),
                Item.newItem("Laptop", "A laptop description", 1500.0));

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = items.stream()
                .map(ItemListOutput::from)
                .toList();

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                items);

        when(itemGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort,
                expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(itemGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListCastitemsAndResultIsEmpty_shouldReturn() {
        // given
        final var items = List.<Item>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = List.<ItemListOutput>of();

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                items);

        when(itemGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort,
                expectedDirection);

        // when
        final var actualOutput = useCase.execute(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());

        verify(itemGateway).findAll(eq(aQuery));
    }

     @Test
    public void givenAValidQuery_whenCallsListItemsAndGatewayThrowsRandomException_shouldException() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedErrorMessage = "Gateway error";

        when(itemGateway.findAll(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort,
                expectedDirection);

        // when
        final var actualException = Assertions.assertThrows(IllegalStateException.class, () -> {
            useCase.execute(aQuery);
        });

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(itemGateway).findAll(eq(aQuery));
    }
}
