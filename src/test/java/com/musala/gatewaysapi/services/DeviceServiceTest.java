package com.musala.gatewaysapi.services;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.hateoas.listeners.GetDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.PaginationDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.SaveDiscoverabilityListener;
import com.musala.gatewaysapi.repositories.DeviceRepository;
import com.musala.gatewaysapi.validations.DeviceModelValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.UUID;

import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.utils.DevicesTestUtil.DEVICE_UUID_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.utils.DevicesTestUtil.generateDevice;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.VALID_UUID_WITH_NO_GATEWAY;
import static com.musala.gatewaysapi.utils.TestUtils.getUriBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { DeviceService.class, GatewayService.class, DeviceRepository.class, Gateway.class, Device.class,
        PaginationDiscoverabilityListener.class , DiscoveryService.class,  ApplicationEventPublisher.class,
        SaveDiscoverabilityListener.class, GetDiscoverabilityListener.class})
class DeviceServiceTest {
    @Autowired
    private DeviceService deviceService;
    @MockBean
    private DeviceRepository deviceRepository;
    @MockBean
    private GatewayService gatewayService;

    private final Gson gson = new Gson();

    @Test
    void getDeviceByUuid_Successfully() {
        Device expectedDevice = generateDevice();
        when(deviceRepository.findDeviceByDevicesUuidEquals(any())).thenReturn(expectedDevice);
        Device actualDevice = deviceService.getDeviceByUuid(expectedDevice.getDevicesUuid());
        assertEquals(gson.toJson(expectedDevice), gson.toJson(actualDevice));
    }
    @Test
    void getDeviceByUuid_fail_deviceNotFound() {
        String requestedDeviceUuid = UUID.randomUUID().toString();
        when(deviceRepository.findDeviceByDevicesUuidEquals(any())).thenReturn(null);
        try{
            deviceService.getDeviceByUuid(requestedDeviceUuid);
            fail();
        } catch (EntityNotFoundException e) {
            assertEquals(MessageFormat.format(DEVICE_UUID_NOT_FOUND_ERROR_MESSAGE, requestedDeviceUuid), e.getMessage());
        }
    }

    @Test
    void getDevice_Successfully() {
//        Device expectedDevice = generateDevice();
//        HttpServletResponse mockResponse = new MockHttpServletResponse();
//        Device device = deviceService.getDevice(getUriBuilder(), mockResponse, )
    }

    @Test
    void createDevice_Successfully() {
    }

    @Test
    void updateDevice_Successfully() {
    }

    @Test
    void deleteDevice_Successfully() {
    }
}