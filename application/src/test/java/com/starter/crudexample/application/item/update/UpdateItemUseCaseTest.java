package com.starter.crudexample.application.item.update;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;

public class UpdateItemUseCaseTest extends UseCaseTest {

	 @InjectMocks
    private DefaultUpdateItemUseCase useCase;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(itemGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateItem_shouldReturnItsIdentifier() {
        //given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();
        final var expectedName = "Item name updated";
        final var expectedDescription = "Item description updated";
        final var expectedPrice = 20.0;

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        when(itemGateway.findById(any()))
                .thenReturn(Optional.of(Item.with(aItem)));

        when(itemGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        //when
        final var actualOutput = useCase.execute(aCommand);

        //then
        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(itemGateway).findById(eq(expectedId));

        verify(itemGateway).update(argThat(aUpdatedItem ->
                Objects.equals(expectedId, aUpdatedItem.getId())
                        && Objects.equals(expectedName, aUpdatedItem.getName())
                        && Objects.equals(expectedDescription, aUpdatedItem.getDescription())
                        && Objects.equals(expectedPrice, aUpdatedItem.getPrice())
                        && Objects.equals(aItem.getCreatedAt(), aUpdatedItem.getCreatedAt())
                        && aItem.getUpdatedAt().isBefore(aUpdatedItem.getUpdatedAt())
                        && aUpdatedItem.getDeletedAt() == null
        ));
    }
    
    @Test
    public void givenAInvalidName_whenCallsUpdateItem_shouldThrowsNotificationException() {
        //given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();
        final String expectedName = null;
        final var expectedDescription = "Item description updated";
        final var expectedPrice = 20.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        when(itemGateway.findById(any()))
                .thenReturn(Optional.of(Item.with(aItem)));

        //when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        //then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway).findById(eq(expectedId));

        verify(itemGateway, org.mockito.Mockito.never()).update(any());
    
    }

    @Test
    public void givenAInvalidDescription_whenCallsUpdateItem_shouldThrowsNotificationException() {
        //given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();
        final var expectedName = "Item name updated";
        final String expectedDescription = null;
        final var expectedPrice = 20.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        when(itemGateway.findById(any()))
                .thenReturn(Optional.of(Item.with(aItem)));

        //when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        //then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway).findById(eq(expectedId));

        verify(itemGateway, never()).update(any());

    }

    @Test
    public void givenAInvalidPrice_whenCallsUpdateItem_shouldThrowsNotificationException() {
        //given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();
        final var expectedName = "Item name updated";
        final var expectedDescription = "Item description updated";
        final Double expectedPrice = -1.0;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be negative";

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        when(itemGateway.findById(any()))
                .thenReturn(Optional.of(Item.with(aItem)));

        //when
        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        //then
        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway).findById(eq(expectedId));

        verify(itemGateway, never()).update(any());
    }

    @Test
    public void givenAInvalidId_whenCallsUpdateItem_shouldThrowsNotFoundException() {
        //given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();
        final var expectedName = "Item name updated";
        final var expectedDescription = "Item description updated";
        final var expectedPrice = 20.0;
        final var expectedErrorMessage = "Item with ID %s was not found".formatted(expectedId.getValue());

        final var aCommand = UpdateItemCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedPrice
        );

        when(itemGateway.findById(any()))
                .thenReturn(Optional.empty());

        //when
        final var actualException = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(aCommand)
        );

        //then
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(itemGateway).findById(eq(expectedId));
    }
}