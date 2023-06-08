package com.leetcodeapi.utils;

import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DailyScoreResetTask {

    private final UserRepository userRepository;
    private final UserService userService;

    private final ModelMapper modelMapper;


    public DailyScoreResetTask(UserRepository userRepository, UserService userService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Scheduled(cron = "0 0 0 * * *",zone = "Asia/Calcutta")
    public void resetDailyScores() {
        log.info("Resetting Daily Score");
        List<User> allUser = userRepository.findAll();
        for (User user: allUser){
            user.setDailyPoints(0L);
            userService.updateUser(modelMapper.map(user, UserDto.class),user.getId());
        }
    }

}
