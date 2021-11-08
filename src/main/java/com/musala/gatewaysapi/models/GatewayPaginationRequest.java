package com.musala.gatewaysapi.models;

import com.musala.gatewaysapi.validations.PageIsValid;
import lombok.*;

import java.io.Serializable;

import static com.musala.gatewaysapi.constants.Constants.DEFAULT_PAGE;
import static com.musala.gatewaysapi.constants.Constants.DEFAULT_PAGE_SIZE;

@PageIsValid
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayPaginationRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int page = DEFAULT_PAGE;
    private int size = DEFAULT_PAGE_SIZE;
}
