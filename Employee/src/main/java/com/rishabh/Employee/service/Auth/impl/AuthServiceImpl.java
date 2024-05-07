package com.rishabh.Employee.service.Auth.impl;

import com.rishabh.Employee.service.Auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public Boolean isAuthenticated(String authToken) {
        if (authToken==null)
            return false;
        String apiUrl = "http://localhost:9090/api/validateUser?authToken=" + authToken;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
