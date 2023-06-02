package com.leetcodeapi.services;

import com.leetcodeapi.dto.leetcodeproblem.Question;

public interface LeetcodeService {

    boolean existByuserName(String username);

    Question getProblemById(Long id);
}
