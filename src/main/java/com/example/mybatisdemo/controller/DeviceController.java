package com.example.mybatisdemo.controller;


import com.example.mybatisdemo.dto.DeviceDTO;
import com.example.mybatisdemo.pojo.Device;
import com.example.mybatisdemo.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 设备表 前端控制器
 * </p>
 *
 * @author coolsunxu
 * @since 2025-10-19
 */
@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    private final IDeviceService iDeviceService;

    public DeviceController(IDeviceService iDeviceService) {
        this.iDeviceService = iDeviceService;
    }

    @PostMapping("/add")
    public boolean add(@RequestBody @Validated DeviceDTO deviceDTO) {
        Device device = new Device();
        device.setDeviceId(deviceDTO.getDeviceId());
        device.setOnline(deviceDTO.getOnline());
        return iDeviceService.save(device);
    }

    @PostMapping("/update")
    public Device update(@Validated @RequestBody DeviceDTO deviceDTO) {

        // 执行更新
        iDeviceService.updateDevice(deviceDTO.getDeviceId());

        return null;

    }
}
