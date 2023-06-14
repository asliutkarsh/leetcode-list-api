package com.leetcodeapi.utils;

import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.entities.UserDailyScore;
import com.leetcodeapi.repository.UserDailyScoreRepository;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DailyScoreResetTask {
    private final UserDailyScoreRepository userDailyScoreRepository;

    private final UserRepository userRepository;
    private final UserService userService;

    private final ModelMapper modelMapper;


    public DailyScoreResetTask(UserRepository userRepository, UserService userService, ModelMapper modelMapper,
                               UserDailyScoreRepository userDailyScoreRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.userDailyScoreRepository = userDailyScoreRepository;
    }

    @Scheduled(cron = "0 0 0 * * *",zone = "Asia/Calcutta")
    public void resetDailyScores() {
        log.info("Resetting Daily Score on "+ new Date());
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<User> allUser = userRepository.findAll();
        for (User user: allUser){
            userDailyScoreRepository.save(new UserDailyScore(user,yesterday,user.getDailyPoints()));
            user.setDailyPoints(0L);
            userService.updateUser(modelMapper.map(user, UserDto.class),user.getId());
        }
    }

}
