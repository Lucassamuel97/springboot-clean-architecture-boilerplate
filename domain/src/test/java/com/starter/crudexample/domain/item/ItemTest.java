package com.starter.crudexample.domain.item;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testItemCreation() {
        Item item = new Item("Test Item", "Test Description");
        assertNotNull(item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
    }
}
