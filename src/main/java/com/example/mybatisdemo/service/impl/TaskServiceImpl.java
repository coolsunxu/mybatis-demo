package com.example.mybatisdemo.service.impl;

import com.example.mybatisdemo.constant.Status;
import com.example.mybatisdemo.disruptor.DisruptorManager;
import com.example.mybatisdemo.disruptor.TaskQueueItem;
import com.example.mybatisdemo.dto.TaskDTO;
import com.example.mybatisdemo.exception.BusinessException;
import com.example.mybatisdemo.helper.IdGenerateHelper;
import com.example.mybatisdemo.pojo.CommonTask;
import com.example.mybatisdemo.service.CommonTaskService;
import com.example.mybatisdemo.service.TaskService;
import com.example.mybatisdemo.service.TimeWheelService;
import com.example.mybatisdemo.task.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.mybatisdemo.constant.ErrorCode.*;


/**
 * @author sunxu
 */

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private final DisruptorManager disruptorManager;

    private final CommonTaskService commonTaskService;

    private final IdGenerateHelper idGenerateHelper;

    private final TimeWheelService timeWheelService;

    public TaskServiceImpl(
            DisruptorManager disruptorManager,
            CommonTaskService commonTaskService,
            IdGenerateHelper idGenerateHelper,
            TimeWheelService timeWheelService
    ) {
        this.disruptorManager = disruptorManager;
        this.commonTaskService = commonTaskService;
        this.idGenerateHelper = idGenerateHelper;
        this.timeWheelService = timeWheelService;
    }

    @Override
    public int addAsyncTask(TaskDTO taskDTO) {
        int errorCode = SUCCESS;
        String deviceId = taskDTO.getDeviceId();
        Long busId = idGenerateHelper.nextId();

        TaskQueueItem taskQueueItem = TaskQueueItem.builder()
                .busId(busId)
                .name(taskDTO.getName())
                .bizType(taskDTO.getBizType())
                .content(taskDTO.getContent())
                .createTime(System.currentTimeMillis())
                .expiredTime(taskDTO.getExpiredTime())
                .deviceId(taskDTO.getDeviceId())
                .source(taskDTO.getSource())
                .status(Status.PENDING.getValue())
                .createUserId(taskDTO.getCreateUserId())
                .build();

        try {
            // 保存任务到数据库
            commonTaskService.addTask(taskQueueItem);

            // 添加任务到队列，可能出现队列已满的情况
            disruptorManager.addTask(taskQueueItem);

            // 插入任务到延时队列，插入失败则更新数据库，并返回
            timeWheelService.add(new DelayTask(commonTaskService, busId, taskDTO.getExpiredTime()));

        } catch (BusinessException e) {
            log.warn("device {} handle task error", deviceId, e);
            errorCode = e.getCode();
        } catch (Exception e) {
            errorCode = UNKNOWN_ERROR;
            log.warn("device {} handle task with unknown error", deviceId, e);
        }

        // 任务是否成功 状态是异步更新的
        if (errorCode != 0) {
            updateTask(errorCode, busId);
        }

        return 0;
    }

    @Override
    public int addSyncTask(TaskDTO taskDTO) {

        int errorCode = SUCCESS;
        String deviceId = taskDTO.getDeviceId();
        Long busId = idGenerateHelper.nextId();
        CompletableFuture<Integer> cf = new CompletableFuture<>();

        TaskQueueItem taskQueueItem = TaskQueueItem.builder()
                .busId(busId)
                .name(taskDTO.getName())
                .bizType(taskDTO.getBizType())
                .content(taskDTO.getContent())
                .createTime(System.currentTimeMillis())
                .expiredTime(taskDTO.getExpiredTime())
                .deviceId(taskDTO.getDeviceId())
                .source(taskDTO.getSource())
                .status(Status.PENDING.getValue())
                .createUserId(taskDTO.getCreateUserId())
                .cf(cf)
                .build();

        try {
            // 保存任务到数据库
            commonTaskService.addTask(taskQueueItem);

            // 添加任务到队列，可能出现队列已满的情况
            disruptorManager.addTask(taskQueueItem);

            // 同步等待结果
            errorCode = cf.get(taskDTO.getExpiredTime(), TimeUnit.SECONDS);

        } catch (BusinessException e) {
            log.warn("device {} handle task error", deviceId, e);
            errorCode = e.getCode();
        } catch (TimeoutException e) {
            errorCode = TIME_OUT;
            log.warn("device {} handle task timeout", deviceId);
        } catch (Exception e) {
            errorCode = UNKNOWN_ERROR;
            log.warn("device {} handle task with unknown error", deviceId, e);
        } finally {
            updateTask(errorCode, busId);
        }

        return errorCode;
    }

    private void updateTask(int errorCode, Long busId) {
        Short status;
        if (errorCode == SUCCESS) {
            status = Status.SUCCEED.getValue();
        } else {
            status = Status.FAILED.getValue();
        }

        commonTaskService.updateTask(CommonTask.builder()
                .busId(busId)
                .errorCode(errorCode)
                .completeTime(System.currentTimeMillis())
                .status(status)
                .build()
        );
    }
}
