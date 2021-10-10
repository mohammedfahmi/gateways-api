package com.musala.gatewaysapi.models;

import com.musala.gatewaysapi.entities.Gateway;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AbstractGateway {
    private String gatewayUuid;

    private String gatewayName;

    private String gatewayIpv4;

    public AbstractGateway getAbstractGatewayFromGateway(Gateway gateway) {
        return AbstractGateway.builder()
                .gatewayUuid(gateway.getGatewayUuid())
                .gatewayName(gateway.getGatewayName())
                .gatewayIpv4(gateway.getGatewayIpv4()).build();
    }
}
