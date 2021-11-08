package com.musala.gatewaysapi.controllers;

import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.models.DeviceModel;
import com.musala.gatewaysapi.services.DeviceService;
import com.musala.gatewaysapi.validations.DeviceModelValidator;
import com.musala.gatewaysapi.validations.DidGatewayDevicesExceededLimit;
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
import java.util.Map;

import static com.musala.gatewaysapi.constants.ApiMapping.*;
import static com.musala.gatewaysapi.validations.ValidationUtil.handleBindResult;

@RestController
@RequestMapping
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

    @Operation(summary = "get a gateway's device", tags = {"getDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully retrieved a single Device entity",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeviceModel.class))
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter"),
            @ApiResponse(responseCode = "404",
                    description = "Not found Entity")
    })
    @GetMapping(path = GET_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeviceModel getDevice(
            @Valid @IsUuid @PathVariable String gateway_uuid, @Valid @IsUuid @PathVariable String device_uuid,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        return deviceService.getDevice(uriBuilder, response, gateway_uuid, device_uuid).getDeviceModelFromDevice();
    }

    @Operation(summary = "create a Device", tags = {"createDevice"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successfully created Device entity",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameter")
    })
    @PostMapping(path = CREATE_NEW_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> createDevice(
            @Valid @DidGatewayDevicesExceededLimit @PathVariable String gateway_uuid,
            @Valid @RequestBody DeviceModel deviceModel, BindingResult result,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        deviceService.createDevice( uriBuilder, response, gateway_uuid,
                Device.builder().devicesUuid(deviceModel.getDevicesUuid())
                        .devicesName(deviceModel.getDevicesName())
                        .deviceCreationDate(deviceModel.getDeviceCreationDate())
                        .vendor(deviceModel.getVendor())
                        .status(deviceModel.getStatus()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Device created successfully and added to requested gateway device list"));
    }
    @PutMapping(path = UPDATE_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> updateDevice(
            @Valid @IsUuid @PathVariable String gateway_uuid, @Valid @IsUuid @PathVariable String device_uuid,
            @Valid @RequestBody DeviceModel deviceModel, BindingResult result,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        handleBindResult(result);
        deviceService.updateDevice(uriBuilder, response, device_uuid, gateway_uuid,  Device.builder().devicesUuid(deviceModel.getDevicesUuid())
                .devicesName(deviceModel.getDevicesName())
                .deviceCreationDate(deviceModel.getDeviceCreationDate())
                .vendor(deviceModel.getVendor())
                .status(deviceModel.getStatus()).build());
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway's Device updated successfully"));
    }
    @DeleteMapping(path = DELETE_GATEWAY_DEVICE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String >> deleteDevice(
            @Valid @IsUuid @PathVariable String gateway_uuid, @Valid @IsUuid @PathVariable String device_uuid,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        deviceService.deleteDevice(uriBuilder, response, device_uuid, gateway_uuid);
        return ResponseEntity.ok(Collections.singletonMap("message","Gateway's Device deleted successfully"));
    }
}
