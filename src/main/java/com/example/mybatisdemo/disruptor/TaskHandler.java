package com.example.mybatisdemo.disruptor;

import com.example.mybatisdemo.constant.ErrorCode;
import com.example.mybatisdemo.constant.Status;
import com.example.mybatisdemo.pojo.CommonTask;
import com.example.mybatisdemo.service.CommonTaskService;
import com.example.mybatisdemo.threadpool.TimingThreadPool;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author sunxu
 */

@Slf4j
public class TaskHandler implements EventHandler<TaskQueueItem> {

    private final TimingThreadPool fastTaskExecutor;

    private final TimingThreadPool slowTaskExecutor;

    private final CommonTaskService commonTaskService;

    public TaskHandler(
            TimingThreadPool fastTaskExecutor,
            TimingThreadPool slowTaskExecutor,
            CommonTaskService commonTaskService
    ) {
        this.fastTaskExecutor = fastTaskExecutor;
        this.slowTaskExecutor = slowTaskExecutor;
        this.commonTaskService = commonTaskService;
    }

    @Override
    public void onEvent(TaskQueueItem taskQueueItem, long l, boolean b) throws Exception {

        switch (taskQueueItem.getBizType()) {
            case 0:
                handleSyncTask(taskQueueItem);
                break;
            case 1:
                handleAsyncTask(taskQueueItem);
                break;
            default:
        }
    }

    private void handleSyncTask(TaskQueueItem taskQueueItem) {
        try {
            slowTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    // 捕获异常，防止线程池中断
                    try {
                        // 判断任务是否过期
                        if ((System.currentTimeMillis() - taskQueueItem.getCreateTime()) / 1000 > taskQueueItem.getExpiredTime()) {
                            // 返回错误码
                            taskQueueItem.getCf().complete(ErrorCode.TASK_EXPIRED);
                            return;
                        }

                        // 任务处理部分
                        log.info("device {} execute task {}", taskQueueItem.getDeviceId(), taskQueueItem.getContent());
                        taskQueueItem.getCf().complete(ErrorCode.SUCCESS);
                    } catch (Exception e) {
                        log.warn("device {} execute task {} failed", taskQueueItem.getDeviceId(), taskQueueItem.getBusId(), e);
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            log.warn("device {} execute task with thread pool reject", taskQueueItem.getDeviceId());
            taskQueueItem.getCf().complete(ErrorCode.THREAD_POOL_REJECT);
        }

        //其他异常会由service层捕获
    }

    private void handleAsyncTask(TaskQueueItem taskQueueItem) {
        try {
            fastTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    // 捕获异常，防止线程池中断
                    try {
                        // 判断任务是否过期
                        if ((System.currentTimeMillis() - taskQueueItem.getCreateTime()) / 1000 > taskQueueItem.getExpiredTime()) {
                            // 更新数据库
                            commonTaskService.updateTask(CommonTask.builder()
                                    .busId(taskQueueItem.getBusId())
                                    .errorCode(ErrorCode.TASK_EXPIRED)
                                    .status(Status.FAILED.getValue())
                                    .completeTime(System.currentTimeMillis())
                                    .build());
                            return;
                        }

                        // 更新任务开始执行时间
                        commonTaskService.updateTask(CommonTask.builder()
                                .busId(taskQueueItem.getBusId())
                                .scheduleTime(System.currentTimeMillis())
                                .build());

                        // 任务处理部分
                        log.info("device {} execute task {}", taskQueueItem.getDeviceId(), taskQueueItem.getContent());

                        // 更新数据库中的状态
                        commonTaskService.updateTask(CommonTask.builder()
                                .busId(taskQueueItem.getBusId())
                                .errorCode(ErrorCode.SUCCESS)
                                .status(Status.SUCCEED.getValue())
                                .completeTime(System.currentTimeMillis())
                                .build());
                    } catch (Exception e) {
                        log.warn("device {} execute task {} failed", taskQueueItem.getDeviceId(), taskQueueItem.getBusId());
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            log.warn("device {} execute task with thread pool reject", taskQueueItem.getDeviceId());
            // 更新任务状态
            commonTaskService.updateTask(CommonTask.builder()
                    .busId(taskQueueItem.getBusId())
                    .errorCode(ErrorCode.THREAD_POOL_REJECT)
                    .status(Status.FAILED.getValue())
                    .completeTime(System.currentTimeMillis())
                    .build());
        }

    }
}
