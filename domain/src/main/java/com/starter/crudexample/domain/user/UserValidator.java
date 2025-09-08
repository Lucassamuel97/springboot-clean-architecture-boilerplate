package com.starter.crudexample.domain.user;

import com.starter.crudexample.domain.validation.ValidationHandler;
import com.starter.crudexample.domain.validation.Validator;
import com.starter.crudexample.domain.validation.Error;

public class UserValidator extends Validator {    

    public static final int NAME_MAX_LENGTH = 25;
    public static final int NAME_MIN_LENGTH = 1;

    private final User user;

    protected UserValidator(final User aUser, final ValidationHandler aHandler) {
        super(aHandler);
        this.user = aUser;
    }

    @Override
    public void validate() {
        checkUsernameConstraints();
        checkEmailConstraints();
        checkPasswordConstraints();
        checkRolesConstraints();
    }

     private void checkUsernameConstraints() {
        final var username = this.user.getUsername();
        if (username == null) {
            this.validationHandler().append(new Error("'username' should not be null"));
            return;
        }

        if (username.isBlank()) {
            this.validationHandler().append(new Error("'username' should not be empty"));
            return;
        }

        final int length = username.trim().length();
        if (length > NAME_MAX_LENGTH || length < NAME_MIN_LENGTH) {
            this.validationHandler().append(new Error("'username' must be between 1 and 25 characters"));
        }        
    }

    private void checkEmailConstraints() {
        final var email = this.user.getEmail();
        if (email == null) {
            this.validationHandler().append(new Error("'email' should not be null"));
            return;
        }

        if (email.isBlank()) {
            this.validationHandler().append(new Error("'email' should not be empty"));
            return;
        }

        // Validação básica de formato de email
        if (!email.contains("@") || !email.contains(".")) {
            this.validationHandler().append(new Error("'email' should be a valid email address"));
            return;
        }

        final int length = email.trim().length();
        if (length > 255 || length < 5) {
            this.validationHandler().append(new Error("'email' must be between 5 and 255 characters"));
        }
    }

    private void checkPasswordConstraints() {
        final var password = this.user.getPassword();
        if (password == null) {
            this.validationHandler().append(new Error("'password' should not be null"));
            return;
        }

        if (password.isBlank()) {
            this.validationHandler().append(new Error("'password' should not be empty"));
            return;
        }

        final int length = password.trim().length();
        if (length > 255 || length < 6) {
            this.validationHandler().append(new Error("'password' must be between 6 and 255 characters"));
        }        
    }

    private void checkRolesConstraints() {
        final var roles = this.user.getRoles();
        if (roles == null) {
            this.validationHandler().append(new Error("'roles' should not be null"));
            return;
        }

        if (roles.isEmpty()) {
            this.validationHandler().append(new Error("'roles' should not be empty"));
            return;
        }

        // Validar se todas as roles estão presentes no enum Role
        for (Role role : roles) {
            if (role == null) {
                this.validationHandler().append(new Error("'role' should valid and not null"));
                continue;
            }
            
            boolean isValidRole = false;
            for (Role validRole : Role.values()) {
                if (role.equals(validRole)) {
                    isValidRole = true;
                    break;
                }
            }
            
            if (!isValidRole) {
                this.validationHandler().append(new Error("'role' " + role + " is not a valid role"));
            }
        }
    }
}
