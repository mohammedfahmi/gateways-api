package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPageRequest;
import com.musala.gatewaysapi.repositories.GatewayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class GatewayService {

    private GatewayRepository gatewayRepository;

    public int getAllGatewaysCount() {
        return this.gatewayRepository.getAllGatewaysCount();
    }

    public List<AbstractGateway> getGatewaysPage(GatewayPageRequest request) {
        Pageable gatewaysPageSortedById = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id"));
        return getGatewaysPage(gatewaysPageSortedById);
    }

    private List<AbstractGateway> getGatewaysPage(Pageable gatewaysPage) {
        return this.gatewayRepository.findAll(gatewaysPage).getContent()
                .stream().map(new AbstractGateway()::getAbstractGatewayFromGateway)
                .collect(Collectors.toList());
    }
}
