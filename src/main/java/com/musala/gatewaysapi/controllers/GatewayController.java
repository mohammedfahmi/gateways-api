package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayModel;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.services.GatewayService;
import com.musala.gatewaysapi.validations.AbstractGatewayValidator;
import com.musala.gatewaysapi.validations.IsUuid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.handleBindResult;

@RestController
@RequestMapping(API_ROOT)
@Validated
@Slf4j
@AllArgsConstructor
public class GatewayController {

    private GatewayService gatewayService;
    private AbstractGatewayValidator abstractGatewayValidator;

    @InitBinder(value = "abstractGateway")
    void initStudentValidator(WebDataBinder binder) {
        binder.setValidator(this.abstractGatewayValidator);
    }

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
        handleBindResult(result);
        return gatewayService.getGateways(uriBuilder, response, gatewayPaginationRequest);
    }

    @Operation(summary = "get a Gateway with full details", tags = {"getGateway"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully retrieved a single Gateway entity",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GatewayModel.class))
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter")
    })
    @GetMapping(path = GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, produces = MediaType.APPLICATION_JSON_VALUE)
    public GatewayModel getGateway(
            @Valid @IsUuid @PathVariable String gateway_uuid, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        return gatewayService.getGateway(gateway_uuid).getGatewayModelFromGateway();
    }

    @Operation(summary = "create a Gateway", tags = {"createGateway"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully created Gateway entity",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter")
    })
    @PostMapping(path = CREATE_NEW_GATEWAY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> createGateway(
            @Valid @RequestBody AbstractGateway abstractGateway, BindingResult result,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        gatewayService.saveGateway(uriBuilder, response, Gateway.builder()
                .gatewayIpv4(abstractGateway.getGatewayIpv4())
                .gatewayName(abstractGateway.getGatewayName())
                .gatewayUuid(abstractGateway.getGatewayUuid()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway created successfully"));
    }
    @PutMapping(path = UPDATE_GATEWAY_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> updateGateway(
            @Valid @IsUuid @PathVariable String gateway_uuid, @Valid @RequestBody AbstractGateway abstractGateway,
            BindingResult result, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        gatewayService.updateGateway(uriBuilder, response, gateway_uuid, Gateway.builder()
                .gatewayIpv4(abstractGateway.getGatewayIpv4())
                .gatewayName(abstractGateway.getGatewayName())
                .gatewayUuid(abstractGateway.getGatewayUuid()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway Updated successfully"));
    }

}
