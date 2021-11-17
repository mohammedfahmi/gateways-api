package com.musala.gatewaysapi.entities;

import com.musala.gatewaysapi.models.DeviceModel;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name="devices")
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id", nullable = false)
    private Long id;

    @Column(name = "devices_uuid", nullable = false)
    private String devicesUuid;

    @Column(name = "devices_name", nullable = false)
    private String devicesName;

    @Column(name = "vendor", nullable = false)
    private String vendor;

    @Column(name = "device_creation_date", nullable = false)
    private LocalDateTime deviceCreationDate;

    @Column(name = "status", nullable = false)
    private Boolean status = false;

    @ManyToOne
    @JoinColumn(name = "gateway_id", nullable = false)
    private Gateway gateway;

    public DeviceModel toModel() {
        return DeviceModel.builder()
                        .devicesUuid(this.devicesUuid == null ? "": this.devicesUuid)
                        .devicesName(this.devicesName == null ? "": this.devicesName)
                        .vendor(this.vendor == null ? "": this.vendor)
                        .deviceCreationDate(this.deviceCreationDate == null ? LocalDateTime.MIN: this.deviceCreationDate)
                        .status(this.status != null && this.status)
                        .build();
    }
}