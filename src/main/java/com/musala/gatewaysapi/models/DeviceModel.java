package com.musala.gatewaysapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceModel {
    private String devicesUuid;
    private String devicesName;
    private String vendor;
    private LocalDateTime deviceCreationDate;
    private Boolean status = false;
}
