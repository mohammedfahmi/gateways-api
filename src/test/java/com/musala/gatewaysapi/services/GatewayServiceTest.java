package com.musala.gatewaysapi.services;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.hateoas.listeners.GetDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.SaveDiscoverabilityListener;
import com.musala.gatewaysapi.hateoas.listeners.PaginationDiscoverabilityListener;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.repositories.GatewayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import java.text.MessageFormat;

import static com.musala.gatewaysapi.constants.Constants.GATEWAY_NOT_FOUND_ERROR_MESSAGE;
import static com.musala.gatewaysapi.utils.GatewaysTestUtils.*;
import static com.musala.gatewaysapi.utils.TestUtils.asJsonString;
import static com.musala.gatewaysapi.utils.TestUtils.getUriBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { GatewayService.class, GatewayRepository.class, ApplicationEventPublisher.class, Gateway.class,
                            PaginationDiscoverabilityListener.class, AbstractGateway.class, GatewayPaginationRequest.class,
                            SaveDiscoverabilityListener.class, DiscoveryService.class, GetDiscoverabilityListener.class}
)
class GatewayServiceTest {

    @MockBean
    private GatewayRepository gatewayRepository;
    @Autowired
    private GatewayService gatewayService;

    private final Gson gson = new Gson();

    @BeforeEach
    public void beforeEach() {
    }
    @AfterEach
    public void reset_mocks() {
        Mockito.reset(gatewayRepository);
    }
    @Test
    void getGatewaysCount_successfully() {
        when(gatewayRepository.getAllGatewaysCount()).thenReturn(55);
        assertEquals(gatewayService.getAllGatewaysCount(), 55);
    }
    @SuppressWarnings("SimplifyStreamApiCallChains")
    @Test
    void getGateways_Page4_Size10_Successfully() {
        StringBuilder expectedResponse = new StringBuilder();
        StringBuilder actualResponse = new StringBuilder();
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        String expectedPaginationLinksHeader = getPaginationLinks();

        GatewayPaginationRequest request =  gson.fromJson(PAGE_NUMBER_4_PAGE_SIZE_10, GatewayPaginationRequest.class);
        getAbstractGatewayListPage4Size10().stream().forEachOrdered(abstractGateway -> expectedResponse.append(gson.toJson(abstractGateway)));
        when(gatewayRepository.findAll(PageRequest.of(request.getPage(), request.getSize(), Sort.by("id")))).thenReturn(getGatewaysPages());
        gatewayService.getGateways(getUriBuilder(), mockResponse, request)
                .stream().forEachOrdered(abstractGateway -> actualResponse.append(gson.toJson(abstractGateway)));
        assertEquals( expectedResponse.toString(), actualResponse.toString());
        assertEquals( mockResponse.getHeader("Link"), expectedPaginationLinksHeader);
    }
    @Test
    void getGateway_with_UUID_Successfully() {
        final Gateway gateway = generateGateway();
        when(gatewayRepository.findGatewayByGatewayUuidEquals(any())).thenReturn(gateway);
        final Gateway response = gatewayService.getGateway(gateway.getGatewayUuid());
        assertEquals( asJsonString(gateway), asJsonString(response));
    }
    @Test
    void getGateway_with_Discovery_Header_Successfully() {
        final Gateway gateway = generateGateway();
        gateway.setId(55L);
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        String getResultLinksHeader = getResultLink();
        when(gatewayRepository.findGatewayByGatewayUuidEquals(any())).thenReturn(gateway);
        when(gatewayRepository.getAllGatewaysCount()).thenReturn(55);
        final Gateway response = gatewayService.getGateway(getUriBuilder(), mockResponse, gateway.getGatewayUuid());
        assertEquals( asJsonString(gateway), asJsonString(response));
        assertEquals( mockResponse.getHeader("Link"), getResultLinksHeader);
    }
    @Test
    void getGateway_with_No_Valid_Gateway_UUID() {
        when(gatewayRepository.findGatewayByGatewayUuidEquals(any())).thenReturn(null);
        try{
            gatewayService.getGateway(VALID_UUID_WITH_NO_GATEWAY);
            fail();
        } catch (EntityNotFoundException e) {
            assertEquals( MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, VALID_UUID_WITH_NO_GATEWAY), e.getMessage());
        }
    }
    @Test
    void createGateway_Successfully() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final Gateway gateway = generateGateway();
        String ExpectedCreationLocationHeader = "http://localhost:8080/gateways-api/api/rest/gateway/" + gateway.getGatewayUuid();
        when(gatewayRepository.saveAndFlush(gateway)).thenReturn(gateway);
        final Gateway response = gatewayService.saveGateway(getUriBuilder(), mockResponse,gateway);
        assertEquals( asJsonString(gateway), asJsonString(response));
        assertEquals( mockResponse.getHeader("Location"), ExpectedCreationLocationHeader);
    }
    @Test
    void updateGateway_Successfully() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final Gateway oldGateway = generateGateway();
        oldGateway.setId(1L);
        final Gateway newGateway = generateGateway();
        newGateway.setId(1L);
        String ExpectedCreationLocationHeader = "http://localhost:8080/gateways-api/api/rest/gateway/" + newGateway.getGatewayUuid();
        when(gatewayRepository.findGatewayByGatewayUuidEquals(any())).thenReturn(oldGateway);
        when(gatewayRepository.saveAndFlush(any())).thenReturn(newGateway);
        final Gateway response = gatewayService.updateGateway(getUriBuilder(), mockResponse, oldGateway.getGatewayUuid(), newGateway);
        assertEquals( asJsonString(newGateway), asJsonString(response));
        assertEquals( mockResponse.getHeader("Location"), ExpectedCreationLocationHeader);
    }
    @Test
    void updateGateway_with_No_Valid_Gateway_UUID() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final Gateway oldGateway = generateGateway();
        oldGateway.setId(1L);
        final Gateway newGateway = generateGateway();
        newGateway.setId(1L);
        when(gatewayRepository.findGatewayByGatewayUuidEquals(any())).thenReturn(null);
        try {
            gatewayService.updateGateway(getUriBuilder(), mockResponse, oldGateway.getGatewayUuid(), newGateway);
            fail();
        } catch (EntityNotFoundException e) {
            assertEquals( MessageFormat.format(GATEWAY_NOT_FOUND_ERROR_MESSAGE, oldGateway.getGatewayUuid()), e.getMessage());
        }
    }


}