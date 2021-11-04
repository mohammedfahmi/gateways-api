package com.musala.gatewaysapi.validations;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Objects;
@Slf4j
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

    public void handleBindResult(BindingResult result) {
        if (result.getAllErrors().size() == 0)
            return;
        StringBuilder bindingErrors = new StringBuilder();
        StringBuilder validationFailedMessages = new StringBuilder();
        result.getAllErrors().forEach(
                objectError -> {
                    Objects.requireNonNull(objectError.getCodes());
                    int errorCode = objectError.getCodes().length -1;
                    final String[] codes = objectError.getCodes();
                    validationFailedMessages.append(codes[errorCode]);
                    bindingErrors.append(Arrays.deepToString(codes));
                }
        );
        log.info("Binding Failed after Failed Validation; Binding Exception: {}", bindingErrors);
        throw new ValidationException(validationFailedMessages.toString());
    }
}
