package com.musala.gatewaysapi.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musala.gatewaysapi.configuration.TraceRequestFilter;
import com.musala.gatewaysapi.configuration.security.SecurityConfiguration;
import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.services.DeviceService;
import com.musala.gatewaysapi.utils.LocalDateTimeAdapter;
import com.musala.gatewaysapi.validations.DeviceModelValidator;
import com.musala.gatewaysapi.validations.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.time.LocalDateTime;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.utils.DevicesTestUtil.*;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.NOT_FOUND_RESPONSE;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.NOT_VALID_UUID_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = DeviceController.class,
        excludeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = TraceRequestFilter.class)
)
@Slf4j
public class DeviceControllerTest {
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DeviceService deviceService;

    @SuppressWarnings("InstantiationOfUtilityClass")
    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public ValidationUtil validationUtil() {
            return new ValidationUtil();
        }
        @Bean
        public DeviceModelValidator deviceModelValidator() {
            return new DeviceModelValidator();
        }
    }

    private Gson gson;

    @BeforeEach
    void beforeEach() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }


    @Test
    void createDevice_ValidRequest_200() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());

        when(this.deviceService.createDevice(any(), any(), any(), any())).thenReturn(device);
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"Device created successfully and added to requested gateway device list\"}"));
    }
    @Test
    void createDevice_gateway_notFound_fail_404() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String notFoundErrorMessage = MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Gateway", device.getGateway().getGatewayUuid());
        final String expectedResponse = MessageFormat.format(NOT_FOUND_RESPONSE, "Gateway", device.getGateway().getGatewayUuid());

        when(this.deviceService.createDevice(any(), any(), any(), any())).thenThrow(new EntityNotFoundException(notFoundErrorMessage));
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_gateway_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "createDevice.gateway_uuid", unValidUuid);
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, unValidUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_nullUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDevicesUuid(null);
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "devicesUuid");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_emptyUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDevicesUuid("");
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "devicesUuid");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_notValidUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDevicesUuid("not_uuid");
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "not_uuid", "devicesUuid");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_nullName_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDevicesName(null);
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "devicesName");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_emptyName_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDevicesName("");
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "devicesName");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_nullValue_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setVendor(null);
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "vendor");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_emptyValue_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setVendor("");
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "", "vendor");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_nullStatus_succeed_200() throws Exception {
        final Device device = generateRandomDevice();
        device.setStatus(null);
        final String body = gson.toJson(device.toModel());
        final String expectedResponse =  "{\"message\":\"Device created successfully and added to requested gateway device list\"}";
        when(this.deviceService.createDevice(any(), any(), any(), any())).thenReturn(device);
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_notValidStatus_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setStatus(null);
        final String body = "{\"devicesUuid\": \"410c94f5-22e3-46de-bd04-8b137bb29362\", \"devicesName\": \"Device-b4ddb985-d25d-4f72-a1ca-326aac959a7b\", " +
                "\"vendor\": \"texas tech\", \"deviceCreationDate\": \"2021-11-11T23:45:54\", \"status\": \"not_boolean\"}";
        final String expectedResponse = "{\"error\":\"Failed to parse the request parameters as they are in wrong format.\"}";

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void createDevice_device_nullCreationDate_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        device.setDeviceCreationDate(null);
        final String body = gson.toJson(device.toModel());
        final String expectedResponse = MessageFormat.format(NOT_Valid_RESPONSE, "-999999999-01-01T00:00:00", "deviceCreationDate");

        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_DEVICE, device.getGateway().getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }


    @Test
    void getDevice_ValidRequest_200() throws Exception {
        final Device device = generateRandomDevice();
        final String response = gson.toJson(device.toModel());

        when(this.deviceService.getDevice(any(), any(), any(), any())).thenReturn(device);
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    void getDevice_gateway_notFound_fail_404() throws Exception {
        final Device device = generateRandomDevice();
        final String notFoundErrorMessage = MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Gateway", device.getGateway().getGatewayUuid());
        final String expectedResponse = MessageFormat.format(NOT_FOUND_RESPONSE, "Gateway", device.getGateway().getGatewayUuid());

        when(this.deviceService.getDevice(any(), any(), any(), any())).thenThrow(new EntityNotFoundException(notFoundErrorMessage));
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void getDevice_device_notFound_fail_404() throws Exception {
        final Device device = generateRandomDevice();
        final String notFoundErrorMessage = MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Device", device.getGateway().getGatewayUuid());
        final String expectedResponse = MessageFormat.format(NOT_FOUND_RESPONSE, "Device", device.getGateway().getGatewayUuid());

        when(this.deviceService.getDevice(any(), any(), any(), any())).thenThrow(new EntityNotFoundException(notFoundErrorMessage));
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void getDevice_gateway_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "getDevice.gateway_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DEVICE, unValidUuid, device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void getDevice_device_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "getDevice.device_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), unValidUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }


    @Test
    void updateDevice_ValidRequest_200() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String expectedResponse =  "{\"message\":\"Gateway's Device updated successfully\"}";

        when(this.deviceService.updateDevice(any(), any(), any(), any(), any())).thenReturn(device);
        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void updateDevice_resource_notFound_fail_404() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String notFoundErrorMessage = MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Gateway", device.getGateway().getGatewayUuid());
        final String expectedResponse = MessageFormat.format(NOT_FOUND_RESPONSE, "Gateway", device.getGateway().getGatewayUuid());

        when(this.deviceService.updateDevice(any(), any(), any(), any(), any())).thenThrow(new EntityNotFoundException(notFoundErrorMessage));
        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void updateDevice_gateway_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "updateDevice.gateway_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DEVICE, unValidUuid, device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void updateDevice_device_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String body = gson.toJson(device.toModel());
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "updateDevice.device_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), unValidUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }


    @Test
    void deleteDevice_ValidRequest_200() throws Exception {
        final Device device = generateRandomDevice();
        final String expectedResponse =  "{\"message\":\"Gateway's Device deleted successfully\"}";

        doNothing().when(this.deviceService).deleteDevice(any(), any(), any(), any());
        ResultActions result = mockMvc.perform(
                delete(API_ROOT + DELETE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void deleteDevice_resource_notFound_fail_404() throws Exception {
        final Device device = generateRandomDevice();
        final String notFoundErrorMessage = MessageFormat.format(NOT_FOUND_ERROR_MESSAGE, "Gateway", device.getGateway().getGatewayUuid());
        final String expectedResponse = MessageFormat.format(NOT_FOUND_RESPONSE, "Gateway", device.getGateway().getGatewayUuid());

        doThrow(new EntityNotFoundException(notFoundErrorMessage)).when(this.deviceService).deleteDevice(any(), any(), any(), any());
        ResultActions result = mockMvc.perform(
                delete(API_ROOT + DELETE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void deleteDevice_gateway_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "deleteDevice.gateway_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                delete(API_ROOT + DELETE_GATEWAY_DEVICE, unValidUuid, device.getDevicesUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
    @Test
    void deleteDevice_device_badUuid_fail_400() throws Exception {
        final Device device = generateRandomDevice();
        final String unValidUuid = "not UUID";
        final String expectedResponse = MessageFormat.format(NOT_VALID_UUID_ERROR, "deleteDevice.device_uuid", unValidUuid);

        ResultActions result = mockMvc.perform(
                delete(API_ROOT + DELETE_GATEWAY_DEVICE, device.getGateway().getGatewayUuid(), unValidUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }
}
