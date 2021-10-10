package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayPageRequest;
import com.musala.gatewaysapi.services.GatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.musala.gatewaysapi.constants.ApiMapping.API_ROOT;
import static com.musala.gatewaysapi.constants.ApiMapping.GET_ALL_GATEWAYS;

@RestController
@RequestMapping(API_ROOT)
@Validated
@Slf4j
@AllArgsConstructor
public class GatewayController {

    private GatewayService gatewayService;
    @Operation(summary = "get a page of Gateways", tags = {"getGatewayPage" })
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
    public ResponseEntity<List<AbstractGateway>> getGatewayPage(
            @Validated @RequestBody final GatewayPageRequest request) {
        final List<AbstractGateway> response = gatewayService.getGatewaysPage(request);
        return ResponseEntity.ok(response);
    }
}
