package com.musala.gatewaysapi.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.hateoas.listeners.GetDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.PaginationDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.SaveDiscoverabilityListener;
import com.musala.gatewaysapi.repositories.DeviceRepository;
import com.musala.gatewaysapi.utils.LocalDateTimeAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.musala.gatewaysapi.constants.Constants.DEVICE_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.utils.DevicesTestUtil.*;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.generateGateway;
import static com.musala.gatewaysapi.utils.TestUtils.getUriBuilder;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

    private HttpServletResponse mockResponse;
    private String requestedDeviceUuid;
    private String requestedGatewayUuid;
    private UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void beforeEach() {
        mockResponse = new MockHttpServletResponse();
        requestedDeviceUuid = UUID.randomUUID().toString();
        requestedGatewayUuid = UUID.randomUUID().toString();
        uriComponentsBuilder = getUriBuilder();
    }


    @Test
    void getDevice_Successfully() {
        String expectedReadLocationHeader = MessageFormat.format(GET_DEL_DEVICE_DISCOVERABILITY, requestedGatewayUuid);
        Device expectedDevice = generateRandomDevice();
        expectedDevice.setDevicesUuid(requestedDeviceUuid);
        Gateway expectedGateway = expectedDevice.getGateway();
        expectedGateway.setGatewayUuid(requestedGatewayUuid);
        expectedGateway.setDevices(Collections.singletonList(expectedDevice));

        when(gatewayService.getGateway(any())).thenReturn(expectedGateway);

        Device actualDevice = deviceService.getDevice(uriComponentsBuilder, mockResponse, requestedGatewayUuid, requestedDeviceUuid);
        assertEquals(expectedDevice, actualDevice);
        assertEquals(mockResponse.getHeader("Location"), expectedReadLocationHeader);
    }
    @Test
    void getDevice_NotFoundGateway_fail() {
        when(gatewayService.getGateway(any()))
                .thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid)));
        try {
            deviceService.getDevice(uriComponentsBuilder, mockResponse, requestedGatewayUuid, requestedDeviceUuid);
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }
    @SuppressWarnings("unchecked")
    @Test
    void getDevice_NotFoundDevice_fail() {
        Gateway expectedGateway = generateGateway();
        expectedGateway.setGatewayUuid(requestedGatewayUuid);
        expectedGateway.setDevices(Collections.EMPTY_LIST);

        when(gatewayService.getGateway(any())).thenReturn(expectedGateway);

        try {
            deviceService.getDevice(uriComponentsBuilder, mockResponse, requestedGatewayUuid, requestedDeviceUuid);
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, requestedDeviceUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }


    @Test
    void createDevice_Successfully() {
        Device expectedDevice = generateRandomDevice();
        expectedDevice.setDevicesUuid(requestedDeviceUuid);
        Gateway expectedGateway = expectedDevice.getGateway();
        expectedGateway.setGatewayUuid(requestedGatewayUuid);
        expectedGateway.setDevices(Collections.singletonList(expectedDevice));
        String expectedSaveDeviceLocationHeader =
                MessageFormat.format(UPDATE_DEVICE_DISCOVERABILITY, requestedGatewayUuid, expectedDevice.getDevicesUuid());

        when(gatewayService.getGateway(any())).thenReturn(expectedGateway);
        when(deviceRepository.saveAndFlush(any())).thenReturn(expectedDevice);

        Device actualDevice = deviceService.createDevice(uriComponentsBuilder, mockResponse, requestedGatewayUuid, expectedDevice);
        assertEquals(expectedDevice, actualDevice);
        assertEquals(mockResponse.getHeader("Location"), expectedSaveDeviceLocationHeader);
    }
    @Test
    void createDevice_NotFoundGateway_fail() {
        when(gatewayService.getGateway(any()))
                .thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid)));
        try {
            deviceService.createDevice(uriComponentsBuilder, mockResponse, requestedGatewayUuid, generateRandomDevice());
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }


    @Test
    void updateDevice_Successfully() {
        Device oldDevice = generateRandomDevice();
        oldDevice.setDevicesUuid(requestedDeviceUuid);
        Gateway oldGateway = oldDevice.getGateway();
        oldGateway.setGatewayUuid(requestedGatewayUuid);
        oldGateway.setDevices(Collections.singletonList(oldDevice));
        Device newDevice = oldDevice.toBuilder().devicesName("device-updated").build();
        String expectedSaveDeviceLocationHeader =
                MessageFormat.format(UPDATE_DEVICE_DISCOVERABILITY, requestedGatewayUuid, newDevice.getDevicesUuid());

        when(gatewayService.getGateway(any())).thenReturn(oldGateway);
        when(deviceRepository.saveAndFlush(any())).thenReturn(newDevice);

        Device actualDevice = deviceService.updateDevice(
                uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid, newDevice);
        assertEquals(newDevice, actualDevice);
        assertEquals(mockResponse.getHeader("Location"), expectedSaveDeviceLocationHeader);
    }
    @Test
    void updateDevice_NotFoundGateway_fail() {
        when(gatewayService.getGateway(any()))
                .thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid)));
        try {
            deviceService.updateDevice(
                    uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid, any());
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }
    @Test
    void updateDevice_NotFoundDevice_fail() {
        Gateway gateway = generateGateway();
        when(gatewayService.getGateway(any())).thenReturn(gateway);
        try {
            deviceService.updateDevice(
                    uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid, any());
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, requestedDeviceUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }


    @Test
    void deleteDevice_Successfully() {
        String expectedDeleteLocationHeader = MessageFormat.format(GET_DEL_DEVICE_DISCOVERABILITY, requestedGatewayUuid);
        Device expectedDevice = generateRandomDevice();
        expectedDevice.setDevicesUuid(requestedDeviceUuid);
        Gateway expectedGateway = expectedDevice.getGateway();
        expectedGateway.setGatewayUuid(requestedGatewayUuid);
        expectedGateway.setDevices(Collections.singletonList(expectedDevice));

        when(gatewayService.getGateway(any())).thenReturn(expectedGateway);
        doNothing().when(deviceRepository).delete(any());

        deviceService.deleteDevice(uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid);
        assertEquals(mockResponse.getHeader("Location"), expectedDeleteLocationHeader);
    }
    @Test
    void deleteDevice_NotFoundGateway_fail() {
        when(gatewayService.getGateway(any()))
                .thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid)));
        try {
            deviceService.deleteDevice(uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid);
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, requestedGatewayUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }
    @Test
    void deleteDevice_NotFoundDevice_fail() {
        Gateway gateway = generateGateway();
        when(gatewayService.getGateway(any())).thenReturn(gateway);
        try {
            deviceService.deleteDevice(uriComponentsBuilder, mockResponse, requestedDeviceUuid, requestedGatewayUuid);
            fail();
        } catch (EntityNotFoundException exception) {
            assertEquals(MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, requestedDeviceUuid), exception.getMessage());
        } catch (Exception exception) {
            fail();
        }
    }
}