package com.starter.crudexample.infrastructure.item;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;
import com.starter.crudexample.MySQLGatewayTest;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.pagination.SearchQuery;

@MySQLGatewayTest
public class ItemMySQLGatewayTest {
    @Autowired
    private ItemMySQLGateway itemGateway;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testDependencies() {
        Assertions.assertNotNull(itemGateway);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    public void givenAValidItem_whenCallsCreate_shouldPersistIt() {
        // given
        final var expectedName = "Item 1";
        final var expectedDescription = "Item 1 description";
        final var expectedPrice = 10.0;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        final var expectedId = aItem.getId();

        Assertions.assertEquals(0, itemRepository.count());

        // when
        final var actualItem = itemGateway.create(Item.with(aItem));

        // then
        Assertions.assertEquals(1, itemRepository.count());

        Assertions.assertEquals(expectedId, actualItem.getId());
        Assertions.assertEquals(expectedName, actualItem.getName());
        Assertions.assertEquals(expectedDescription, actualItem.getDescription());
        Assertions.assertEquals(expectedPrice, actualItem.getPrice());
        Assertions.assertNotNull(actualItem.getCreatedAt());
        Assertions.assertNotNull(actualItem.getUpdatedAt());

        final var actualEntity = itemRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedPrice, actualEntity.getPrice());
        Assertions.assertNotNull(actualEntity.getCreatedAt());
        Assertions.assertNotNull(actualEntity.getUpdatedAt());
    }

    @Test
    public void givenAValidItem_whenCallsUpdate_shouldRefreshIt() {
        // given
        final var expectedName = "Item updated";
        final var expectedDescription = "Item update description";
        final var expectedPrice = 20.0;
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);
        final var expectedId = aItem.getId();

        final var currentItem = itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        Assertions.assertEquals(1, itemRepository.count());
        Assertions.assertEquals(expectedId.getValue(), currentItem.getId());
        Assertions.assertEquals(aItem.getName(), currentItem.getName());

        // when
        final var actualItem = itemGateway.update(
                Item.with(aItem).update(expectedName, expectedDescription, expectedPrice));

        // then
        Assertions.assertEquals(1, itemRepository.count());

        Assertions.assertEquals(expectedId, actualItem.getId());
        Assertions.assertEquals(expectedName, actualItem.getName());
        Assertions.assertEquals(expectedDescription, actualItem.getDescription());
        Assertions.assertEquals(expectedPrice, actualItem.getPrice());
        Assertions.assertEquals(aItem.getCreatedAt(), actualItem.getCreatedAt());
        Assertions.assertTrue(aItem.getUpdatedAt().isBefore(actualItem.getUpdatedAt()));

        final var actualEntity = itemRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedPrice, actualEntity.getPrice());
        Assertions.assertEquals(aItem.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aItem.getUpdatedAt().isBefore(actualEntity.getUpdatedAt()));
    }

    @Test
    public void givenTwoItemsAndOnePersisted_whenCallsExistsByIds_shouldReturnPersistedID() {
        // given
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);
        final var expectedId = aItem.getId();

        Assertions.assertEquals(0, itemRepository.count());
        itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        Assertions.assertEquals(1, itemRepository.count());

        // when
        final var actualItem = itemGateway.existsByIds(
                List.of(expectedId, ItemID.from("123")));

        // then
        Assertions.assertEquals(1, actualItem.size());
        Assertions.assertTrue(actualItem.contains(expectedId));
        Assertions.assertEquals(expectedId.getValue(), actualItem.get(0).getValue());
    }

    @Test
    public void givenAValidItem_whenCallsDeleteById_shouldDeleteIt() {
        // given
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);
        final var expectedId = aItem.getId();

        Assertions.assertEquals(0, itemRepository.count());
        itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        Assertions.assertEquals(1, itemRepository.count());

        // when
        itemGateway.deleteById(expectedId);

        // then
        Assertions.assertEquals(0, itemRepository.count());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
        // given
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);

        Assertions.assertEquals(0, itemRepository.count());
        itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        Assertions.assertEquals(1, itemRepository.count());

        // when
        itemGateway.deleteById(ItemID.from("123"));

        // then
        Assertions.assertEquals(1, itemRepository.count());
    }

    @Test
    public void givenAValidItem_whenCallsFindById_shouldReturnIt() {
        // given
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);
        final var expectedId = aItem.getId();

        Assertions.assertEquals(0, itemRepository.count());
        itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        Assertions.assertEquals(1, itemRepository.count());

        // when
        final var actualItem = itemGateway.findById(expectedId);

        // then
        Assertions.assertTrue(actualItem.isPresent());

        Assertions.assertEquals(expectedId, actualItem.get().getId());
        Assertions.assertEquals(aItem.getName(), actualItem.get().getName());
        Assertions.assertEquals(aItem.getDescription(), actualItem.get().getDescription());
        Assertions.assertEquals(aItem.getPrice(), actualItem.get().getPrice());
        Assertions.assertEquals(aItem.getCreatedAt(), actualItem.get().getCreatedAt());
        Assertions.assertEquals(aItem.getUpdatedAt(), actualItem.get().getUpdatedAt());
    }

    @Test
    public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var aItem = Item.newItem("Item 1", "Item 1 description", 10.0);

        Assertions.assertEquals(0, itemRepository.count());
        itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        Assertions.assertEquals(1, itemRepository.count());

        // when
        final var actualItem = itemGateway.findById(ItemID.from("123"));

        // then
        Assertions.assertTrue(actualItem.isEmpty());
    }

    @Test
    public void givenEmptyItems_whenCallsFindAll_shouldReturnEmpty() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        // when
        final var actualResult = itemGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertTrue(actualResult.items().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "not,0,10,1,1,Notebook",
            "lular,0,10,1,1,Celular",
            "tabl,0,10,1,1,Tablet",
            "IMP,0,10,1,1,Impressora",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName) {
        // given
        this.mockItems();

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                "name",
                "asc");

        // when
        final var actualResult = itemGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Celular",
            "name,desc,0,10,5,5,Tablet",
            "createdAt,asc,0,10,5,5,Notebook",
            "createdAt,desc,0,10,5,5,Item test5",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnSorted(
        final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        this.mockItems();

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                "",
                expectedSort,
                expectedDirection);

        // when
        final var actualResult = itemGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    private void mockItems() {
        // Salvando individualmente para garantir diferenças (sequenciais) em createdAt
        // e preservar a ordenação determinística nos testes de sort por createdAt.
        itemRepository.saveAndFlush(ItemJpaEntity.from(Item.newItem("Notebook", "Item 1 description", 10.0)));
        itemRepository.saveAndFlush(ItemJpaEntity.from(Item.newItem("Celular", "Item 2 description", 20.0)));
        itemRepository.saveAndFlush(ItemJpaEntity.from(Item.newItem("Tablet", "Item 3 description", 30.0)));
        itemRepository.saveAndFlush(ItemJpaEntity.from(Item.newItem("Impressora", "Item 4 description", 40.0)));
        itemRepository.saveAndFlush(ItemJpaEntity.from(Item.newItem("Item test5", "Item 5 description", 50.0)));
    }
}