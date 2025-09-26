package com.starter.crudexample.application.item.update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.starter.crudexample.IntegrationTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.infrastructure.item.persistence.ItemJpaEntity;
import com.starter.crudexample.infrastructure.item.persistence.ItemRepository;


@IntegrationTest
public class UpdateItemUseCaseIT {
    @Autowired
    private UpdateItemUseCase useCase;

    @Autowired
    private ItemRepository itemRepository;

    @MockitoSpyBean
    private ItemGateway itemGateway;

    
    @Test
    public void givenAValidCommand_whenCallsUpdateItem_shouldReturnItsIdentifier() {
        // given
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));
        
        final var expectedId = aItem.getId();
        final var expectedNewName = "Updated Item Name";
        final var expectedNewDescription = "Updated Item Description";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedNewName,
                expectedNewDescription,
                expectedPrice
        );

        // when
        final var actualOutput = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final var actualPersistedItem = this.itemRepository.findById(expectedId.getValue()).get();
        
        Assertions.assertEquals(expectedId.getValue(), actualPersistedItem.getId());
        Assertions.assertEquals(expectedNewName, actualPersistedItem.getName());
        Assertions.assertEquals(expectedNewDescription, actualPersistedItem.getDescription());
        Assertions.assertEquals(expectedPrice, actualPersistedItem.getPrice());
        Assertions.assertNotNull(actualPersistedItem.getCreatedAt());
        Assertions.assertTrue(actualPersistedItem.getUpdatedAt().isAfter(actualPersistedItem.getCreatedAt()));

        verify(itemGateway).findById(any());
        verify(itemGateway).update(any());
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateItem_shouldThrowsNotificationException() {
        // given
        final var aItem = Item.newItem(
            "Valid Item Name",
            "Valid Item Description",
            10.0);
        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        final var expectedId = aItem.getId();
        final String expectedNewName = null;
        final var expectedNewDescription = "Updated Item Description";
        final var expectedPrice = 10.0;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedNewName,
                expectedNewDescription,
                expectedPrice
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    
        verify(itemGateway).findById(any());
        verify(itemGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidDescription_whenCallsUpdateItem_shouldThrowsNotificationException() {
        // given
        final var aItem = Item.newItem(
            "Valid Item Name",
            "Valid Item Description",
            10.0);
        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        final var expectedId = aItem.getId();
        final var expectedNewName = "Updated Item Name";
        final String expectedNewDescription = null;
        final var expectedPrice = 10.0;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedNewName,
                expectedNewDescription,
                expectedPrice
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    
        verify(itemGateway).findById(any());
        verify(itemGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidPrice_whenCallsUpdateItem_shouldThrowsNotificationException() {
        // given
        final var aItem = Item.newItem(
            "Valid Item Name",
            "Valid Item Description",
            10.0);
        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        final var expectedId = aItem.getId();
        final var expectedNewName = "Updated Item Name";
        final var expectedNewDescription = "Updated Item Description";
        final var expectedPrice = -1.0;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be negative";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedNewName,
                expectedNewDescription,
                expectedPrice
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    
        verify(itemGateway).findById(any());
        verify(itemGateway, times(0)).update(any());
    }

    //testando se os 3 erros juntos funcionam
    @Test
    public void givenAInvalidCommand_whenCallsUpdateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = null;
        final String expectedDescription = null;
        final Double expectedPrice = -10.0;
        final var expectedErrorCount = 3;
        final var expectedErrorMessage1 = "'name' should not be null";
        final var expectedErrorMessage2 = "'description' should not be null";
        final var expectedErrorMessage3 = "'price' should not be negative";

        final var aItem = Item.newItem(
            "Valid Item Name",
            "Valid Item Description",
            10.0);

        this.itemRepository.saveAndFlush(ItemJpaEntity.from(aItem));

        final var expectedId = aItem.getId();

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );
        // when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage1, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessage2, actualException.getErrors().get(1).message());
        Assertions.assertEquals(expectedErrorMessage3, actualException.getErrors().get(2).message());
        verify(itemGateway).findById(any());
        verify(itemGateway, times(0)).update(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateItem_shouldThrowsNotFoundException() {
        // given
        final var expectedId = ItemID.from("123");
        final var expectedName = "Valid Item Name";
        final var expectedDescription = "Valid Item Description";
        final var expectedPrice = 10.0;

        final var expectedErrorMessage = "Item with ID 123 was not found";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(itemGateway).findById(any());
        verify(itemGateway, times(0)).update(any());
    }


}