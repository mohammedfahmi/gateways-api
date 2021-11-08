package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.hateoas.events.GetResultEvent;
import com.musala.gatewaysapi.hateoas.events.SaveResultEvent;
import com.musala.gatewaysapi.hateoas.events.PaginatedResultsEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static com.musala.gatewaysapi.constants.ApiMapping.*;

@AllArgsConstructor
@Service
public class DiscoveryService {

    private ApplicationEventPublisher eventPublisher;

    public void triggerGetGatewayPageDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response,
                                               Integer page, Integer totalPages, Integer size) {
        uriBuilder.path(API_ROOT+GET_ALL_GATEWAYS);
        eventPublisher.publishEvent(new PaginatedResultsEvent(this, uriBuilder, response, page, totalPages, size));
    }
    public void triggerGetGatewayEntityDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, Integer totalGatewayCount) {
        uriBuilder.path(API_ROOT+GET_ALL_GATEWAYS);
        eventPublisher.publishEvent(new GetResultEvent(this, uriBuilder, response, totalGatewayCount));
    }
    public void triggerSaveGatewayDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUuid) {
        uriBuilder.path(API_ROOT+GET_GATEWAY_DETAILS_WITH_ITS_DEVICES);
        uriBuilder.uriVariables(Collections.singletonMap("gateway_uuid", gatewayUuid));
        eventPublisher.publishEvent(new SaveResultEvent(this, uriBuilder, response));
    }
    public void triggerGetDeviceEntityDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUuid) {
        triggerSaveGatewayDiscovery(uriBuilder, response, gatewayUuid);
    }
    public void triggerDeleteDeviceEntityDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUuid) {
        triggerSaveGatewayDiscovery(uriBuilder, response, gatewayUuid);
    }
    public void triggerSaveDeviceDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String deviceUuid, String gatewayUuid) {
        uriBuilder.path(API_ROOT+GET_GATEWAY_DEVICE);
        uriBuilder.uriVariables(Collections.singletonMap("gateway_uuid", gatewayUuid));
        uriBuilder.uriVariables(Collections.singletonMap("device_uuid", deviceUuid));
        eventPublisher.publishEvent(new SaveResultEvent(this, uriBuilder, response));
    }
}
