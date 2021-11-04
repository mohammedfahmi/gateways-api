package com.musala.gatewaysapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheck implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
}
