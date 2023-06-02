package com.leetcodeapi.dto.leetcodeproblem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String difficulty;
    private String title;
    private String titleSlug;
}
