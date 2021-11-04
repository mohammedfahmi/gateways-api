package com.musala.gatewaysapi.models;

import com.musala.gatewaysapi.validations.PageIsValid;
import lombok.*;

import java.io.Serializable;

@PageIsValid
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayPaginationRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int page = 0;
    private int size = 10;
}
