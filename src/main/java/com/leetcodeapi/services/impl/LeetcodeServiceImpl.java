package com.leetcodeapi.services.impl;

import com.leetcodeapi.utils.AppConstants;
import com.leetcodeapi.dto.leetcodeproblem.Question;
import com.leetcodeapi.dto.leetcodeproblem.Root;
import com.leetcodeapi.services.LeetcodeService;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class LeetcodeServiceImpl implements LeetcodeService {

    private final RestTemplate restTemplate;

    public LeetcodeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean existByuserName(String username) {
        String query = "query getUserProfile($username: String!) { matchedUser(username: $username) { username } }";
        String requestBody = String.format("{\"query\":\"%s\",\"variables\":{\"username\":\"%s\"}}", query, username);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            ResponseEntity<String> responseEntity = restTemplate.exchange(AppConstants.LEETCODE_URL, HttpMethod.POST, requestEntity, String.class);
            String response = responseEntity.getBody();
            assert response != null;
            return !response.contains("errors");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Question getProblemById(Long id) {
        String searchKeywords = String.valueOf(id);
        String query = "query problemsetQuestionList($categorySlug: String, $limit: Int, $skip: Int, $filters: QuestionListFilterInput) { problemsetQuestionList: questionList(categorySlug: $categorySlug limit: $limit skip: $skip filters: $filters) { total: totalNum questions: data { difficulty title titleSlug } } }";
        String variables = String.format("{\"categorySlug\": \"\", \"skip\": 0, \"limit\": 1, \"filters\": { \"searchKeywords\": \"%s\" }}", searchKeywords);
        String requestBody = "{\"query\": \"" + query.replace("\"", "\\\"") + "\", \"variables\": " + variables + "}";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Root> responseEntity = restTemplate.exchange(
                    AppConstants.LEETCODE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Root.class
            );

            if (Objects.requireNonNull(responseEntity.getBody()).getData().getProblemsetQuestionList() ==null){
                return null;
            }else {
                return Objects.requireNonNull(responseEntity.getBody()).getData().getProblemsetQuestionList().getQuestions().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
