package com.leetcodeapi.controller;

import com.leetcodeapi.utils.AppConstants;
import com.leetcodeapi.dto.ApiResponse;
import com.leetcodeapi.dto.TaskDto;
import com.leetcodeapi.dto.TaskResponse;
import com.leetcodeapi.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<Long> createTask(@RequestBody TaskDto taskDto, @PathVariable("userId") Long userId){
        Long taskId= taskService.createTask(taskDto,userId);
        return new ResponseEntity<>(taskId, HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    public ResponseEntity<TaskResponse> fetchAllTask(
            @RequestParam(value ="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(value ="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(value = "sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir){
        return ResponseEntity.ok(taskService.getAllTask(pageNumber,pageSize,sortBy,sortDir));
    }

    @GetMapping("/user/{userId}/tasks")
    public ResponseEntity<TaskResponse> fetchTaskByUser(@PathVariable("userId") Long userId,
                                                        @RequestParam(value ="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                        @RequestParam(value ="pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                        @RequestParam(value = "sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
                                                        @RequestParam(value = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir)  {
        TaskResponse taskResponse = taskService.getTaskByUser(userId,pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(taskResponse,HttpStatus.OK);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDto> fetchTaskById(@PathVariable("taskId") Long taskId)  {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable("taskId") Long taskId){
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(new ApiResponse("Task Deleted Successfully",true),HttpStatus.OK);
    }



    @PutMapping("/tasks/{tasksId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("tasksId")Long taskId, @RequestBody TaskDto taskDto){
        TaskDto updatedTask = taskService.updateTask(taskDto,taskId);
        return ResponseEntity.ok(updatedTask);
    }



}
