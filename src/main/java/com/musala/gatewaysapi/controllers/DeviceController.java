package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.models.DeviceModel;
import com.musala.gatewaysapi.services.DeviceService;
import com.musala.gatewaysapi.validations.DeviceModelValidator;
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
import java.util.Map;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.handleBindResult;

@RestController
@RequestMapping(API_ROOT)
@Validated
@Slf4j
@AllArgsConstructor
public class DeviceController {
    private DeviceService deviceService;
    private DeviceModelValidator deviceModelValidator;

    @InitBinder(value = "deviceModel")
    void initStudentValidator(WebDataBinder binder) {
        binder.setValidator(this.deviceModelValidator);
    }

    @Operation(summary = "create a Device", tags = {"createDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Device created successfully and added to requested gateway device list",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                    name = "An example Response to createDevice endpoint.",
                                    value = "{\"message\":\"Device created successfully and added to requested gateway device list\"}"
                                )}
                        )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                        name = "An example Response to a bad device_uuid, not in uuid format.",
                                        value = "{\"error\":\"Validation failed, createDevice.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
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
                                        value = "{\"timestamp\":\"2021-11-16T03:46:17.164+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f/device\"}"
                                )}
                        )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                        name = "An example Response to device_uuid not found.",
                                        value = "{\"error\":\"Requested Device with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
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
    @PostMapping(path = CREATE_NEW_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createDevice(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the gateway that contains wanted device. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            @Valid @RequestBody(description = "Device to be created.", required = true, content = @Content(
                            schema=@Schema(implementation = DeviceModel.class), mediaType = "application/json",
                            examples = {@ExampleObject(value = "{\"devicesUuid\":\"a3e3befe-3c2d-11ec-a662-0242ac160003\"," +
                                    "\"devicesName\":\"device-a3e3bf16-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\"," +
                                    "\"deviceCreationDate\":\"2021-11-02T22:38:46\",\"status\":true}")})
            ) DeviceModel deviceModel, BindingResult result,UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        deviceService.createDevice(uriBuilder, response, gateway_uuid,
                Device.builder().devicesUuid(deviceModel.getDevicesUuid())
                        .devicesName(deviceModel.getDevicesName())
                        .deviceCreationDate(deviceModel.getDeviceCreationDate())
                        .vendor(deviceModel.getVendor())
                        .status(deviceModel.getStatus()).build());
        return ResponseEntity.ok(Collections.singletonMap("message", "Device created successfully and added to requested gateway device list"));
    }

    @Operation(summary = "get a gateway's device", tags = {"getDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully retrieved a single Device entity",
                    content = {
                        @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceModel.class),
                            examples = { @ExampleObject(
                                    name = "An example Response to getDevice endpoint.",
                                    value = "{\"devicesUuid\":\"ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\",\"devicesName\":\"Device-3ebae3e5-bd64-4c8e-8370-48f96717e9e8\",\"vendor\":\"texas tech\",\"deviceCreationDate\":\"2021-11-16T03:08:32.258\",\"status\":true}"
                            )}
                        )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                        name = "An example Response to a bad device_uuid, not in uuid format.",
                                        value = "{\"error\":\"Validation failed, getDevice.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
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
                                        value = "{\"timestamp\":\"2021-11-16T03:45:26.377+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f/device/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                )}
                        )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                        name = "An example Response to device_uuid not found.",
                                        value = "{\"error\":\"Requested Device with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
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
    @GetMapping(path = GET_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeviceModel getDevice(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the gateway that contains wanted device. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            @Parameter( in= ParameterIn.PATH, name = "device_uuid",
                    description="Uuid of the device to be read. Cannot be empty.", required=true,
                    example = "a3e28a87-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String device_uuid,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        return deviceService.getDevice(uriBuilder, response, gateway_uuid, device_uuid).getDeviceModelFromDevice();
    }

    @Operation(summary = "update a Device details", tags = {"updateDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Gateway's Device updated successfully",
                    content = {
                        @Content(
                                mediaType = "application/json",
                                examples = { @ExampleObject(
                                        name = "An example Response to updateDevice endpoint.",
                                        value = "{\"message\":\"Gateway's Device updated successfully\"}"
                                )}
                        )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad device_uuid, not in uuid format.",
                                            value = "{\"error\":\"Validation failed, updateDevice.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
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
                                            value = "{\"timestamp\":\"2021-11-16T03:53:05.949+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f/device/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to device_uuid not found.",
                                            value = "{\"error\":\"Requested Device with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
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
    @PutMapping(path = UPDATE_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> updateDevice(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the gateway that contains wanted device. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            @Parameter( in= ParameterIn.PATH, name = "device_uuid",
                    description="Uuid of the device to be updated. Cannot be empty.", required=true,
                    example = "a3e28a87-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String device_uuid,
            @Valid @RequestBody(description = "Device to update.", required = true, content = @Content(
                    schema=@Schema(implementation = DeviceModel.class), mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"devicesUuid\":\"a3e3befe-3c2d-11ec-a662-0242ac160003\"," +
                            "\"devicesName\":\"device-a3e3bf16-3c2d-11ec-a662-0242ac160003\",\"vendor\":\"texas tech\"," +
                            "\"deviceCreationDate\":\"2021-11-02T22:38:46\",\"status\":true}")})
            ) DeviceModel deviceModel, BindingResult result, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        deviceService.updateDevice(uriBuilder, response, device_uuid, gateway_uuid, Device.builder().devicesUuid(deviceModel.getDevicesUuid())
                .devicesName(deviceModel.getDevicesName())
                .deviceCreationDate(deviceModel.getDeviceCreationDate())
                .vendor(deviceModel.getVendor())
                .status(deviceModel.getStatus()).build());
        return ResponseEntity.ok(Collections.singletonMap("message", "Gateway's Device updated successfully"));
    }

    @Operation(summary = "delete a Device details", tags = {"deleteDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Gateway's Device deleted successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to deleteDevice endpoint.",
                                            value = "{\"message\":\"Gateway's Device deleted successfully\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "400",
                    description = "bad request, not valid parameter",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to a bad device_uuid, not in uuid format.",
                                            value = "{\"error\":\"Validation failed, deleteDevice.gateway_uuid: Requested uuid q3c23161-3c2d-11ec-a662-0242ac160003 is not valid.\"}"
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
                                            value = "{\"timestamp\":\"2021-11-16T03:53:05.949+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"path\":\"/gateways-api/api/rest/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f/device/ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f\"}"
                                    )}
                            )
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Requested resource is not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = { @ExampleObject(
                                            name = "An example Response to device_uuid not found.",
                                            value = "{\"error\":\"Requested Device with uuid ad1275e3-b3ef-48e0-8ce8-fd3fbdf0080f is not Found\"}"
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
    @DeleteMapping(path = DELETE_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteDevice(
            @Parameter( in= ParameterIn.PATH, name = "gateway_uuid",
                    description="Uuid of the gateway that contains wanted device. Cannot be empty.", required=true,
                    example = "a3c221f5-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String gateway_uuid,
            @Parameter( in= ParameterIn.PATH, name = "device_uuid",
                    description="Uuid of the device to be deleted. Cannot be empty.", required=true,
                    example = "a3e28a87-3c2d-11ec-a662-0242ac160003")
            @Valid @IsUuid @PathVariable String device_uuid,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        deviceService.deleteDevice(uriBuilder, response, device_uuid, gateway_uuid);
        return ResponseEntity.ok(Collections.singletonMap("message", "Gateway's Device deleted successfully"));
    }
}
