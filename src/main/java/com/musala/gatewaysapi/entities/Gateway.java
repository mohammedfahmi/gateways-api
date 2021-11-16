package com.musala.gatewaysapi.entities;

import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayModel;
import lombok.*;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.musala.gatewaysapi.constants.Constants.DEVICE_NOT_FOUND_ERROR_MESSAGE;
import static java.util.Collections.EMPTY_LIST;

@Getter
@Setter
@Builder(toBuilder=true)
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

    @OneToMany(mappedBy="gateway")
    private List<Device> devices;

    public AbstractGateway getAbstractGatewayFromGateway() {
        return AbstractGateway.builder()
                        .gatewayUuid(this.getGatewayUuid() == null ? "": this.getGatewayUuid())
                        .gatewayName(this.getGatewayName() == null ? "": this.getGatewayName())
                        .gatewayIpv4(this.getGatewayIpv4() == null ? "": this.getGatewayIpv4())
                        .build();

    }
    @SuppressWarnings("unchecked")
    public GatewayModel getGatewayModelFromGateway() {
        return GatewayModel.builder()
                        .gatewayUuid(this.getGatewayUuid() == null ? "": this.getGatewayUuid())
                        .gatewayName(this.getGatewayName() == null ? "": this.getGatewayName())
                        .gatewayIpv4(this.getGatewayIpv4() == null ? "": this.getGatewayIpv4())
                        .devices(this.getDevices() == null ? EMPTY_LIST :
                                this.getDevices().stream().map(Device::getDeviceModelFromDevice).collect(Collectors.toList()))
                        .build();

    }

    public Device getDeviceFromGatewayByUuid(String deviceUuid) {
        String notFoundErrorMessage = MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, deviceUuid);
        if(!Optional.ofNullable(this.getDevices()).isPresent())
            throw new EntityNotFoundException(notFoundErrorMessage);
        List<Device> devices = this.getDevices().stream().filter(device -> device.getDevicesUuid().equals(deviceUuid)).collect(Collectors.toList());
        if(devices.isEmpty())
            throw new EntityNotFoundException(notFoundErrorMessage);
        return devices.get(0);
    }
}