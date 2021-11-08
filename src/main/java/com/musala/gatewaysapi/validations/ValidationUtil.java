package com.musala.gatewaysapi.validations;

import com.google.gson.Gson;
import com.musala.gatewaysapi.models.BindingError;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@NoArgsConstructor
public class ValidationUtil {
    private static final Gson gson = new Gson();

    public static void customViolationTemplateGeneration (
            @NonNull final String message,
            @NonNull final ConstraintValidatorContext constraintValidatorContext
    ) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    public static void handleBindResult(BindingResult result) {
        if (result.getAllErrors().size() == 0)
            return;
        StringJoiner validationFailedMessages = new StringJoiner(", ");
        result.getAllErrors().forEach(
                objectError -> {
                    BindingError error = gson.fromJson(gson.toJson(objectError), BindingError.class);
                    log.info(error.toString());
                    validationFailedMessages.add(error.toString());

                }
        );
        throw new ValidationException(validationFailedMessages.toString());
    }

    public static Boolean isMatchingPattern (String regx, String value) {
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
