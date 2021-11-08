package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.services.GatewayService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.UUID;

import static com.musala.gatewaysapi.constants.Constants.MAX_DEVICES_PER_GATEWAY;
import static com.musala.gatewaysapi.validations.ValidationErrorMessages.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.customViolationTemplateGeneration;
import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, PARAMETER})
@Constraint(validatedBy = DidGatewayDevicesExceededLimitImpl.class)
public @interface DidGatewayDevicesExceededLimit {
    String message() default DEFAULT_NUMBER_OF_DEVICES_EXCEEDED_LIMIT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
@Component
@AllArgsConstructor
class DidGatewayDevicesExceededLimitImpl
        implements ConstraintValidator<DidGatewayDevicesExceededLimit, String> {

    private GatewayService gatewayService;

    @Override
    public void initialize(DidGatewayDevicesExceededLimit constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String gatewayUuid, ConstraintValidatorContext constraintValidatorContext) {
        try {
            UUID.fromString(gatewayUuid);
        } catch (IllegalArgumentException exception) {
            customViolationTemplateGeneration(
                    MessageFormat.format(REQUESTED_GATEWAY_UUID_IS_NOT_VALID, gatewayUuid),constraintValidatorContext);
            return false;
        }
        try {
            Assert.isTrue(gatewayService.getGateway(gatewayUuid).getDevices().size() < MAX_DEVICES_PER_GATEWAY,
                    MessageFormat.format(NUMBER_OF_DEVICES_EXCEEDED_LIMIT, gatewayUuid));
        } catch (IllegalArgumentException exception) {
            customViolationTemplateGeneration( exception.getMessage() ,constraintValidatorContext);
            return false;
        }
        return true;
    }
}
