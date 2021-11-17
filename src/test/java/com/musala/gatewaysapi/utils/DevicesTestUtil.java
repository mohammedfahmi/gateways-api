package com.musala.gatewaysapi.utils;

import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.entities.Gateway;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static com.musala.gatewaysapi.utils.GatewaysTestUtils.generateGateway;
import static java.time.ZoneOffset.UTC;

@UtilityClass
public class DevicesTestUtil {
    public static final String DEVICE_VALID_UUID = "a3e3befe-3c2d-11ec-a662-0242ac160003";
    public static final String NOT_FOUND_DEVICE_UUID = "6f28ec80-8be9-4a4b-b00a-440fb5b828c8";
    public static final String NOT_FOUND_GATEWAY_UUID = "0394dd96-da11-41da-b510-14bde81b7cd0";
    public static final String GET_DEVICE_RESPONSE_FOR_VALID_UUID = "{\"devicesUuid\":\"a3e3befe-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"device-a3e3bf16-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":\"2021-11-02T22:38:46\",\"status\":true}";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Requested {0} with uuid {1} is not Found";
    public static final String DEVICE_NOT_FOUND_ERROR_MESSAGE= "404 : ['{'\"error\":\"Requested Device with uuid {0} is not Found\"'}']";
    public static final String NOT_FOUND_RESPONSE = "'{'\"error\":\"Requested Device with uuid {0} is not Found\"'}'";
    public static final String NOT_Valid_RESPONSE = "'{'\"error\":\"Validation failed, failed to bind value {0} to field {1}.\"'}'";
    public static final String NOT_VALID_UUID_ERROR_MESSAGE = "400 : ['{'\"error\":\"Validation failed, {0}: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"'}']";
    public static final String GET_DEL_DEVICE_DISCOVERABILITY = "http://localhost:8080/gateways-api/api/rest/gateway/{0}";
    public static final String UPDATE_DEVICE_DISCOVERABILITY = "http://localhost:8080/gateways-api/api/rest/{0}/device/{1}";


    public static Device generateRandomDevice() {
        Random random = new Random();
        Gateway gateway = generateGateway();
        return Device.builder()
                .id(random.nextLong())
                .devicesUuid(UUID.randomUUID().toString())
                .devicesName("Device-"+UUID.randomUUID())
                .deviceCreationDate(LocalDateTime.now(UTC).minusDays(1))
                .vendor("texas tech")
                .status(true)
                .gateway(gateway).build();
    }
    public static Device generateStaticDevice() {
        return Device.builder()
                .devicesUuid("410c94f5-22e3-46de-bd04-8b137bb29362")
                .devicesName("device-b4ddb985-d25d-4f72-a1ca-326aac959a7b")
                .deviceCreationDate(LocalDateTime.parse("2021-11-11T23:45:54"))
                .vendor("texas tech")
                .status(true).build();
    }
}
