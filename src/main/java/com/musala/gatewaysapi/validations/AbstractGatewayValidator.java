package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.models.AbstractGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import static com.musala.gatewaysapi.constants.Constants.IPV4_PATTERN;
import static com.musala.gatewaysapi.validations.ValidationErrorMessages.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.isMatchingPattern;

@SuppressWarnings("NullableProblems")
@Slf4j
@Component
public class AbstractGatewayValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AbstractGateway.class.equals(clazz);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void validate(Object target, Errors errors) {
        AbstractGateway requestBody = (AbstractGateway) target;
        try {
            Assert.notNull(requestBody.getGatewayUuid(), DEFAULT_REQUESTED_GATEWAY_UUID_IS_NOT_VALID);
            Assert.hasText(requestBody.getGatewayUuid(), DEFAULT_REQUESTED_GATEWAY_UUID_IS_NOT_VALID);
            UUID.fromString(requestBody.getGatewayUuid());
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getGatewayUuid() != null && !Objects.equals(requestBody.getGatewayUuid(), "")) ?
                    MessageFormat.format(REQUESTED_GATEWAY_UUID_IS_NOT_VALID, requestBody.getGatewayUuid()) :
                    DEFAULT_REQUESTED_GATEWAY_UUID_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("gatewayUuid", logMessage);
        }
        try {
            Assert.notNull(requestBody.getGatewayIpv4(), DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID);
            Assert.hasText(requestBody.getGatewayIpv4(), DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID);
            Assert.isTrue(isMatchingPattern(IPV4_PATTERN, requestBody.getGatewayIpv4()), DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID);
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getGatewayIpv4() != null && !Objects.equals(requestBody.getGatewayIpv4(), "")) ?
                    MessageFormat.format(REQUESTED_GATEWAY_IPV4_IS_NOT_VALID, requestBody.getGatewayIpv4()) :
                    DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("gatewayIpv4", logMessage);
        }
        try {
            Assert.notNull(requestBody.getGatewayName(), DEFAULT_REQUESTED_GATEWAY_NAME_IS_NOT_VALID);
            Assert.hasText(requestBody.getGatewayName(), DEFAULT_REQUESTED_GATEWAY_NAME_IS_NOT_VALID);
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getGatewayName() != null && !Objects.equals(requestBody.getGatewayName(), "")) ?
                    MessageFormat.format(REQUESTED_GATEWAY_NAME_IS_NOT_VALID, requestBody.getGatewayName()) :
                    DEFAULT_REQUESTED_GATEWAY_NAME_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("gatewayName", logMessage);
        }
    }
}
