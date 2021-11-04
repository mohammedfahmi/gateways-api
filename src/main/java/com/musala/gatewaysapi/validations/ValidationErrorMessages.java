package com.musala.gatewaysapi.validations;

import lombok.experimental.UtilityClass;

@SuppressWarnings("RedundantModifiersUtilityClassLombok")
@UtilityClass
public class ValidationErrorMessages {
    public static final String CANNOT_BE_NULL = "cannot be null.";
    public static final String CANNOT_BE_EMPTY = "cannot be empty.";
    public static final String REQUESTED_PAGE_NOT_AVAILABLE = "Requested page {0} is not available";
    public static final String REQUESTED_DEFAULT_PAGE_NOT_AVAILABLE = "Page is not available";
    public static final String REQUESTED_CHANGE_DATE_CANNOT_BE_IN_THE_FUTURE = "requested: {0} cannot be in the future, current time: {1}.";
    public static final String REQUESTED_CHANGE_TYPE_IS_NOT_SUPPORTED = "requested: {0} is not supported.";
    public static final String REQUESTED_ENTITY_IS_NOT_SUPPORTED = "requested: {0} is not supported.";
    public static final String REQUESTED_PROJECT_UUID_DOES_NOT_EXIST = "requested: {0} does not exist.";
}
