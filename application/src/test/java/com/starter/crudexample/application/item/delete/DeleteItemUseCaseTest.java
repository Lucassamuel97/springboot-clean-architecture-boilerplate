package com.starter.crudexample.application.item.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.starter.crudexample.application.UseCaseTest;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemGateway;
import com.starter.crudexample.domain.item.ItemID;

public class DeleteItemUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DefaultDeleteItemUseCase useCase;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(itemGateway);
    }

    @Test
    public void givenAValidId_whenCallsDeleteItem_shouldDeleteIt() {
        // given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();

        doNothing()
                .when(itemGateway).deleteById(any());

        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // then
        verify(itemGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteItem_shouldBeOk() {
        // given
        final var expectedId = ItemID.from("invalid-id");

        doNothing()
                .when(itemGateway).deleteById(any());

        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // then
        verify(itemGateway).deleteById(eq(expectedId));
    }

    @Test
    // dadoUmIdValido_quandoChamarDeleteItemEGatewayLancarExcecao_deveReceberExcecao
    public void givenAValidId_whenCallsDeleteItemAndGatewayThrowsException_shouldReceiveException() {
        // given
        final var aItem = Item.newItem("Item 1", "Description 1", 10.0);

        final var expectedId = aItem.getId();

        doThrow(new RuntimeException("Gateway error"))
                .when(itemGateway).deleteById(any());

        // when
        Assertions.assertThrows(RuntimeException.class, () -> useCase.execute(expectedId.getValue()));

        // then
        verify(itemGateway).deleteById(eq(expectedId));
    }
}