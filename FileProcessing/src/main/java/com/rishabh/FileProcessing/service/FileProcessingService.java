package com.rishabh.FileProcessing.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface FileProcessingService {
    String importFile(MultipartFile file);

    String exportFile(List<Map<String, Object>> jsonDataArray);
}
