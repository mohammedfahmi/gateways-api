package com.musala.gatewaysapi.constants;

import lombok.experimental.UtilityClass;

/**
 * Utility Class that hosts all Global Constants
 */
@UtilityClass
public class Constants {
    public static final String IPV4_PATTERN = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
    public static final String GATEWAY_NOT_FOUND_ERROR_MESSAGE = "Requested Gateway with uuid {0} is not Found";

}
