package com.rishabh.FileProcessing.controller;


import com.rishabh.FileProcessing.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    @Autowired
    FileProcessingService fileProcessingService;

    @PostMapping("/import")
    public ResponseEntity<?> convertCsvToJson(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a CSV file.");
            }
            String json = fileProcessingService.importFile(file);
            return ResponseEntity.ok().body(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the CSV file.");
        }
    }


    @PostMapping("/export")
    public ResponseEntity<?> convertJsonToCsv(@RequestBody List<Map<String, Object>> jsonDataArray) {
        if (jsonDataArray == null || jsonDataArray.isEmpty()) {
            return ResponseEntity.badRequest().body("JSON array cannot be empty.");
        }
        try {
            String csv = fileProcessingService.exportFile(jsonDataArray);
            return ResponseEntity.ok().body(csv);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the JSON.");
        }
    }
}
