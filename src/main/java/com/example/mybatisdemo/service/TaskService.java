package com.example.mybatisdemo.service;

import com.example.mybatisdemo.dto.TaskDTO;

/**
 * @author sunxu
 */
public interface TaskService {

    /**
     * 添加异步任务
     * @param taskDTO 任务参数
     * @return 返回添加结果
     */
    int addAsyncTask(TaskDTO taskDTO);

    /**
     * 添加同步任务
     * @param taskDTO 任务参数
     * @return 返回添加结果
     */
    int addSyncTask(TaskDTO taskDTO);

}
