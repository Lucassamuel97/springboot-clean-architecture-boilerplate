package com.starter.crudexample.domain.item;

import com.starter.crudexample.domain.validation.ValidationHandler;
import com.starter.crudexample.domain.validation.Validator;
import com.starter.crudexample.domain.validation.Error;

public class ItemValidator extends Validator {

    public static final int NAME_MAX_LENGTH = 255;
    public static final int NAME_MIN_LENGTH = 3;
    private final Item item;

    public ItemValidator(final Item aItem, final ValidationHandler aHandler) {
        super(aHandler);
        this.item = aItem;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkDescriptionConstraints();
        checkPriceConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.item.getName();
        if (name == null) {
            this.validationHandler().append(new Error("'name' should not be null"));
            return;
        }

        if (name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty"));
            return;
        }

        final int length = name.trim().length();
        if (length > NAME_MAX_LENGTH || length < NAME_MIN_LENGTH) {
            this.validationHandler().append(new Error("'name' must be between 3 and 255 characters"));
            return;
        }

    }

    private void checkDescriptionConstraints() {
        final var description = this.item.getDescription();
        if (description == null) {
            this.validationHandler().append(new Error("'description' should not be null"));
            return;
        }

        if (description.length() > 1000) {
            this.validationHandler().append(new Error("'description' must be less than 1000 characters"));
            return;
        }
    }

    private void checkPriceConstraints() {
        final var price = this.item.getPrice();
        if (price == null) {
            this.validationHandler().append(new Error("'price' should not be null"));
            return;
        }

        if (price < 0) {
            this.validationHandler().append(new Error("'price' should not be negative"));
            return;
        }
    }
}