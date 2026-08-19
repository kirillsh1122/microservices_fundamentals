package com.microservice.architecture.overview.storage_service.validation;


import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = FormatAllowed.Validator.class)
public @interface FormatAllowed {

    String message() default "Invalid list of ids provided for the DELETE endpoint";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


    class Validator implements ConstraintValidator<FormatAllowed, String> {

        private final int MAX_LENGTH = 200;

        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.matches("^\\d+(,\\d+)*$")
                    && (!value.isEmpty()) && (value.length() <= MAX_LENGTH);
        }

    }
}
