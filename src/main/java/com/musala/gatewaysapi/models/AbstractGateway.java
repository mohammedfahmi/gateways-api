package com.musala.gatewaysapi.models;

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
}
