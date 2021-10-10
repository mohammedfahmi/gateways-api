package com.musala.gatewaysapi.constants;

import lombok.experimental.UtilityClass;

/**
 * Constant class to document the api endpoints.
 * <p>
 * This constants class should be used to reference the endpoints in controllers of the rest
 * interface as well as by all clients, because this allows the endpoint to only be updated once if
 * it needs to change.
 */
@UtilityClass
public final class ApiMapping {
    public static final String API_ROOT = "/api/rest";
    public static final String GET_ALL_GATEWAYS = "/gateways";
    public static final String CREATE_NEW_GATEWAY = "/gateway";
    public static final String GET_GATEWAY_DETAILS_WITH_ITS_DEVICES = "/gateway/{gateway_uuid}";
    public static final String UPDATE_GATEWAY_DETAILS = "/gateway/{gateway_uuid}";
    public static final String CREATE_NEW_DEVICE = "/{gateway_uuid}/device";
    public static final String UPDATE_GATEWAY_DEVICE = "/{gateway_uuid}/device/{device_uuid}";
    public static final String DELETE_GATEWAY_DEVICE = "/{gateway_uuid}/device/{device_uuid}";
}
