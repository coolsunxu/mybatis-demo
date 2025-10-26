package com.example.mybatisdemo.service.impl;


import com.example.mybatisdemo.disruptor.TaskQueueItem;
import com.example.mybatisdemo.exception.BusinessException;
import com.example.mybatisdemo.mapper.CommonTaskMapper;
import com.example.mybatisdemo.mapstruct.TaskMapper;
import com.example.mybatisdemo.pojo.CommonTask;
import com.example.mybatisdemo.pojo.CommonTaskExample;
import com.example.mybatisdemo.service.CommonTaskService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.mybatisdemo.constant.ErrorCode.SAVE_INTO_DB_ERROR;

/**
 * @author sunxu
 */

@Service
@Slf4j
public class CommonTaskServiceImpl implements CommonTaskService {

    private final CommonTaskMapper commonTaskMapper;

    public CommonTaskServiceImpl(CommonTaskMapper commonTaskMapper) {
        this.commonTaskMapper = commonTaskMapper;
    }

    @Override
    public void addTask(TaskQueueItem taskQueueItem) {
        CommonTask commonTask = TaskMapper.INSTANCE.dto2Po(taskQueueItem);
        try {
            int result = commonTaskMapper.insertSelective(commonTask);
            if (result <= 0) {
                throw new BusinessException(SAVE_INTO_DB_ERROR, "save task into db error");
            }
        } catch (Exception e) {
            log.info("save task error",e);
            throw new BusinessException(SAVE_INTO_DB_ERROR, "save task into db error");
        }
    }


    /**
     * 只需要在taskDTO设置需要更新的字段即可，无需全字段更新
     *
     * @param commonTask 设置需要更新的特定字段
     * @return 返回更新的条目数
     */
    @Override
    public int updateTask(CommonTask commonTask) {
        CommonTaskExample commonTaskExample = new CommonTaskExample();
        CommonTaskExample.Criteria criteria = commonTaskExample.createCriteria();
        criteria.andBusIdEqualTo(commonTask.getBusId());
        return commonTaskMapper.updateByExampleSelective(commonTask, commonTaskExample);
    }

    /**
     * 用于定时任务扫表，判断是否有待执行的任务因为pod宕机，没有及时更新状态
     * @param status 任务状态
     * @return 返回查询到的数据
     */
    @Override
    public List<CommonTask> getTaskByPendingStatus(Short status) {
        return commonTaskMapper.getTaskByPendingStatus(status);
    }

    @Override
    public CommonTask getDelayTaskByBusId(Long busId) {
        return commonTaskMapper.getDelayTaskByBusId(busId);
    }


}
