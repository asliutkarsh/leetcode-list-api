package com.leetcodeapi.services;

import com.leetcodeapi.dto.TaskDto;
import com.leetcodeapi.dto.TaskResponse;
import com.leetcodeapi.dto.leetcodeproblem.Question;
import com.leetcodeapi.entities.Task;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.entities.UserDailyScore;
import com.leetcodeapi.exception.DuplicateEntryException;
import com.leetcodeapi.exception.ProblemNotFoundException;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.repository.TaskRepository;
import com.leetcodeapi.repository.UserDailyScoreRepository;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.impl.TaskServiceImpl;
import com.leetcodeapi.services.impl.UserServiceImpl;
import com.leetcodeapi.utils.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl underTestService;

    @Mock private UserRepository userRepository;
    @Mock private LeetcodeService leetcodeService;
    @Mock private TaskRepository taskRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private UserDailyScoreRepository userDailyScoreRepository;


    @Test
    void createTask_ValidTaskDto_ReturnsTaskId() {
        //Given
        Long userId = 1L;
        TaskDto taskDto = new TaskDto();
        taskDto.setProblemId(123L);

        Task task = new Task();
        task.setProblemId(taskDto.getProblemId());

        User user = new User();
        user.setId(userId);
        user.setTotalPoints(0L);
        user.setDailyPoints(0L);

        Question question = new Question();
        question.setDifficulty("Easy");
        question.setTitle("Two Sum");
        question.setTitleSlug("two-sum");

        Task expectedTask = new Task();
        expectedTask.setProblemId(taskDto.getProblemId());
        expectedTask.setDifficulty(question.getDifficulty());
        expectedTask.setTitle(question.getTitle());
        expectedTask.setLink(AppConstants.LEETCODE_PROBLEM_LINK+question.getTitleSlug());
        expectedTask.setUser(user);
        expectedTask.setPoints(100L);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.existsByProblemId(taskDto.getProblemId())).thenReturn(false);
        when(leetcodeService.getProblemById(taskDto.getProblemId())).thenReturn(question);
        when(modelMapper.map(taskDto, Task.class)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

        //When
        Long actualTaskId = underTestService.createTask(taskDto, userId);

        //Then
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();

        assertThat(actualTaskId).isEqualTo(expectedTask.getId());
        assertThat(capturedTask.getId()).isEqualTo(expectedTask.getId());
        assertThat(capturedTask.getProblemId()).isEqualTo(expectedTask.getProblemId());
        assertThat(capturedTask.getDifficulty()).isEqualTo(expectedTask.getDifficulty());
        assertThat(capturedTask.getTitle()).isEqualTo(expectedTask.getTitle());
        assertThat(capturedTask.getLink()).isEqualTo(expectedTask.getLink());
        assertThat(capturedTask.getUser()).isEqualTo(expectedTask.getUser());
        assertThat(capturedTask.getPoints()).isEqualTo(expectedTask.getPoints());

    }

    @Test
    void createTask_DuplicateTask_ThrowsDuplicateEntryException() {
        //Given
        Long userId = 1L;

        TaskDto taskDto = new TaskDto();
        taskDto.setProblemId(123L);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.existsByProblemId(taskDto.getProblemId())).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTestService.createTask(taskDto, userId))
                .isInstanceOf(DuplicateEntryException.class);
        //Then
        verify(taskRepository, never()).save(any(Task.class));
        verify(userRepository, never()).save(user);
    }

    @Test
    void createTask_InvalidProblemId_ThrowsProblemNotFoundException() {
        Long userId = 1L;

        TaskDto taskDto = new TaskDto();
        taskDto.setProblemId(123L);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.existsByProblemId(taskDto.getProblemId())).thenReturn(false);
        when(leetcodeService.getProblemById(taskDto.getProblemId())).thenReturn(null);

        assertThatThrownBy(() -> underTestService.createTask(taskDto, userId))
                .isInstanceOf(ProblemNotFoundException.class);
        verify(taskRepository, never()).save(any(Task.class));
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateTask_ValidTaskDto_ReturnsUpdatedTaskDto() {
        //Given
        Long taskId = 1L;

        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setNotes("Updated notes");

        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        //When
        TaskDto result = underTestService.updateTask(taskDto, taskId);

        //Then
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();

        assertThat(capturedTask.getId()).isEqualTo(taskDto.getId());
        assertThat(capturedTask.getNotes()).isEqualTo(taskDto.getNotes());
    }

    @Test
    void updateTask_InvalidTaskId_ThrowsResourceNotFoundException() {
        Long taskId = 1L;

        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setNotes("Updated notes");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.updateTask(taskDto, taskId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).save(any(Task.class));
        verify(modelMapper, never()).map(any(Task.class), eq(TaskDto.class));
    }
    @Test
    void getAllTask() {
        //Given
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "title";
        String sortDir = "asc";

        Long taskId1 = 1L;
        Long problemId1 = 1L;
        String title1 = "Two Sum";
        String difficulty1 = "Easy";
        String link1 = "https://leetcode.com/problems/two-sum/";
        Long points1 = 100L;
        String notes1 = "Notes";
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        User user1 = new User("test", "test", "test");

        Long taskId2 = 2L;
        Long problemId2 = 2L;
        String title2 = "Add Two Numbers";
        String difficulty2 = "Medium";
        String link2 = "https://leetcode.com/problems/add-two-numbers/";
        Long points2 = 300L;
        String notes2 = "Notes";
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        User user2 = new User("test", "test", "test");


        Task task1 = new Task(taskId1, problemId1,title1, difficulty1, link1, points1,timestamp1, notes1,user1);
        TaskDto taskDto1 = new TaskDto(taskId1,problemId1,title1, difficulty1, link1, points1,timestamp1, notes1);

        Task task2 = new Task(taskId2, problemId2,title2, difficulty2, link2, points2,timestamp2, notes2,user2);
        TaskDto taskDto2 = new TaskDto(taskId2,problemId2,title2, difficulty2, link2, points2,timestamp2, notes2);

        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        Page<Task> taskPage = new PageImpl<>(taskList);

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(task1, TaskDto.class)).thenReturn(taskDto1);
        when(modelMapper.map(task2, TaskDto.class)).thenReturn(taskDto2);

        //When
        TaskResponse result = underTestService.getAllTask(pageNumber, pageSize, sortBy, sortDir);

        //Then
        assertThat(result.getTasks().size()).isEqualTo(2);
        assertThat(result.getTasks().get(0)).isEqualTo(taskDto1);
        assertThat(result.getTasks().get(1)).isEqualTo(taskDto2);

    }

    @Test
    void getTaskById_ExistingTaskId_ReturnsTaskDto() {
        Long taskId = 1L;

        Task task = new Task();
        task.setId(taskId);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);

        TaskDto result = underTestService.getTaskById(taskId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(taskDto);
        verify(taskRepository,times(1)).findById(taskId);
    }

    @Test
    void getTaskById_NonExistingTaskId_ThrowsResourceNotFoundException() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.getTaskById(taskId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).save(any(Task.class));
        verify(modelMapper, never()).map(any(Task.class), eq(TaskDto.class));
    }

    @Test
    void deleteTask_ExistingTaskId_DeletesTaskAndUpdatesPoints() {
        //Given
        Long taskId = 1L;
        Long userId = 1L;
        Long points = 10L;

        User user = new User();
        user.setId(userId);
        user.setDailyPoints(100L);
        user.setTotalPoints(200L);

        Task task = new Task();
        task.setId(taskId);
        task.setUser(user);
        task.setPoints(points);
        task.setTimestamp(new Timestamp(System.currentTimeMillis()));

        UserDailyScore userDailyScore = new UserDailyScore();
        userDailyScore.setUser(user);
        userDailyScore.setScore(points);
        userDailyScore.setDate(LocalDate.now());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userDailyScoreRepository.findByUser_IdAndDate(userId, LocalDate.now())).thenReturn(Optional.of(userDailyScore));
        //When
        underTestService.deleteTask(taskId);

        //Then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(task);
        verify(userDailyScoreRepository, times(1)).findByUser_IdAndDate(userId, LocalDate.now());
        verify(userDailyScoreRepository, times(1)).save(userDailyScore);
        verify(userRepository, times(1)).save(user);

        assertThat(user.getDailyPoints()).isEqualTo(90L);
        assertThat(user.getTotalPoints()).isEqualTo(190L);
    }

    @Test
    void deleteTask_NonExistingTaskId_ThrowsResourceNotFoundException() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> underTestService.deleteTask(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(0)).delete(any(Task.class));
        verify(userDailyScoreRepository, times(0)).findByUser_IdAndDate(anyLong(), any(LocalDate.class));
        verify(userDailyScoreRepository, times(0)).save(any(UserDailyScore.class));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void getTaskByUser_ValidUserIdAndPageable_ReturnsTaskResponse() {
        Long userId = 1L;
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "title";
        String sortDir = "asc";

        Long taskId1 = 1L;
        Long problemId1 = 1L;
        String title1 = "Two Sum";
        String difficulty1 = "Easy";
        String link1 = "https://leetcode.com/problems/two-sum/";
        Long points1 = 100L;
        String notes1 = "Notes";
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        User user1 = new User("test", "test", "test");
        user1.setId(userId);

        Long taskId2 = 2L;
        Long problemId2 = 2L;
        String title2 = "Add Two Numbers";
        String difficulty2 = "Medium";
        String link2 = "https://leetcode.com/problems/add-two-numbers/";
        Long points2 = 300L;
        String notes2 = "Notes";
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        User user2 = new User("test", "test", "test");
        user1.setId(2L);

        Task task1 = new Task(taskId1, problemId1,title1, difficulty1, link1, points1,timestamp1, notes1,user1);
        TaskDto taskDto1 = new TaskDto(taskId1,problemId1,title1, difficulty1, link1, points1,timestamp1, notes1);

        Task task2 = new Task(taskId2, problemId2,title2, difficulty2, link2, points2,timestamp2, notes2,user2);
        TaskDto taskDto2 = new TaskDto(taskId2,problemId2,title2, difficulty2, link2, points2,timestamp2, notes2);


        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);
        Page<Task> taskPage = new PageImpl<>(taskList);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(taskRepository.findTasksByUser(user1, PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending())))
                .thenReturn(taskPage);
        when(modelMapper.map(task1, TaskDto.class)).thenReturn(taskDto1);

        TaskResponse result = underTestService.getTaskByUser(userId, pageNumber, pageSize, sortBy, sortDir);

        assertNotNull(result);
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(taskList.size(), result.getTotalElement());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLastPage());
        assertEquals(taskList.size(), result.getTasks().size());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).findTasksByUser(user1, PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending()));
        verify(modelMapper, times(taskList.size())).map(any(Task.class), eq(TaskDto.class));
    }
}