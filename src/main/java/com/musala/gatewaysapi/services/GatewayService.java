package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.entities.Gateway;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;


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
            triggerGetGatewayDiscovery(uriBuilder, response, request.getPage(), gateways.getTotalPages(), request.getSize());
        return this.abstractGatewaysPage(gateways);
    }

    private Page<Gateway> getGatewaysPage(GatewayPaginationRequest request) {
        Pageable gatewaysPageSortedById = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id"));
        Page<Gateway> gateways = this.gatewayRepository.findAll(gatewaysPageSortedById);
        return gateways;
    }

    public void triggerGetGatewayDiscovery(UriComponentsBuilder uriBuilder, HttpServletResponse response,
                                           Integer page, Integer totalPages, Integer size){
        eventPublisher.publishEvent(
                new PaginatedResultsEvent(
                        this, uriBuilder, response, page, totalPages, size));
    }

    public List<AbstractGateway> abstractGatewaysPage(Page<Gateway> gateways) {
        return gateways.getContent().stream().map(Gateway::getAbstractGatewayFromGateway)
                .collect(Collectors.toList());
    }
}
