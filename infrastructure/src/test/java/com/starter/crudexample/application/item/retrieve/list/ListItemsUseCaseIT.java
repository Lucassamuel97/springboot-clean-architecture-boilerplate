package com.starter.crudexample.application.item.retrieve.list;

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
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.pagination.SearchQuery;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;

@IntegrationTest
public class ListItemsUseCaseIT {
    @Autowired
    private ListItemsUseCase useCase;

    @Autowired
    private ItemRepository itemRepository;

    @MockitoSpyBean
    private ItemGateway itemGateway;

    @Test
    public void givenAValidQuery_whenCallsListItems_shouldReturnAll() {
        // Given
        final var aItems = List.of(
                Item.newItem("Item 1", "Description 1", 10.0),
                Item.newItem("Item 2", "Description 2", 20.0),
                Item.newItem("Item 3", "Description 3", 30.0));

        this.itemRepository.saveAllAndFlush(
                aItems.stream()
                        .map(ItemJpaEntity::from)
                        .toList());

        Assertions.assertEquals(3, this.itemRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 3;

        final var expectedItems = aItems.stream()
                .map(ItemListOutput::from)
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
        Assertions.assertTrue(
                expectedItems.size() == actualOutput.items().size()
                        && expectedItems.containsAll(actualOutput.items()));

        verify(itemGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListItemsAndIsEmpty_shouldReturn() {
        // Given
        Assertions.assertEquals(0, this.itemRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
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

        verify(itemGateway).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListItemsAndGatewayThrowsRandomException_shouldException() {
        // Given
        Assertions.assertEquals(0, this.itemRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(itemGateway).findAll(any());

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

        verify(itemGateway).findAll(any());
    }
}