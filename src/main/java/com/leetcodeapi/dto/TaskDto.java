package com.leetcodeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private Long id;
    private Long problemId;
    private String title;
    private String difficulty;
    private String link;
    private Long points;
    private Timestamp timestamp;
    private String notes;
//    private UserDto user;

}
