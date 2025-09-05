package com.starter.crudexample.domain.user;

import java.util.Objects;

import com.starter.crudexample.domain.Identifier;
import com.starter.crudexample.domain.utils.IdUtils;

public class UserID extends Identifier {

    private final String value;

    private UserID(final String anId) {
        Objects.requireNonNull(anId);
        this.value = anId;
    }

    public static UserID unique() {
        return UserID.from(IdUtils.uuid());
    }

    public static UserID from(final String anId) {
        return new UserID(anId);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserID that = (UserID) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}