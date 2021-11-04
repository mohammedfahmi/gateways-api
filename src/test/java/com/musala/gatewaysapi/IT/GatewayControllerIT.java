package com.musala.gatewaysapi.IT;

import com.google.gson.Gson;
import com.musala.gatewaysapi.models.AbstractGateway;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

import static com.musala.gatewaysapi.IT.TestRestTemplate.*;
import static com.musala.gatewaysapi.constants.ApiMapping.GET_ALL_GATEWAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Durations.ONE_MINUTE;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Slf4j
public class GatewayControllerIT {
    private final Gson gson = new Gson();

    @BeforeAll
    static void beforeAll() {
        Awaitility.setDefaultTimeout(ONE_MINUTE);
        with().pollInterval(fibonacci().with().unit(SECONDS).and().offset(3)).await().until(() -> healthCheckCall());
        with().pollInterval(fibonacci().with().unit(SECONDS).and().offset(3)).await().until( () ->  testCall());
    }

    @Test
    void getGateways_page4_size10_and_response200() {
        final ResponseEntity<?> responseEntity = restCall(GET_ALL_GATEWAYS, HttpMethod.GET,
                prepareQueryParams("page", "4", "size", "10"), AbstractGateway[].class, new HashMap<>(1));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
