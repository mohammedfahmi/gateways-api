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
    public static final String DEVICE_UUID_NOT_FOUND_ERROR_MESSAGE = "Requested Device with uuid {0} is not Found";

    public static Device generateDevice() {
        Random random = new Random();
        Gateway gateway = generateGateway();
        return Device.builder()
                .id(random.nextLong())
                .devicesUuid(UUID.randomUUID().toString())
                .devicesName("Device-"+UUID.randomUUID())
                .deviceCreationDate(LocalDateTime.now(UTC))
                .vendor("texas tech")
                .status(true)
                .gateway(gateway).build();
    }
}
