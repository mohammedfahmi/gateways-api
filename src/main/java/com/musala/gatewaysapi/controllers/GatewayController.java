package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.entities.Gateway;
import com.musala.gatewaysapi.models.AbstractGateway;
import com.musala.gatewaysapi.models.GatewayModel;
import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.services.GatewayService;
import com.musala.gatewaysapi.validations.AbstractGatewayValidator;
import com.musala.gatewaysapi.validations.IsUuid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AbstractGateway.class),
                                    examples = { @ExampleObject(
                                            name = "An example Response to getGatewayPage endpoint.",
                                            value = "[{\"gatewayUuid\":\"a3c23161-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gateway-a3c2316a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"70.22.2.45\"}," +
                                                    "{\"gatewayUuid\":\"a3c231e0-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gateway-a3c231e8-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"17.50.0.52\"}]"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad page and bad size values.",
                                            value = "{\"error\":\"Validation failed, failed to bind value four to field page, failed to bind value ten to field size.\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "401",
                    description = "unAuthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an un-Authorized request.",
                                            value = "{\"timestamp\":\"2021-11-16T03:45:26.377+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/gateways\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an Internal Server Error.",
                                            value = "{\"error\":\"something went wrong.\"}"
                                    )}
                            )
                    })
    })
    @GetMapping(path = GET_ALL_GATEWAYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AbstractGateway> getGatewayPage(
            @Parameter( in= ParameterIn.QUERY, name = "gatewayPaginationRequest", schema =  @Schema(implementation = GatewayPaginationRequest.class),
                    description="object contains the Query parameters size and page, default values for size is 10 and for page is 0. Cannot be empty.", required=true,
                    example = "{\"page\": 0, \"size\": 10}")
            @Valid GatewayPaginationRequest gatewayPaginationRequest,
            BindingResult result, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        return gatewayService.getGateways(uriBuilder, response, gatewayPaginationRequest);
    }

    @Operation(summary = "get a single Gateway resource", tags = {"getGateway"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully retrieved a single Gateway resource",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GatewayModel.class),
                                    examples = { @ExampleObject(
                                            name = "An example Response to getGatewayPage endpoint.",
                                            value = "{\"gatewayUuid\":\"a3c23161-3c2d-11ec-a662-0242ac160003\",\"gatewayName\":\"gateway-a3c2316a-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"70.22.2.45\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad gateway_uuid, not in uuid format.",
                                            value = "{\"error\":\"Validation failed, getGateway.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "401",
                    description = "unAuthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an un-Authorized request.",
                                            value = "{\"timestamp\":\"2021-11-16T03:45:26.377+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/gateway/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to gateway_uuid not found.",
                                            value = "{\"error\":\"Requested Gateway with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an Internal Server Error.",
                                            value = "{\"error\":\"something went wrong.\"}"
                                    )}
                            )
                    })
    })
    @GetMapping(path = GET_GATEWAY_DETAILS_WITH_ITS_DEVICES, produces = MediaType.APPLICATION_JSON_VALUE)
    public GatewayModel getGateway(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the wanted gateway. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        return gatewayService.getGateway(uriBuilder, response, gateway_uuid).getGatewayModelFromGateway();
    }

    @Operation(summary = "create a Gateway", tags = {"createGateway"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully created Gateway entity",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to createGateway endpoint.",
                                            value = "{\"message\":\"Gateway created successfully\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad request body.",
                                            value = "{\"error\":\"Validation failed, failed to bind value null to field gatewayUuid, failed to bind value null to field gatewayIpv4, failed to bind value null to field gatewayName.\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "401",
                    description = "unAuthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an un-Authorized request.",
                                            value = "{\"timestamp\":\"2021-11-16T03:45:26.377+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/gateway/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an Internal Server Error.",
                                            value = "{\"error\":\"something went wrong.\"}"
                                    )}
                            )
                    })
    })
    @PostMapping(path = CREATE_NEW_GATEWAY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> createGateway(
            @Valid @RequestBody(description = "Gateway to be created.", required = true, content = @Content(
                    schema=@Schema(implementation = AbstractGateway.class), mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"gatewayUuid\":\"a3c231e0-3c2d-11ec-a662-0242ac160003\"," +
                            "\"gatewayName\":\"gateway-a3c231e8-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"17.50.0.52\"}")})
            ) AbstractGateway abstractGateway, BindingResult result, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        gatewayService.saveGateway(uriBuilder, response, Gateway.builder()
                .gatewayIpv4(abstractGateway.getGatewayIpv4())
                .gatewayName(abstractGateway.getGatewayName())
                .gatewayUuid(abstractGateway.getGatewayUuid()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway created successfully"));
    }

    @Operation(summary = "Update Gateway details", tags = {"updateGateway"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully updated a single Gateway entity",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to updateGateway endpoint.",
                                            value = "{\"message\":\"Gateway updated successfully\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad gateway_uuid, not in uuid format.",
                                            value = "{\"error\":\"Validation failed, updateGateway.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "401",
                    description = "unAuthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an un-Authorized request.",
                                            value = "{\"timestamp\":\"2021-11-16T03:45:26.377+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/gateway/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to gateway_uuid not found.",
                                            value = "{\"error\":\"Requested Gateway with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to an Internal Server Error.",
                                            value = "{\"error\":\"something went wrong.\"}"
                                    )}
                            )
                    })
    })
    @PutMapping(path = UPDATE_GATEWAY_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> updateGateway(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the gateway to be updated. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            @Valid @RequestBody(description = "new values of the Gateway to be updated.", required = true, content = @Content(
                    schema=@Schema(implementation = AbstractGateway.class), mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"gatewayUuid\":\"a3c231e0-3c2d-11ec-a662-0242ac160003\"," +
                            "\"gatewayName\":\"gateway-a3c231e8-3c2d-11ec-a662-0242ac160003\",\"gatewayIpv4\":\"17.50.0.52\"}")})
            ) AbstractGateway abstractGateway, BindingResult result, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        gatewayService.updateGateway(uriBuilder, response, gateway_uuid, Gateway.builder()
                .gatewayIpv4(abstractGateway.getGatewayIpv4())
                .gatewayName(abstractGateway.getGatewayName())
                .gatewayUuid(abstractGateway.getGatewayUuid()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway Updated successfully"));
    }

}
