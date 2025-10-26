package com.example.mybatisdemo.service.impl;

import com.example.mybatisdemo.mapper.DeviceMapper;
import com.example.mybatisdemo.pojo.Device;
import com.example.mybatisdemo.service.IDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author coolsunxu
 * @since 2025-10-19
 */
@Service
@Slf4j
@EnableRetry
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {
    @Override
    @Retryable(
            value = { OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void updateDevice(String deviceId) {
        // 先查询获取记录，此时记录包含当前version值
        Device device = this.getById(deviceId);

        log.info("start to update device {}", device.getDeviceId());
        // 修改字段
        device.setDeviceId("fhwejkfhrweiu");

        throw new OptimisticLockingFailureException("测试异常");
        //this.updateById(device);
    }
}
