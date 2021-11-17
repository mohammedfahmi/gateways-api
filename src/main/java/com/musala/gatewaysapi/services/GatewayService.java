package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.repositories.GatewayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;


@Slf4j
@Service
@AllArgsConstructor
public class GatewayService {

    private GatewayRepository gatewayRepository;
    private DiscoveryService discoveryService;

    public int getAllGatewaysCount() {
        return this.gatewayRepository.getAllGatewaysCount();
    }

    public List<AbstractGateway> getGateways(UriComponentsBuilder uriBuilder, HttpServletResponse response, GatewayPaginationRequest request) {
        Page<Gateway> gateways = getGatewaysPage(request);
        if(!gateways.getContent().isEmpty())
            discoveryService.triggerGetGatewayPageDiscovery(uriBuilder, response, request.getPage(), gateways.getTotalPages(), request.getSize());
        return this.abstractGatewaysPage(gateways);
    }
    private Page<Gateway> getGatewaysPage(GatewayPaginationRequest request) {
        Pageable gatewaysPageSortedById = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id"));
        return this.gatewayRepository.findAll(gatewaysPageSortedById);
    }
    public List<AbstractGateway> abstractGatewaysPage(Page<Gateway> gateways) {
        return gateways.getContent().stream().map(Gateway::toAbstractGateway)
                .collect(Collectors.toList());
    }

    public Gateway getGateway(String gatewayUUID) {
        Optional<Gateway> result = Optional.ofNullable(this.gatewayRepository.findGatewayByGatewayUuidEquals(gatewayUUID));
        if(result.isPresent())
            return result.get();
        throw new EntityNotFoundException(MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, gatewayUUID));
    }

    public Gateway getGateway(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUUID) {
        Gateway gateway = getGateway(gatewayUUID);
        discoveryService.triggerGetGatewayEntityDiscovery(uriBuilder, response, getAllGatewaysCount());
        return gateway;
    }

    public Gateway saveGateway(UriComponentsBuilder uriBuilder, HttpServletResponse response, Gateway gateway) {
        Gateway savedGateway = gatewayRepository.saveAndFlush(gateway);
        discoveryService.triggerSaveGatewayDiscovery(uriBuilder, response, savedGateway.getGatewayUuid());
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
        discoveryService.triggerSaveGatewayDiscovery(uriBuilder, response, savedGateway.getGatewayUuid());
        return savedGateway;
    }



}
