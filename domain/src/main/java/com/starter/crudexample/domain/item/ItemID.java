package com.starter.crudexample.domain.item;

import java.util.Objects;

import com.starter.crudexample.domain.Identifier;
import com.starter.crudexample.domain.utils.IdUtils;

public class ItemID extends Identifier {

    private final String value;

    private ItemID(final String anId) {
        Objects.requireNonNull(anId);
        this.value = anId;
    }

    public static ItemID unique() {
        return ItemID.from(IdUtils.uuid());
    }

    public static ItemID from(final String anId) {
        return new ItemID(anId);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ItemID that = (ItemID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}