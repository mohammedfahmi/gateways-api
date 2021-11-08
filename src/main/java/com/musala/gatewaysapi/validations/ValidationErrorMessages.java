package com.musala.gatewaysapi.validations;

import lombok.experimental.UtilityClass;

@SuppressWarnings("RedundantModifiersUtilityClassLombok")
@UtilityClass
public class ValidationErrorMessages {
    public static final String REQUESTED_PAGE_NOT_AVAILABLE = "Requested page {0} is not available";
    public static final String DEFAULT_REQUESTED_PAGE_NOT_AVAILABLE = "Page is not available";
    public static final String REQUESTED_UUID_IS_NOT_VALID = "Requested uuid {0} is not valid";
    public static final String DEFAULT_REQUESTED_UUID_IS_NOT_VALID = "Requested uuid is not valid";
    public static final String REQUESTED_GATEWAY_UUID_IS_NOT_VALID = "Requested gateway uuid {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_UUID_IS_NOT_VALID = "Requested gateway uuid is not valid";
    public static final String REQUESTED_GATEWAY_IPV4_IS_NOT_VALID = "Requested gateway IPv4 {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID = "Requested gateway IPv4 is not valid";
    public static final String REQUESTED_GATEWAY_NAME_IS_NOT_VALID = "Requested gateway name {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_NAME_IS_NOT_VALID = "Requested gateway name is not valid";
    public static final String REQUESTED_DEVICE_UUID_IS_NOT_VALID = "Requested device uuid {0} is not valid";
    public static final String DEFAULT_REQUESTED_DEVICE_UUID_IS_NOT_VALID = "Requested device uuid is not valid";
    public static final String REQUESTED_DEVICE_NAME_IS_NOT_VALID = "Requested device name {0} is not valid";
    public static final String DEFAULT_REQUESTED_DEVICE_NAME_IS_NOT_VALID = "Requested device name is not valid";
    public static final String REQUESTED_DEVICE_VENDOR_IS_NOT_VALID = "Requested device vendor {0} is not valid";
    public static final String DEFAULT_REQUESTED_DEVICE_VENDOR_IS_NOT_VALID = "Requested device vendor is not valid";
    public static final String REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID = "Requested device creation date {0} is not valid";
    public static final String DEFAULT_REQUESTED_DEVICE_CREATION_DATE_IS_NOT_VALID = "Requested device creation date is not valid";
    public static final String DEFAULT_REQUESTED_DEVICE_STATUS_IS_NOT_VALID = "Requested device status is not valid";
    public static final String NUMBER_OF_DEVICES_EXCEEDED_LIMIT = "can't create any more devices for requested gateway with gateway uuid {0}";
    public static final String DEFAULT_NUMBER_OF_DEVICES_EXCEEDED_LIMIT = "can't create any more devices for requested gateway";
}
