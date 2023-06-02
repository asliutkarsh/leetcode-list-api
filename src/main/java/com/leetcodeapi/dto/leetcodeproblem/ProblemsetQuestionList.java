package com.leetcodeapi.dto.leetcodeproblem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemsetQuestionList {
    private int total;
    private ArrayList<Question> questions;
}
