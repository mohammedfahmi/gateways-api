package com.musala.gatewaysapi.entities;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    private Instant deviceCreationDate;

    @Column(name = "status", nullable = false)
    private Boolean status = false;
}