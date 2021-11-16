package com.musala.gatewaysapi.validations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.UUID;

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.customViolationTemplateGeneration;
import static java.lang.annotation.ElementType.*;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, PARAMETER})
@Constraint(validatedBy = IsUuidImpl.class)
public @interface IsUuid {
    String message() default DEFAULT_REQUESTED_UUID_IS_NOT_VALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
@Component
@RequiredArgsConstructor
class IsUuidImpl
        implements ConstraintValidator<IsUuid, String> {
    @Override
    public void initialize(IsUuid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext constraintValidatorContext) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            customViolationTemplateGeneration(
                    MessageFormat.format(REQUESTED_UUID_IS_NOT_VALID, uuid),constraintValidatorContext);
            return false;
        }
        return true;
    }
}