package com.musala.gatewaysapi.services;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.hateoas.listeners.GatewayPaginationDiscoverabilityListener;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.repositories.GatewayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;

import static com.musala.gatewaysapi.utils.GatewaysTestUtils.getAbstractGatewayListPage4Size10;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.getGatewaysPages;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.getPaginationLinks;
import static com.musala.gatewaysapi.utils.TestUtils.getUriBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { GatewayService.class, GatewayRepository.class, ApplicationEventPublisher.class, Gateway.class,
                            GatewayPaginationDiscoverabilityListener.class, AbstractGateway.class, GatewayPaginationRequest.class}
)
class GatewayServiceTest {
    public static final String PAGE_NUMBER_4_PAGE_SIZE_10 = "{\"page\":4,\"size\":10}";
    @MockBean
    private GatewayRepository gatewayRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private GatewayService gatewayService;
    private final Gson gson = new Gson();

    @BeforeEach
    public void beforeEach() {
        gatewayService = new GatewayService(gatewayRepository, eventPublisher);
    }
    @Test
    void getGateways_Page4_Size10_Successfully() {
        StringBuilder expectedResponse = new StringBuilder();
        StringBuilder actualResponse = new StringBuilder();
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        String ExpectedPaginationLinksHeader = getPaginationLinks();

        GatewayPaginationRequest request =  gson.fromJson(PAGE_NUMBER_4_PAGE_SIZE_10, GatewayPaginationRequest.class);
        getAbstractGatewayListPage4Size10().stream().forEachOrdered(abstractGateway -> expectedResponse.append(gson.toJson(abstractGateway)));
        when(gatewayRepository.findAll(PageRequest.of(request.getPage(), request.getSize(), Sort.by("id")))).thenReturn(getGatewaysPages());
        gatewayService.getGateways(getUriBuilder(), mockResponse, request)
                .stream().forEachOrdered(abstractGateway -> actualResponse.append(gson.toJson(abstractGateway)));
        assertEquals( expectedResponse.toString(), actualResponse.toString());
        assertEquals( mockResponse.getHeader("Link"), ExpectedPaginationLinksHeader);
    }

}