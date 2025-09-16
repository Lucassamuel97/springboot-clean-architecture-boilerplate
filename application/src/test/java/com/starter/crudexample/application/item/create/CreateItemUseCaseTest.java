package com.starter.crudexample.application.item.create;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.item.ItemGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Objects;

public class CreateItemUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateItemUseCase useCase;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(itemGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateItem_shouldReturnIt() {
        // given
        final var expectedName = "Smartphone";
        final var expectedDescription = "Latest smartphone model";
        final var expectedPrice = 999.99;

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        when(itemGateway.create(any()))
                .thenAnswer(returnsFirstArg());
                
        // when
        final var actualOutput  = useCase.execute(aCommand);

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(itemGateway).create(argThat(aItem ->
                Objects.nonNull(aItem.getId())
                        && Objects.equals(expectedName, aItem.getName())
                        && Objects.equals(expectedDescription, aItem.getDescription())
                        && Objects.equals(expectedPrice, aItem.getPrice())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = null;
        final String expectedDescription = "Valid description";
        final double expectedPrice = 99.99;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway, times(0)).create(any());
    }

    @Test
    public void givenAInvalidDescription_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = "Valid Name";
        final String expectedDescription = null;
        final double expectedPrice = 99.99;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway, times(0)).create(any());
    }

    @Test
    public void givenAInvalidPrice_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = "Valid Name";
        final String expectedDescription = "Valid description";
        final double expectedPrice = -1;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be negative";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway, times(0)).create(any());
    }

    @Test
    public void givenAInvalidNullPrice_whenCallsCreateItem_shouldThrowsNotificationException() {
        // given
        final String expectedName = "Valid Name";
        final String expectedDescription = "Valid description";
        final Double expectedPrice = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'price' should not be null";

        final var aCommand = CreateItemCommand.with(expectedName, expectedDescription, expectedPrice);

        // when
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            useCase.execute(aCommand);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(itemGateway, times(0)).create(any());
    }
}