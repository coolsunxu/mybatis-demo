package com.example.mybatisdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mybatisdemo.pojo.Device;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 设备表 服务类
 * </p>
 *
 * @author coolsunxu
 * @since 2025-10-19
 */

@Transactional
public interface IDeviceService extends IService<Device> {
    void updateDevice(String deviceId);
}
