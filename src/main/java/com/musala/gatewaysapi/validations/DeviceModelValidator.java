package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.models.DeviceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.*;
import static java.time.ZoneOffset.UTC;

@SuppressWarnings("NullableProblems")
@Slf4j
@Component
public class DeviceModelValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return DeviceModel.class.equals(clazz);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void validate(Object target, Errors errors) {
        DeviceModel requestBody = (DeviceModel) target;
        log.info("request object: "+ requestBody);
        try {
            Assert.notNull(requestBody.getDevicesUuid(), DEFAULT_REQUESTED_DEVICE_UUID_IS_NOT_VALID);
            Assert.hasText(requestBody.getDevicesUuid(), DEFAULT_REQUESTED_DEVICE_UUID_IS_NOT_VALID);
            UUID.fromString(requestBody.getDevicesUuid());
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getDevicesUuid() != null && !Objects.equals(requestBody.getDevicesUuid(), "")) ?
                    MessageFormat.format(REQUESTED_DEVICE_UUID_IS_NOT_VALID, requestBody.getDevicesUuid()) :
                    DEFAULT_REQUESTED_DEVICE_UUID_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("devicesUuid", logMessage);
        }
        try {
            Assert.notNull(requestBody.getDevicesName(), DEFAULT_REQUESTED_DEVICE_NAME_IS_NOT_VALID);
            Assert.hasText(requestBody.getDevicesName(), DEFAULT_REQUESTED_DEVICE_NAME_IS_NOT_VALID);
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getDevicesName() != null && !Objects.equals(requestBody.getDevicesName(), "")) ?
                    MessageFormat.format(REQUESTED_DEVICE_NAME_IS_NOT_VALID, requestBody.getDevicesName()) :
                    DEFAULT_REQUESTED_DEVICE_NAME_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("devicesName", logMessage);
        }
        try {
            Assert.notNull(requestBody.getVendor(), DEFAULT_REQUESTED_DEVICE_VENDOR_IS_NOT_VALID);
            Assert.hasText(requestBody.getVendor(), DEFAULT_REQUESTED_DEVICE_VENDOR_IS_NOT_VALID);
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getVendor() != null && !Objects.equals(requestBody.getVendor(), "")) ?
                    MessageFormat.format(REQUESTED_DEVICE_VENDOR_IS_NOT_VALID, requestBody.getVendor()) :
                    DEFAULT_REQUESTED_DEVICE_VENDOR_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("vendor", logMessage);
        }
        try {
            Assert.notNull(requestBody.getDeviceCreationDate(), DEFAULT_REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID);
            Assert.isTrue(requestBody.getDeviceCreationDate().getYear() > 0, DEFAULT_REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID);
            Assert.isTrue(LocalDateTime.now(UTC)
                    .isAfter( requestBody.getDeviceCreationDate()),DEFAULT_REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID);
        } catch (IllegalArgumentException  exception) {
            String logMessage = (requestBody.getDeviceCreationDate() != null &&  requestBody.getDeviceCreationDate().getYear() > 0) ?
                    MessageFormat.format(REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID, requestBody.getDeviceCreationDate()) :
                    DEFAULT_REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID;
            log.error( logMessage, exception);
            errors.rejectValue("deviceCreationDate", logMessage);
        }
    }
}
