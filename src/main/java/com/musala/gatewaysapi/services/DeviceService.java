package com.musala.gatewaysapi.services;

import com.musala.gatewaysapi.entities.Device;
import com.musala.gatewaysapi.repositories.DeviceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Optional;

import static com.musala.gatewaysapi.constants.Constants.DEVICE_NOT_FOUND_ERROR_MESSAGE;

@Slf4j
@AllArgsConstructor
@Service
public class DeviceService {

    private DeviceRepository deviceRepository;
    private DiscoveryService discoveryService;
    private GatewayService gatewayService;

    public Device getDeviceByUuid(String deviceUuid) {
        Optional<Device> result = Optional.ofNullable(deviceRepository.findDeviceByDevicesUuidEquals(deviceUuid));
        if(result.isPresent())
            return result.get();
        throw new EntityNotFoundException(MessageFormat.format(DEVICE_NOT_FOUND_ERROR_MESSAGE, deviceUuid));
    }

    public Device getDevice(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUuid, String deviceUuid) {
        Device device = getDeviceByUuid(deviceUuid);
        discoveryService.triggerGetDeviceEntityDiscovery(uriBuilder, response, gatewayUuid);
        return device;
    }

    public Device createDevice(UriComponentsBuilder uriBuilder, HttpServletResponse response, String gatewayUuid, Device device) {
        device.setGateway(gatewayService.getGateway(gatewayUuid));
        Device createdDevice = deviceRepository.saveAndFlush(device);
        discoveryService.triggerSaveDeviceDiscovery(uriBuilder, response, createdDevice.getDevicesUuid(), gatewayUuid);
        return createdDevice;
    }

    public Device updateDevice(UriComponentsBuilder uriBuilder, HttpServletResponse response, String deviceUuid, String gatewayUuid, Device newDevice) {
        Device oldDevice = getDeviceByUuid(deviceUuid);
        Device device = Device.builder()
                .id(oldDevice.getId())
                .devicesUuid(newDevice.getDevicesUuid())
                .deviceCreationDate(newDevice.getDeviceCreationDate())
                .devicesName(newDevice.getDevicesName())
                .status(newDevice.getStatus())
                .vendor(newDevice.getVendor())
                .gateway(oldDevice.getGateway()).build();
        Device savedDevice = deviceRepository.saveAndFlush(device);
        discoveryService.triggerSaveDeviceDiscovery(uriBuilder, response, savedDevice.getDevicesUuid(), gatewayUuid);
        return savedDevice;
    }

    public void deleteDevice(UriComponentsBuilder uriBuilder, HttpServletResponse response, String deviceUuid, String gatewayUuid) {
        Device device = getDeviceByUuid(deviceUuid);
        deviceRepository.delete(device);
        discoveryService.triggerDeleteDeviceEntityDiscovery(uriBuilder, response, gatewayUuid);
        deviceRepository.flush();
    }
}
