package com.rishabh.Employee.service.File.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.rishabh.Employee.service.File.FileProcessingService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {
    private final Gson gson = new Gson();

    @Override
    public JsonArray importFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File is null");
        }
        String apiUrl = "http://localhost:9091/import"; // Your API endpoint URL
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);


            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return gson.fromJson(response.getBody(), JsonArray.class);
            } else throw new Exception("File processing API failed");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String exportFile(String json) {
        String apiUrl = "http://localhost:9091/export"; // Your API endpoint URL
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("File processing API failed");
    }

}
