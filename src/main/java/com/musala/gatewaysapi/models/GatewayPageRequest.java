package com.musala.gatewaysapi.models;

import com.musala.gatewaysapi.validations.PageIsValid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@PageIsValid
@Data
public class GatewayPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    private Integer size = 10;
    @NotNull
    private Integer page = 0;

}
