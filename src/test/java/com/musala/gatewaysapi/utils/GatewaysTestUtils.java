package com.musala.gatewaysapi.utils;

import com.google.gson.Gson;
import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.models.AbstractGateway;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Constant class to hold any common utility methods.
 */
@UtilityClass
public class GatewaysTestUtils {
    static Gson gson = new Gson();
    public static List<AbstractGateway> getAbstractGatewayListPage4Size10() {
        String jsonAbstractGateway = "[{\"gatewayUuid\":\"a3c23161-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2316a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"70.22.2.45\"}," +
                "{\"gatewayUuid\":\"a3c231e0-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c231e8-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"17.50.0.52\"}," +
                "{\"gatewayUuid\":\"a3c23256-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2325f-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"60.45.47.99\"}," +
                "{\"gatewayUuid\":\"a3c232d8-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c232e0-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"53.71.94.57\"}," +
                "{\"gatewayUuid\":\"a3c23350-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23434-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"3.46.19.59\"}," +
                "{\"gatewayUuid\":\"a3c23585-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23596-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"40.24.0.28\"}," +
                "{\"gatewayUuid\":\"a3c23641-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2364a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"40.18.72.4\"}," +
                "{\"gatewayUuid\":\"a3c236c4-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c236cc-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"3.6.19.80\"}," +
                "{\"gatewayUuid\":\"a3c2373e-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23746-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"41.67.12.61\"}," +
                "{\"gatewayUuid\":\"a3c237ba-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c237c2-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"69.64.10.62\"}]";
        return Arrays.asList(gson.fromJson(jsonAbstractGateway, AbstractGateway[].class));
    }
    public static Page<Gateway> getGatewaysPages() {
        String jsonGateways =
                "[{\"id\":41,\"gatewayUuid\":\"a3c23161-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2316a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"70.22.2.45\",\"devices\":[{\"id\":87,\"devicesUuid\":\"a3e3befe-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3bf16-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true},{\"id\":88,\"devicesUuid\":\"a3e3c2d5-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3c2e6-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":42,\"gatewayUuid\":\"a3c231e0-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c231e8-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"17.50.0.52\",\"devices\":[{\"id\":89,\"devicesUuid\":\"a3e3c6f0-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3c703-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":43,\"gatewayUuid\":\"a3c23256-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2325f-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"60.45.47.99\",\"devices\":[{\"id\":90,\"devicesUuid\":\"a3e3ca70-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3ca81-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":44,\"gatewayUuid\":\"a3c232d8-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c232e0-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"53.71.94.57\",\"devices\":[{\"id\":91,\"devicesUuid\":\"a3e3cdd9-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3cdea-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":45,\"gatewayUuid\":\"a3c23350-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23434-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"3.46.19.59\",\"devices\":[{\"id\":92,\"devicesUuid\":\"a3e3d296-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3d2a9-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":46,\"gatewayUuid\":\"a3c23585-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23596-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"40.24.0.28\",\"devices\":[{\"id\":93,\"devicesUuid\":\"a3e3d627-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3d637-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":47,\"gatewayUuid\":\"a3c23641-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c2364a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"40.18.72.4\",\"devices\":[{\"id\":94,\"devicesUuid\":\"a3e3d997-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3d9a7-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":48,\"gatewayUuid\":\"a3c236c4-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c236cc-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"3.6.19.80\",\"devices\":[{\"id\":95,\"devicesUuid\":\"a3e3dd0b-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3dd1f-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true},{\"id\":96,\"devicesUuid\":\"a3e3e082-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3e093-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":49,\"gatewayUuid\":\"a3c2373e-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c23746-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"41.67.12.61\",\"devices\":[{\"id\":97,\"devicesUuid\":\"a3e3e423-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3e433-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true},{\"id\":98,\"devicesUuid\":\"a3e3e7a4-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3e7b5-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}," +
                        "{\"id\":50,\"gatewayUuid\":\"a3c237ba-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gatway-a3c237c2-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"69.64.10.62\",\"devices\":[{\"id\":99,\"devicesUuid\":\"a3e3eb80-3c2d-11ec-a662-0242ac160003\",\"devicesName\":\"devices-a3e3eb91-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\",\"deviceCreationDate\":{\"seconds\":1635892726,\"nanos\":0},\"status\":true}]}]";
        return new PageImpl(
                Arrays.asList(gson.fromJson(jsonGateways, Gateway[].class)),
                PageRequest.of(4, 10, Sort.by("id")) , 80);
    }
    public static String getPaginationLinks() {
        return "<http://localhost:8080/gateways-api/api/rest/gateways?page=0&size=10>; rel=\"first\", " +
                "<http://localhost:8080/gateways-api/api/rest/gateways?page=3&size=10>; rel=\"prev\", " +
                "<http://localhost:8080/gateways-api/api/rest/gateways?page=5&size=10>; rel=\"next\", " +
                "<http://localhost:8080/gateways-api/api/rest/gateways?page=7&size=10>; rel=\"last\"";
    }
    public static List<AbstractGateway> generateAbstractGatewayList() {
        return generateGatewayList().stream().map(Gateway::getAbstractGatewayFromGateway).collect(Collectors.toList());
    }
    public static List<Gateway> generateGatewayList() {
        return Arrays.asList(
                generateGateway(), generateGateway(), generateGateway(), generateGateway(), generateGateway(),
                generateGateway(), generateGateway(), generateGateway(), generateGateway(), generateGateway());
    }
    public static Gateway generateGateway() {
        Random random = new Random();
        return Gateway.builder()
                .gatewayIpv4(TestUtils.generateRandomIPv4())
                .gatewayName("Gateway" + random.nextInt(256))
                .gatewayUuid(UUID.randomUUID().toString()).build();
    }
}
