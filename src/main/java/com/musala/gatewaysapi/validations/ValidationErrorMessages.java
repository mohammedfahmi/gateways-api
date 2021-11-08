package com.musala.gatewaysapi.validations;

import lombok.experimental.UtilityClass;

@SuppressWarnings("RedundantModifiersUtilityClassLombok")
@UtilityClass
public class ValidationErrorMessages {
    public static final String REQUESTED_PAGE_NOT_AVAILABLE = "Requested page {0} is not available";
    public static final String DEFAULT_REQUESTED_PAGE_NOT_AVAILABLE = "Page is not available";
    public static final String REQUESTED_GATEWAY_UUID_IS_NOT_VALID = "Requested gateway uuid {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_UUID_IS_NOT_VALID = "Requested gateway uuid is not valid";
    public static final String REQUESTED_GATEWAY_IPV4_IS_NOT_VALID = "Requested gateway IPv4 {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_IPV4_IS_NOT_VALID = "Requested gateway IPv4 is not valid";
    public static final String REQUESTED_GATEWAY_NAME_IS_NOT_VALID = "Requested gateway name {0} is not valid";
    public static final String DEFAULT_REQUESTED_GATEWAY_NAME_IS_NOT_VALID = "Requested gateway name is not valid";
}
