package com.musala.gatewaysapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayModel {
    private String gatewayUuid;
    private String gatewayName;
    private String gatewayIpv4;
    private List<DeviceModel> devices;
}
