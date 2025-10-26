package com.example.mybatisdemo.task;

import com.example.mybatisdemo.constant.ErrorCode;
import com.example.mybatisdemo.constant.Status;
import com.example.mybatisdemo.disruptor.TaskQueueItem;
import com.example.mybatisdemo.pojo.CommonTask;
import com.example.mybatisdemo.service.CommonTaskService;
import com.example.mybatisdemo.timeWheel.TimerTask;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sunxu
 */

@Slf4j
public class DelayTask extends TimerTask {


    private final CommonTaskService commonTaskService;
    private final Long busId;

    public DelayTask(
            CommonTaskService commonTaskService,
            Long busId,
            int expiredTime
    ) {
        this.commonTaskService = commonTaskService;
        this.busId = busId;
        super.delayMs = expiredTime * 1000L;
    }


    @Override
    public void run() {
        // 判断任务状态是否修改
        CommonTask commonTask = commonTaskService.getDelayTaskByBusId(busId);
        if (commonTask.getStatus().equals(Status.PENDING.getValue()) || commonTask.getStatus().equals(Status.PROCESSING.getValue())) {
            commonTaskService.updateTask(CommonTask.builder()
                    .status(Status.FAILED.getValue())
                    .errorCode(ErrorCode.TASK_EXPIRED)
                    .build());
        }

        log.info("task {} expired", commonTask.getBusId());
    }
}
