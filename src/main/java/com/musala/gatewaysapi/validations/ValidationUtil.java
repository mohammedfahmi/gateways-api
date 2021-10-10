package com.musala.gatewaysapi.validations;

import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

@Component
@NoArgsConstructor
public class ValidationUtil {

    public void customViolationTemplateGeneration (
            @NonNull final String message,
            @NonNull final ConstraintValidatorContext constraintValidatorContext
    ) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
