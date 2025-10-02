package com.starter.crudexample.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.crudexample.ControllerTest;
import com.starter.crudexample.application.item.create.CreateItemOutput;
import com.starter.crudexample.application.item.create.DefaultCreateItemUseCase;
import com.starter.crudexample.application.item.retrieve.get.DefaultGetItemByIdUseCase;
import com.starter.crudexample.application.item.retrieve.get.ItemOutput;
import com.starter.crudexample.application.item.update.DefaultUpdateItemUseCase;
import com.starter.crudexample.application.item.update.UpdateItemOutput;
import com.starter.crudexample.domain.exceptions.NotFoundException;
import com.starter.crudexample.domain.exceptions.NotificationException;
import com.starter.crudexample.domain.item.Item;
import com.starter.crudexample.domain.item.ItemID;
import com.starter.crudexample.domain.validation.Error;
import com.starter.crudexample.infrastructure.item.models.CreateItemRequest;
import com.starter.crudexample.infrastructure.item.models.UpdateItemRequest;

@ControllerTest(controllers = ItemAPI.class)
public class ItemAPITest {

        @Autowired
        private MockMvc mvc;

        @Autowired
        private ObjectMapper mapper;

        @MockitoBean
        private DefaultCreateItemUseCase createItemUseCase;

        @MockitoBean
        private DefaultGetItemByIdUseCase getItemByIdUseCase;

        @MockitoBean
        private DefaultUpdateItemUseCase updateItemUseCase;

        @Test
        public void givenAValidCommand_whenCallsCreateItem_thenShouldReturnItemId() throws Exception {
                // Given
                final var expectedName = "Item name";
                final var expectedDescription = "Item description";
                final var expectedPrice = 10.0;
                final var expectedId = ItemID.from("123");

                final var aCommand = new CreateItemRequest(expectedName, expectedDescription, expectedPrice);

                when(createItemUseCase.execute(any()))
                                .thenReturn(new CreateItemOutput("123"));

                // When
                final var response = this.mvc.perform(post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(aCommand)))
                                .andDo(print());

                // Then
                response.andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.header().string("Location",
                                                Matchers.equalTo("/items/" + expectedId.getValue())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id",
                                                Matchers.equalTo(expectedId.getValue())));

                verify(createItemUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedName, cmd.name())
                                && Objects.equals(expectedDescription, cmd.description())
                                && Objects.equals(expectedPrice, cmd.price())));
        }

        @Test
        public void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
                // given
                final String expectedName = null;
                final var expectedDescription = "No description";
                final var expectedPrice = 10.0;
                final var expectedMessage = "'name' should not be null";

                final var aCommand = new CreateItemRequest(expectedName, expectedDescription, expectedPrice);

                when(createItemUseCase.execute(any()))
                                .thenThrow(NotificationException.with(new Error(expectedMessage)));

                // when
                final var aRequest = post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(aRequest)
                                .andDo(print());

                // then
                response.andExpect(status().isUnprocessableEntity())
                                .andExpect(header().string("Location", nullValue()))
                                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.errors", hasSize(1)))
                                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

                verify(createItemUseCase).execute(argThat(actualCmd -> Objects.equals(expectedName, actualCmd.name())
                                && Objects.equals(expectedDescription, actualCmd.description())
                                && Objects.equals(expectedPrice, actualCmd.price())));
        }

        @Test
        public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
                // given
                final var expectedName = "Item name";
                final var expectedDescription = "Item description";
                final var expectedPrice = 10.0;

                final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
                final var expectedId = aItem.getId().getValue();

                when(getItemByIdUseCase.execute(any()))
                                .thenReturn(ItemOutput.from(aItem));

                // when
                final var aRequest = get("/items/{id}", expectedId)
                                .accept(MediaType.APPLICATION_JSON);

                final var response = this.mvc.perform(aRequest);

                // then
                response.andExpect(status().isOk())
                                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                                .andExpect(jsonPath("$.price", equalTo(expectedPrice)))
                                .andExpect(jsonPath("$.created_at", equalTo(aItem.getCreatedAt().toString())))
                                .andExpect(jsonPath("$.updated_at", equalTo(aItem.getUpdatedAt().toString())));

                verify(getItemByIdUseCase).execute(eq(expectedId));
        }

        @Test
        public void givenAInvalidId_whenCallsGetByIdAndItemDoesntExists_shouldReturnNotFound() throws Exception {
                // given
                final var expectedId = ItemID.from("123");

                when(getItemByIdUseCase.execute(any()))
                                .thenThrow(NotFoundException.with(Item.class, expectedId));

                // when
                final var aRequest = get("/items/{id}", expectedId.getValue())
                                .accept(MediaType.APPLICATION_JSON);

                final var response = this.mvc.perform(aRequest);

                // then
                response.andExpect(status().isNotFound());

                verify(getItemByIdUseCase).execute(eq(expectedId.getValue()));
        }

        @Test
        public void givenAValidCommand_whenCallsUpdateItem_shouldReturnItsIdentifier() throws Exception {
                // given
                final var expectedName = "Item name";
                final var expectedDescription = "Item description";
                final var expectedPrice = 10.0;

                final var aItem = Item.newItem(expectedName, expectedDescription, expectedPrice);
                final var expectedId = aItem.getId();

                final var aCommand = new UpdateItemRequest(expectedName, expectedDescription, expectedPrice);

                when(updateItemUseCase.execute(any()))
                                .thenReturn(UpdateItemOutput.from(expectedId));

                // when
                final var aRequest = put("/items/{id}", expectedId.getValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(aRequest)
                                .andDo(print());

                // then
                response.andExpect(status().isOk())
                                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

                verify(updateItemUseCase)
                                .execute(argThat(actualCmd -> Objects.equals(expectedId.getValue(), actualCmd.id())
                                                && Objects.equals(expectedName, actualCmd.name())
                                                && Objects.equals(expectedDescription, actualCmd.description())
                                                && Objects.equals(expectedPrice, actualCmd.price())));
        }

        @Test
        public void givenAnInvalidName_whenCallsUpdateItem_shouldReturnNotification() throws Exception {
                // given
                final var expectedId = ItemID.from("123");
                final String expectedName = null;
                final var expectedDescription = "Item description";
                final var expectedPrice = 10.0;
                final var expectedMessage = "'name' should not be null";

                final var aCommand = new UpdateItemRequest(expectedName, expectedDescription, expectedPrice);

                when(updateItemUseCase.execute(any()))
                                .thenThrow(NotificationException.with(new Error(expectedMessage)));

                // when
                final var aRequest = put("/items/{id}", expectedId.getValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(aRequest)
                                .andDo(print());

                // then
                response.andExpect(status().isUnprocessableEntity())
                                .andExpect(header().string("Location", nullValue()))
                                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.errors", hasSize(1)))
                                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));
                verify(updateItemUseCase)
                                .execute(argThat(actualCmd -> Objects.equals(expectedId.getValue(), actualCmd.id())
                                                && Objects.equals(expectedName, actualCmd.name())
                                                && Objects.equals(expectedDescription, actualCmd.description())
                                                && Objects.equals(expectedPrice, actualCmd.price())));
        }
}