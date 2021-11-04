package com.musala.gatewaysapi.controllers;

import com.google.gson.Gson;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.services.GatewayService;
import com.musala.gatewaysapi.validations.PaginationRequestValidator;
import com.musala.gatewaysapi.validations.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.musala.gatewaysapi.constants.ApiMapping.API_ROOT;
import static com.musala.gatewaysapi.constants.ApiMapping.GET_ALL_GATEWAYS;
import static com.musala.gatewaysapi.validations.ValidationErrorMessages.REQUESTED_PAGE_NOT_AVAILABLE;

@RestController
@RequestMapping(API_ROOT)
@Validated
@Slf4j
@AllArgsConstructor
public class GatewayController {

    private GatewayService gatewayService;
    private ValidationUtil validationUtil;
//    private PaginationRequestValidator paginationRequestValidator;

//    @InitBinder(value = "gatewayPaginationRequest")
//    void initStudentValidator(WebDataBinder binder) {
//        binder.setValidator(this.paginationRequestValidator);
//    }

    @Operation(summary = "get a page of Gateways", tags = {"getGatewayPage"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully retrieved a single page of Gateways",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AbstractGateway.class))
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter")
    })
    @GetMapping(path = GET_ALL_GATEWAYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AbstractGateway> getGatewayPage(
            @Valid GatewayPaginationRequest gatewayPaginationRequest, BindingResult result,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        this.validationUtil.handleBindResult(result);
        return gatewayService.getGateways(uriBuilder, response, gatewayPaginationRequest);
    }

}
