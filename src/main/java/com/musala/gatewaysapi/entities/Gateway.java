package com.musala.gatewaysapi.entities;

import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayModel;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gateways")
@Entity
public class Gateway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gateway_id", nullable = false)
    private Long id;

    @Column(name = "gateway_uuid", nullable = false)
    private String gatewayUuid;

    @Column(name = "gateway_name", nullable = false)
    private String gatewayName;

    @Column(name = "gateway_ipv4", nullable = false)
    private String gatewayIpv4;

    @OneToMany()
    @JoinColumn(name = "gateway_id", nullable = false)
    private List<Device> devices;

    public AbstractGateway getAbstractGatewayFromGateway() {
        return AbstractGateway.builder()
                        .gatewayUuid(this.getGatewayUuid() == null ? "": this.getGatewayUuid())
                        .gatewayName(this.getGatewayName() == null ? "": this.getGatewayName())
                        .gatewayIpv4(this.getGatewayIpv4() == null ? "": this.getGatewayIpv4())
                        .build();

    }
    public GatewayModel getGatewayModelFromGateway() {
        return GatewayModel.builder()
                        .gatewayUuid(this.getGatewayUuid() == null ? "": this.getGatewayUuid())
                        .gatewayName(this.getGatewayName() == null ? "": this.getGatewayName())
                        .gatewayIpv4(this.getGatewayIpv4() == null ? "": this.getGatewayIpv4())
                        .devices(this.getDevices() == null ? EMPTY_LIST :
                                this.getDevices().stream().map(Device::getDeviceModelFromDevice).collect(Collectors.toList()))
                        .build();

    }
}