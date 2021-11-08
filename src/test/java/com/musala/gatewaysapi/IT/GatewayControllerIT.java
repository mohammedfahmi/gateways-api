package com.musala.gatewaysapi.IT;

import com.google.gson.Gson;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayModel;
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
import java.util.UUID;

import static com.musala.gatewaysapi.IT.TestRestTemplate.*;
import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, ITBaseContextExtension.class})
@Slf4j
public class GatewayControllerIT {
    private final Gson gson = new Gson();

    @Test
    void getGateways_page4_size10_and_response200() {
        final ResponseEntity<?> responseEntity = restCall(GET_ALL_GATEWAYS, HttpMethod.GET,
                prepareParams("page", "4", "size", "10"), AbstractGateway[].class, new HashMap<>(1), true);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(JSON_ABSTRACT_GATEWAY_PAGE_4_SIZE_10, gson.toJson(responseEntity.getBody(), AbstractGateway[].class));
        assertEquals(getPaginationLinks(), responseEntity.getHeaders().getFirst("Link"));
    }

    @Test
    void getGateways_Parameter_Type_Mismatch_response_400() {
        try {
            restCall(GET_ALL_GATEWAYS, HttpMethod.GET,
                    prepareParams("page", "four", "size", "ten"), AbstractGateway[].class, new HashMap<>(1), true);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(JSON_GATEWAYS_PARAM_TYPE_MISMATCH, gson.toJson(e.getMessage(), String.class));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void getGateways_Fail_Authentication_response_401() {
        try {
            restCall(GET_ALL_GATEWAYS, HttpMethod.GET,
                    prepareParams("page", "4", "size", "10"), AbstractGateway[].class, new HashMap<>(1), false);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertTrue(Objects.requireNonNull(e.getMessage()).contains("\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/gateways\"}"));
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void getGateway_with_Valid_UUID_200() {
        final ResponseEntity<?> responseEntity = restCall(GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, HttpMethod.GET, new HashMap<>(1),
                GatewayModel.class, new HashMap<>(1), true, VALID_UUID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(GATEWAY_RESPONSE_FOR_VALID_UUID, gson.toJson(responseEntity.getBody(), GatewayModel.class));
    }

    @Test
    void getGateway_With_Valid_UUID_Not_Found_Gateway_404() {
        UUID gatewayUuid = UUID.randomUUID();
        try {
            restCall(GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, HttpMethod.GET, new HashMap<>(1),
                GatewayModel.class, new HashMap<>(1), true, gatewayUuid.toString());
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, gatewayUuid), e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void getGateway_with_Not_Valid_UUID_400() {
        try {
            restCall(GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, HttpMethod.GET, new HashMap<>(1),
                    GatewayModel.class, new HashMap<>(1), true, NOT_VALID_UUID);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(NOT_VALID_GATEWAY_UUID_ERROR_MESSAGE, e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }

    @Test
    void createGateway_With_Valid_Gateway_200() {
        final ResponseEntity<?> responseEntity = restCall(CREATE_NEW_GATEWAY, HttpMethod.POST, new HashMap<>(1),
                String.class, prepareParams("gatewayUuid", UUID.randomUUID().toString(),
                "gatewayName", "gateway-" + UUID.randomUUID(), "gatewayIpv4", "70.22.2.49"), true);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("{\"message\":\"Gateway created successfully\"}", responseEntity.getBody());
    }
    @Test
    void createGateway_With_Null_Gateway_400() {
        try {
            restCall(CREATE_NEW_GATEWAY, HttpMethod.POST, new HashMap<>(1),
                    String.class, new HashMap<>(1), true);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(NULL_GATEWAY_FOR_CREATION_ERROR_MESSAGE, e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void createGateway_With_Empty_Gateway_400() {
        try {
            restCall(CREATE_NEW_GATEWAY, HttpMethod.POST, new HashMap<>(1),
                    String.class, prepareParams("gatewayUuid", "",
                            "gatewayName", "", "gatewayIpv4", ""), true);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(EMPTY_GATEWAY_FOR_CREATION_ERROR_MESSAGE, e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }
    @Test
    void createGateway_With_UnValid_UUID_And_IPv4_400() {
        try {
            restCall(CREATE_NEW_GATEWAY, HttpMethod.POST, new HashMap<>(1),
                    String.class, prepareParams("gatewayUuid", NOT_VALID_UUID,
                            "gatewayName", "gateway-"+NOT_VALID_UUID, "gatewayIpv4", "1234.14354.2343245.2344"), true);
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals(NOT_VALID_UUID_AND_IPV4_FOR_GATEWAY_CREATION_ERROR_MESSAGE, e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }


    @Test
    void updateGateway_with_Valid_UUID_And_Body_200() {
        String newGatewayUuid = UUID.randomUUID().toString();
        final ResponseEntity<?> responseEntity = restCall(UPDATE_GATEWAY_DETAILS, HttpMethod.PUT, new HashMap<>(1),
                String.class, prepareParams("gatewayUuid", newGatewayUuid,
                        "gatewayName", "gateway-" + newGatewayUuid, "gatewayIpv4", "71.22.2.50"), true, VALID_UUID);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(GATEWAY_UPDATED_SUCCESSFULLY, responseEntity.getBody());
        @SuppressWarnings("unused")
        final ResponseEntity<?> rollBackUpdateResponseEntity = restCall(UPDATE_GATEWAY_DETAILS, HttpMethod.PUT, new HashMap<>(1),
                String.class, prepareParams("gatewayUuid", VALID_UUID,
                        "gatewayName", "gateway-a3c2316a-3c2d-11ec-a662-0242ac160003", "gatewayIpv4", "70.22.2.45"), true, newGatewayUuid);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(GATEWAY_UPDATED_SUCCESSFULLY, responseEntity.getBody());
    }

    @Test
    void updateGateway_With_Valid_UUID_Not_Found_Gateway_404() {
        String newGatewayUuid = UUID.randomUUID().toString();
        try {
            restCall(UPDATE_GATEWAY_DETAILS, HttpMethod.PUT, new HashMap<>(1),
                    String.class, prepareParams("gatewayUuid", newGatewayUuid,
                            "gatewayName", "gateway-" + newGatewayUuid, "gatewayIpv4", "71.22.2.50"), true, newGatewayUuid  );
            fail();
        } catch (final HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, newGatewayUuid), e.getMessage());
        } catch (final Exception e) {
            fail();
        }
    }

}
