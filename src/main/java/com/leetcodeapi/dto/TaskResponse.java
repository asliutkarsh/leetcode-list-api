package com.leetcodeapi.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private List<TaskDto> tasks;
    private int pageNumber;
    private int pageSize;
    private long totalElement;
    private long totalPages;
    private boolean lastPage;

}
