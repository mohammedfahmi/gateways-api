package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.configuration.TraceRequestFilter;
import com.musala.gatewaysapi.configuration.security.SecurityConfiguration;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.services.GatewayService;
import com.musala.gatewaysapi.validations.ValidationUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static com.musala.gatewaysapi.constants.ApiMapping.API_ROOT;
import static com.musala.gatewaysapi.constants.ApiMapping.GET_ALL_GATEWAYS;
import static com.musala.gatewaysapi.utils.TestUtils.asJsonString;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.generateAbstractGatewayList;
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
    @MockBean
    private ValidationUtil validationUtil;

    @Test
    void getGatewayPage_ValidPage() throws Exception {
        final List<AbstractGateway> gatewaysList = generateAbstractGatewayList();
        final String response = asJsonString(gatewaysList);
        final MultiValueMap<String, String> requestParams = prepareQueryParams("page", "4", "size", "10");
        when(this.gatewayService.getAllGatewaysCount()).thenReturn(60);
        when(this.gatewayService.getGateways(any(), any(), any())).thenReturn(gatewaysList);
        doNothing().when(this.validationUtil).handleBindResult(any());
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
        doNothing().when(this.validationUtil).handleBindResult(any());
        ResultActions result = mockMvc.perform(
                get(API_ROOT + GET_ALL_GATEWAYS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(requestParams)
                        .with(httpBasic(securityConfiguration.getName(), securityConfiguration.getPassword())));
        result.andExpect(status().is4xxClientError())
                .andExpect(content().string(response));
    }
}