package com.starter.crudexample.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.starter.crudexample.infrastructure.item.models.CreateItemRequest;
import com.starter.crudexample.infrastructure.item.models.ItemResponse;
import com.starter.crudexample.infrastructure.item.models.UpdateItemRequest;

@RequestMapping(value = "items")
@Tag(name = "Items")
public interface ItemAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> create(@RequestBody CreateItemRequest input);

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a item by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "item retrieved"),
            @ApiResponse(responseCode = "404", description = "item was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ItemResponse getById(@PathVariable String id);

    @PutMapping(
            value = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a Item by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated"),
            @ApiResponse(responseCode = "404", description = "Item was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> updateById(@PathVariable String id, @RequestBody UpdateItemRequest aBody);
}
