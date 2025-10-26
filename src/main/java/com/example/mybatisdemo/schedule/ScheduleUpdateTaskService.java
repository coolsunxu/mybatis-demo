package com.example.mybatisdemo.schedule;

import com.example.mybatisdemo.config.props.ScheduleConfig;
import com.example.mybatisdemo.constant.Status;
import com.example.mybatisdemo.mapper.CommonTaskMapper;
import com.example.mybatisdemo.pojo.CommonTask;
import com.example.mybatisdemo.pojo.CommonTaskExample;
import com.example.mybatisdemo.service.CommonTaskService;
import com.example.mybatisdemo.util.ThreadFactoryUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.mybatisdemo.constant.ErrorCode.TIME_OUT;

/**
 * @author : sunxu
 * @description :
 * @createDate : 2025/6/1 10:29
 */

@Service
@Slf4j
public class ScheduleUpdateTaskService {

    private final CommonTaskService commonTaskService;

    private final ScheduleConfig scheduleConfig;

    public ScheduleUpdateTaskService(CommonTaskService commonTaskService, ScheduleConfig scheduleConfig) {
        this.commonTaskService = commonTaskService;
        this.scheduleConfig = scheduleConfig;
    }

    /**
     * 定时任务线程池, 为解决系统宕机时，数据库中的任务得不到及时更新
     */
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(
            1, ThreadFactoryUtil.create("schedule-update-task"), new ThreadPoolExecutor.AbortPolicy());


    @PostConstruct
    public void start() {
        log.info("schedule update task status thread start");

        // 因为这里仅仅作为补偿措施，不需要很准确的及时性
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                // 注意代码放在try catch中，防止异常出现，中断流程
                try {
                    PageInfo<CommonTask> pageInfo = getPendingTasks(0, 100);
                    updateTask(pageInfo);
                } catch (Exception e) {
                    log.warn("update pending task error", e);
                }
            }
        }, 0, scheduleConfig.getCheckInterval(), TimeUnit.SECONDS);
    }

    private PageInfo<CommonTask> getPendingTasks(int pageNum, int pageSize) {

        PageInfo<CommonTask> pageInfo = null;

        try {
            // 开启分页
            PageHelper.startPage(pageNum, pageSize);

            // 查找任务状态为未执行的状态，判断是否超时
            List<CommonTask> commonTaskList = commonTaskService.getTaskByPendingStatus(Status.PENDING.getValue());

            pageInfo = new PageInfo<>(commonTaskList);

            log.info("get pending task, total rows {}, total pages {} and size of each page {} and current page and size {} {}",
                    pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageSize(), pageInfo.getPageNum(), pageInfo.getSize());
        } catch (Exception e) {
            log.warn("get pending task error", e);
        } finally {
            PageHelper.clearPage();
        }

        return pageInfo;
    }

    private void updateTask(PageInfo<CommonTask> pageInfo) {
        if (pageInfo == null) {
            return;
        }

        List<CommonTask> commonTaskList = pageInfo.getList();
        for (CommonTask commonTask : commonTaskList) {
            // 判断是否过期，过期更新状态，如果是异步任务，是否需要通知上游服务任务情况
            if ((System.currentTimeMillis() - commonTask.getCreateTime()) / 1000 > commonTask.getExpiredTime()) {
                commonTaskService.updateTask(CommonTask.builder()
                        .busId(commonTask.getBusId())
                        .completeTime(System.currentTimeMillis())
                        .errorCode(TIME_OUT)
                        .status(Status.FAILED.getValue())
                        .build()
                );
            }
        }
    }

}
