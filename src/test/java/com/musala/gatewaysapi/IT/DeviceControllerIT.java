package com.musala.gatewaysapi.IT;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.models.DeviceModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

import static com.musala.gatewaysapi.IT.TestRestTemplate.restCall;
import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.utils.DevicesTestUtil.*;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, ITBaseContextExtension.class})
@Slf4j
public class DeviceControllerIT {
    private final Gson gson = new Gson();

    @Test
    void getDevices_successfully_200(){
        final ResponseEntity<?> responseEntity = restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, DEVICE_VALID_UUID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(GET_DEVICE_RESPONSE_FOR_VALID_UUID, gson.toJson(responseEntity.getBody(), DeviceModel.class));
        assertEquals(MessageFormat.format(GET_DEL_DEVICE_DISCOVERABILITY, GATEWAY_VALID_UUID),
                responseEntity.getHeaders().getFirst("Location"));
    }
    @Test
    void getDevices_unAuthorized_fail_401(){
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), false, GATEWAY_VALID_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void getDevices_gateway_notFound_fail_404(){
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), true, NOT_FOUND_GATEWAY_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_GATEWAY_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void getDevices_device_notFound_fail_404(){
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, NOT_FOUND_DEVICE_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_DEVICE_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void getDevices_notValidGatewayUuid_badRequest_fail_400(){
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), true, NOT_VALID_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_UUID_ERROR_MESSAGE, "getDevice.gateway_uuid"));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void getDevices_notValidDeviceUuid_badRequest_fail_400(){
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, NOT_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_UUID_ERROR_MESSAGE, "getDevice.device_uuid"));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void createDevice_successfully_200(){
        Device createDevice = generateRandomDevice();
        DeviceModel requestBody = createDevice.getDeviceModelFromDevice();
        final ResponseEntity<?> responseEntity = restCall(CREATE_NEW_DEVICE, HttpMethod.POST, new HashMap<>(1),
                String.class, requestBody, true, GATEWAY_VALID_UUID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Device created successfully and added to requested gateway device list\"}" , responseEntity.getBody());
        assertEquals(MessageFormat.format(UPDATE_DEVICE_DISCOVERABILITY, GATEWAY_VALID_UUID, createDevice.getDevicesUuid()),
                responseEntity.getHeaders().getFirst("Location"));

        final ResponseEntity<?> rollBackChanges = restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                String.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, createDevice.getDevicesUuid());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void createDevice_unAuthorized_fail_401(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        try {
            restCall(CREATE_NEW_DEVICE, HttpMethod.POST, new HashMap<>(1),
                    String.class, requestBody, false, GATEWAY_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void createDevice_gateway_notFound_fail_404(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        try {
            restCall(CREATE_NEW_DEVICE, HttpMethod.POST, new HashMap<>(1),
                    String.class, requestBody, true, NOT_FOUND_GATEWAY_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_GATEWAY_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void createDevice_notValidGatewayUuid_badRequest_fail_400(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        try {
            restCall(CREATE_NEW_DEVICE, HttpMethod.POST, new HashMap<>(1),
                    String.class, requestBody, true, NOT_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_GATEWAY_UUID_ERROR_MESSAGE, "createDevice.gateway_uuid", NOT_VALID_UUID));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void updateDevice_successfully_200(){
        DeviceModel oldDevice = (DeviceModel) restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, DEVICE_VALID_UUID).getBody();
        DeviceModel newDevice = generateRandomDevice().getDeviceModelFromDevice();
        final ResponseEntity<?> responseEntity = restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                String.class, newDevice, true, GATEWAY_VALID_UUID, Objects.requireNonNull(oldDevice).getDevicesUuid());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Gateway's Device updated successfully\"}" , responseEntity.getBody());
        assertEquals(MessageFormat.format(UPDATE_DEVICE_DISCOVERABILITY, GATEWAY_VALID_UUID, newDevice.getDevicesUuid()),
                responseEntity.getHeaders().getFirst("Location"));
        DeviceModel updatedDevice = (DeviceModel)  restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, newDevice.getDevicesUuid()).getBody();
        assertEquals(newDevice.getDevicesName(), Objects.requireNonNull(updatedDevice).getDevicesName());

        final ResponseEntity<?> rollBackChanges = restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                String.class, oldDevice, true, GATEWAY_VALID_UUID, newDevice.getDevicesUuid());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void updateDevice_unAuthorized_fail_401(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        requestBody.setDevicesUuid(DEVICE_VALID_UUID);
        try {
            restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                    String.class, requestBody, false, GATEWAY_VALID_UUID, DEVICE_VALID_UUID);
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void updateDevice_gateway_notFound_fail_404(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        requestBody.setDevicesUuid(DEVICE_VALID_UUID);
        try {
            restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                    String.class, requestBody, true, NOT_FOUND_GATEWAY_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_GATEWAY_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void updateDevice_device_notFound_fail_404(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        requestBody.setDevicesUuid(DEVICE_VALID_UUID);
        try {
            restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                    String.class, requestBody, true, GATEWAY_VALID_UUID, NOT_FOUND_DEVICE_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_DEVICE_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void updateDevice_notValidGatewayUuid_badRequest_fail_400(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        requestBody.setDevicesUuid(DEVICE_VALID_UUID);
        try {
            restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                    String.class, requestBody, true, NOT_VALID_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_GATEWAY_UUID_ERROR_MESSAGE, "updateDevice.gateway_uuid", NOT_VALID_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void updateDevice_notValidDeviceUuid_badRequest_fail_400(){
        DeviceModel requestBody = generateRandomDevice().getDeviceModelFromDevice();
        requestBody.setDevicesUuid(DEVICE_VALID_UUID);
        try {
            restCall(UPDATE_GATEWAY_DEVICE, HttpMethod.PUT, new HashMap<>(1),
                    DeviceModel.class, requestBody, true, GATEWAY_VALID_UUID, NOT_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_UUID_ERROR_MESSAGE, "updateDevice.device_uuid"));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void deleteDevice_successfully_200(){
        DeviceModel deviceToDelete = (DeviceModel) restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, DEVICE_VALID_UUID).getBody();
        final ResponseEntity<?> responseEntity = restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                String.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, Objects.requireNonNull(deviceToDelete).getDevicesUuid());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Gateway's Device deleted successfully\"}" , responseEntity.getBody());
        assertEquals(MessageFormat.format(GET_DEL_DEVICE_DISCOVERABILITY, GATEWAY_VALID_UUID),
                responseEntity.getHeaders().getFirst("Location"));
        try {
            restCall(GET_GATEWAY_DEVICE, HttpMethod.GET, new HashMap<>(1),
                    DeviceModel.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, deviceToDelete.getDevicesUuid());
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, deviceToDelete.getDevicesUuid()));
        } catch (final Exception e) {
            fail();
        }
        final ResponseEntity<?> rollBackChanges = restCall(CREATE_NEW_DEVICE, HttpMethod.POST, new HashMap<>(1),
                String.class, deviceToDelete, true, GATEWAY_VALID_UUID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void deleteDevice_unAuthorized_fail_401(){
        try {
            restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                    String.class, new HashMap<>(1), false, GATEWAY_VALID_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void deleteDevice_gateway_notFound_fail_404(){
        try {
            restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                    String.class, new HashMap<>(1), true, NOT_FOUND_GATEWAY_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_GATEWAY_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void deleteDevice_device_notFound_fail_404(){
        try {
            restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                    String.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, NOT_FOUND_DEVICE_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, NOT_FOUND_DEVICE_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void deleteDevice_notValidGatewayUuid_badRequest_fail_400(){
        try {
            restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                    String.class, new HashMap<>(1), true, NOT_VALID_UUID, DEVICE_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_GATEWAY_UUID_ERROR_MESSAGE, "deleteDevice.gateway_uuid", NOT_VALID_UUID));
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void deleteDevice_notValidDeviceUuid_badRequest_fail_400(){
        try {
            restCall(DELETE_GATEWAY_DEVICE, HttpMethod.DELETE, new HashMap<>(1),
                    String.class, new HashMap<>(1), true, GATEWAY_VALID_UUID, NOT_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(e.getMessage(), MessageFormat.format(NOT_VALID_UUID_ERROR_MESSAGE, "deleteDevice.device_uuid"));
        } catch (final Exception e) {
            fail();
        }
    }
}
