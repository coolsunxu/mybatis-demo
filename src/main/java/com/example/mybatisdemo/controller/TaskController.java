package com.example.mybatisdemo.controller;

import com.example.mybatisdemo.dto.TaskDTO;
import com.example.mybatisdemo.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunxu
 */


@RestController
@Slf4j
public class TaskController {


    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/add")
    public int add(@RequestBody TaskDTO taskDTO) {
        if (taskDTO.getBizType() == 0) {
            return taskService.addSyncTask(taskDTO);
        } else {
            return taskService.addAsyncTask(taskDTO);
        }
    }
}
