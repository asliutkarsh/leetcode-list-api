package com.leetcodeapi.services;

import com.leetcodeapi.dto.TaskDto;
import com.leetcodeapi.dto.TaskResponse;

public interface TaskService {

    Long createTask(TaskDto taskDto,Long userId);
    TaskDto updateTask(TaskDto taskDto,Long taskId);
    TaskResponse getAllTask(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    TaskDto getTaskById(Long taskId);
    void deleteTask(Long taskId);
    TaskResponse getTaskByUser(Long userId,Integer pageNumber, Integer pageSize,String sortBy,String sortDir);

}
