package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.hateoas.events.CreationResultEvent;
import com.musala.gatewaysapi.hateoas.events.PaginatedResultsEvent;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.repositories.GatewayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;


@Slf4j
@Service
@AllArgsConstructor
public class GatewayService {

    private GatewayRepository gatewayRepository;
    private ApplicationEventPublisher eventPublisher;

    public int getAllGatewaysCount() {
        return this.gatewayRepository.getAllGatewaysCount();
    }

    public List<AbstractGateway> getGateways(UriComponentsBuilder uriBuilder, HttpServletResponse response, GatewayPaginationRequest request) {
        Page<Gateway> gateways = getGatewaysPage(request);
        if(!gateways.getContent().isEmpty())
            triggerGetGatewayPageDiscovery(uriBuilder, response, request.getPage(), gateways.getTotalPages(), request.getSize());
        return this.abstractGatewaysPage(gateways);
    }
    private Page<Gateway> getGatewaysPage(GatewayPaginationRequest request) {
        Pageable gatewaysPageSortedById = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id"));
        Page<Gateway> gateways = this.gatewayRepository.findAll(gatewaysPageSortedById);
        return gateways;
    }
    public List<AbstractGateway> abstractGatewaysPage(Page<Gateway> gateways) {
        return gateways.getContent().stream().map(Gateway::getAbstractGatewayFromGateway)
                .collect(Collectors.toList());
    }

    public Gateway getGateway(String gatewayUUID) {
        Optional<Gateway> result = Optional.ofNullable(this.gatewayRepository.findGatewayByGatewayUuidEquals(gatewayUUID));
        if(result.isPresent())
            return result.get();
        throw new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, gatewayUUID));
    }

    public Gateway saveGateway(UriComponentsBuilder uriBuilder, HttpServletResponse response, Gateway gateway) {
        Gateway savedGateway = gatewayRepository.saveAndFlush(gateway);
        triggerCreateGatewayDiscovery(uriBuilder, response, savedGateway.getGatewayUuid());
        return savedGateway;
    }

    public Gateway updateGateway(UriComponentsBuilder uriBuilder, HttpServletResponse response,  String gatewayUUID, Gateway newGateway) {
        Gateway gatewayToBeUpdated = getGateway(gatewayUUID);
        Gateway gateway = Gateway.builder()
                .id(gatewayToBeUpdated.getId())
                .gatewayUuid(newGateway.getGatewayUuid())
                .gatewayName(newGateway.getGatewayName())
                .gatewayIpv4(newGateway.getGatewayIpv4())
                .devices(gatewayToBeUpdated.getDevices()).build();
        Gateway savedGateway = gatewayRepository.saveAndFlush(gateway);
        triggerUpdateGatewayDiscovery(uriBuilder, response, savedGateway.getGatewayUuid());
        return savedGateway;
    }

    public void triggerGetGatewayPageDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response,
                                               Integer page, Integer totalPages, Integer size){
        uriBuilder.path(API_ROOT+GET_ALL_GATEWAYS);
        eventPublisher.publishEvent(new PaginatedResultsEvent(this, uriBuilder, response, page, totalPages, size));
    }
    public void triggerCreateGatewayDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String uuid) {
        uriBuilder.path(API_ROOT+GET_GATEWAY_DETAILS_WITH_ITS_DEVICES);
        uriBuilder.uriVariables(Collections.singletonMap("gateway_uuid", uuid));
        eventPublisher.publishEvent(new CreationResultEvent(this, uriBuilder, response));
    }
    public void triggerUpdateGatewayDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response, String uuid) {
        uriBuilder.path(API_ROOT+UPDATE_GATEWAY_DETAILS);
        uriBuilder.uriVariables(Collections.singletonMap("gateway_uuid", uuid));
        eventPublisher.publishEvent(new CreationResultEvent(this, uriBuilder, response));
    }
}
