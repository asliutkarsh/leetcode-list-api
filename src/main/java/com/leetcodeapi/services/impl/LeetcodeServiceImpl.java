package com.leetcodeapi.services.impl;

import com.leetcodeapi.constant.AppConstants;
import com.leetcodeapi.services.LeetcodeService;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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


}
