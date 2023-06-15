package com.leetcodeapi.services.impl;

import com.leetcodeapi.entities.UserDailyScore;
import com.leetcodeapi.exception.DuplicateEntryException;
import com.leetcodeapi.repository.UserDailyScoreRepository;
import com.leetcodeapi.utils.AppConstants;
import com.leetcodeapi.dto.TaskDto;
import com.leetcodeapi.dto.TaskResponse;
import com.leetcodeapi.dto.leetcodeproblem.Question;
import com.leetcodeapi.entities.Task;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.exception.ProblemNotFoundException;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.repository.TaskRepository;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.LeetcodeService;
import com.leetcodeapi.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final LeetcodeService leetcodeService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserDailyScoreRepository userDailyScoreRepository;

    public TaskServiceImpl(TaskRepository taskRepository, LeetcodeService leetcodeService, ModelMapper modelMapper, UserRepository userRepository,
                           UserDailyScoreRepository userDailyScoreRepository) {
        this.taskRepository = taskRepository;
        this.leetcodeService = leetcodeService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userDailyScoreRepository = userDailyScoreRepository;
    }

    @Override
    public Long createTask(TaskDto taskDto,Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","id",userId));
        if (taskRepository.existsByProblemId(taskDto.getProblemId())){
            throw new DuplicateEntryException("Task already exists");
        }
        Task task = modelMapper.map(taskDto,Task.class);
        Question  question = leetcodeService.getProblemById(taskDto.getProblemId());
        if (question==null){
            throw new ProblemNotFoundException();
        }

        task.setTitle(question.getTitle());
        task.setDifficulty(question.getDifficulty());
        task.setLink(AppConstants.LEETCODE_PROBLEM_LINK+question.getTitleSlug());

        Long points = 0L;
        switch (question.getDifficulty()) {
            case AppConstants.LEETCODE_DIFFICULTY_EASY -> points = AppConstants.LEETCODE_DIFFICULTY_EASY_POINTS;
            case AppConstants.LEETCODE_DIFFICULTY_MEDIUM -> points = AppConstants.LEETCODE_DIFFICULTY_MEDIUM_POINTS;
            case AppConstants.LEETCODE_DIFFICULTY_HARD -> points = AppConstants.LEETCODE_DIFFICULTY_HARD_POINTS;
        }

        task.setPoints(points);
        addPoints(points,user);
        task.setTimestamp(new Timestamp(new Date().getTime()));
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return savedTask.getId();
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("Task","id",taskId));
        task.setNotes(taskDto.getNotes());
        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDto.class);
    }

    @Override
    public TaskResponse getAllTask(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc"))?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber,pageSize,sort);
        Page<Task> taskPage = taskRepository.findAll(p);
        return getTaskResponse(taskPage);
    }

    private TaskResponse getTaskResponse(Page<Task> taskPage) {
        List<Task> taskList =taskPage.getContent();
        List<TaskDto> taskDtoList = taskList.stream().map(((task) -> modelMapper.map(task, TaskDto.class))).toList();
        return new TaskResponse(
                taskDtoList,taskPage.getNumber(),taskPage.getSize(),taskPage.getNumberOfElements(),taskPage.getTotalPages(),taskPage.isLast());
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("Task","id",taskId));
        return modelMapper.map(task,TaskDto.class);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("Task","id",taskId));
        deletePoints(task,task.getUser());
        taskRepository.delete(task);
    }

    @Override
    public TaskResponse getTaskByUser(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","id",userId));
        Sort sort = (sortDir.equalsIgnoreCase("asc"))?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(pageNumber,pageSize,sort);
        Page<Task> taskPage =  taskRepository.findTasksByUser(user,p);
        return getTaskResponse(taskPage);
    }

    private void addPoints(long points,User user){
        user.setDailyPoints(user.getDailyPoints()+points);
        user.setTotalPoints(user.getTotalPoints()+points);
        userRepository.save(user);
    }

    private void deletePoints(Task task,User user){
        UserDailyScore userDailyScore = userDailyScoreRepository.findByUser_IdAndDate(
                        user.getId(), task.getTimestamp().toLocalDateTime().toLocalDate()
                        ).orElse(null);
        if (userDailyScore!=null){
            userDailyScore.setScore(userDailyScore.getScore()-task.getPoints());
            userDailyScoreRepository.save(userDailyScore);
        }
        user.setDailyPoints(user.getDailyPoints()-task.getPoints());
        user.setTotalPoints(user.getTotalPoints()-task.getPoints());
        userRepository.save(user);
    }


}
