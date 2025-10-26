package com.example.mybatisdemo.service;

import com.example.mybatisdemo.disruptor.TaskQueueItem;
import com.example.mybatisdemo.pojo.CommonTask;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author sunxu
 */
public interface CommonTaskService {


    /**
     * 添加task到数据库
     *
     * @param taskQueueItem 参数
     */
    void addTask(TaskQueueItem taskQueueItem);


    /**
     * 更新任务状态
     * @param commonTask 参数
     * @return 返回更新结果
     */
    int updateTask(CommonTask commonTask);

    /**
     * 用于定时任务扫表，判断是否有待执行的任务因为pod宕机，没有及时更新状态
     * @param status 任务状态
     * @return 返回查询到的数据
     */
    List<CommonTask> getTaskByPendingStatus(Short status);

    /**
     * 判断任务到期 任务状态是否得到更新
     * @param busId 任务id
     * @return 返回信息
     */
    CommonTask getDelayTaskByBusId(Long busId);
}
