package com.musala.gatewaysapi.controllers;

import com.google.gson.Gson;
import com.musala.gatewaysapi.configuration.TraceRequestFilter;
import com.musala.gatewaysapi.configuration.security.SecurityConfiguration;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.services.GatewayService;
import com.musala.gatewaysapi.validations.AbstractGatewayValidator;
import com.musala.gatewaysapi.validations.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.*;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.*;
import static com.musala.gatewaysapi.utils.TestUtils.asJsonString;
import static com.musala.gatewaysapi.utils.TestUtils.prepareQueryParams;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = GatewayController.class,
        excludeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = TraceRequestFilter.class)
)
@Slf4j
class GatewayControllerTest {
    @Autowired
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GatewayService gatewayService;

    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public ValidationUtil validationUtil() {
            return new ValidationUtil();
        }
        @Bean
        public AbstractGatewayValidator abstractGatewayValidator() {
            return new AbstractGatewayValidator();
        }
    }
    @Test
    void getGatewayPage_ValidPage() throws Exception {
        final List<AbstractGateway> gatewaysList = generateAbstractGatewayList();
        final String response = asJsonString(gatewaysList);
        final MultiValueMap<String, String> requestParams = prepareQueryParams("page", "4", "size", "10");
        when(this.gatewayService.getAllGatewaysCount()).thenReturn(60);
        when(this.gatewayService.getGateways(any(), any(), any())).thenReturn(gatewaysList);
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_ALL_GATEWAYS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(requestParams)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }

    @Test
    void getGatewayPage_NotValidPage() throws Exception {
        final String response = "{\"error\":\"Validation failed, getGatewayPage.gatewayPaginationRequest: Requested page 4 is not available.\"}";
        final MultiValueMap<String, String> requestParams = prepareQueryParams("page", "4","size", "7");
        when(this.gatewayService.getAllGatewaysCount()).thenReturn(28);
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_ALL_GATEWAYS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(requestParams)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void getGateway_ValidGateway() throws Exception {
        final Gateway gateway = generateGateway();
        final String response = asJsonString(gateway.getGatewayModelFromGateway());

        when(this.gatewayService.getGateway(any())).thenReturn(gateway);
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, gateway.getGatewayUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    void getGateway_NotValidGateway() throws Exception {
        final String response = "{\"error\":\"Requested Gateway with uuid c5aab27d-92ab-4858-bd6e-ebfda0fe48d2 is not Found\"}";
        when(this.gatewayService.getGateway(any())).thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, VALID_UUID_WITH_NO_GATEWAY)));
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, VALID_UUID_WITH_NO_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(response));
    }
    @Test
    void getGateway_NotValidUuid() throws Exception {
        final String response = NOT_VALID_GATEWAY_UUID_MESSAGE;
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, NOT_VALID_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_ValidRequest() throws Exception {
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(gateway.getAbstractGatewayFromGateway());
        final String response = "{\"message\":\"Gateway created successfully\"}";
        when(this.gatewayService.saveGateway(any(), any(), any())).thenReturn(gateway);
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Wrong_Format() throws Exception {
        final String response = "{\"error\":\"Failed to parse the request parameters as they are in wrong format.\"}";
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Null_GatewayUuid() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value null to field gatewayUuid.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayName(gateway.getGatewayName())
                .gatewayIpv4(gateway.getGatewayIpv4()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Null_GatewayName() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value null to field gatewayName.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayIpv4(gateway.getGatewayIpv4()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Null_GatewayIpv4() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value null to field gatewayIpv4.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayName(gateway.getGatewayName()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }

    @Test
    void createGateway_UnValidRequest_Empty_GatewayUuid() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value  to field gatewayUuid.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayUuid("")
                .gatewayName(gateway.getGatewayName())
                .gatewayIpv4(gateway.getGatewayIpv4()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Empty_GatewayName() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value  to field gatewayName.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayName("")
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayIpv4(gateway.getGatewayIpv4()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_Empty_GatewayIpv4() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value  to field gatewayIpv4.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayIpv4("")
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayName(gateway.getGatewayName()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_NotValid_GatewayUuid() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value q3c23161-3c2d-11ec-a662-0242ac160003 to field gatewayUuid.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayUuid(NOT_VALID_UUID)
                .gatewayName(gateway.getGatewayName())
                .gatewayIpv4(gateway.getGatewayIpv4()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void createGateway_UnValidRequest_NotValid_GatewayIpv4() throws Exception {
        final String response = "{\"error\":\"Validation failed, failed to bind value 1234.14354.2343245.2344 to field gatewayIpv4.\"}";
        final Gateway gateway = generateGateway();
        final String requestBody = asJsonString(AbstractGateway.builder()
                .gatewayIpv4("1234.14354.2343245.2344")
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayName(gateway.getGatewayName()).build());
        ResultActions result = mockMvc.perform(
                post(API_ROOT + CREATE_NEW_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
    @Test
    void updateGateway_successfully() throws Exception {
        Gateway gateway = generateGateway();
        final String response = "{\"message\":\"Gateway Updated successfully\"}";
        when(this.gatewayService.updateGateway(any(), any(), any(), any())).thenReturn(gateway);
        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DETAILS, VALID_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(gateway))
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isOk())
                .andExpect(content().string(response));
    }
    @Test
    void updateGateway_NotFoundGateway() throws Exception {
        Gateway gateway = generateGateway();
        final String response = "{\"error\":\"Requested Gateway with uuid c5aab27d-92ab-4858-bd6e-ebfda0fe48d2 is not Found\"}";
        when(this.gatewayService.updateGateway(any(), any(), any(), any())).thenThrow(new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, VALID_UUID_WITH_NO_GATEWAY)));
        ResultActions result = mockMvc.perform(
                put(API_ROOT + UPDATE_GATEWAY_DETAILS, VALID_UUID_WITH_NO_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(gateway))
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().isNotFound())
                .andExpect(content().string(response));
    }
}