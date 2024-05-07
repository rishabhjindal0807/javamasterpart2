package com.rishabh.Employee.service.File;

import com.google.gson.JsonArray;
import org.springframework.web.multipart.MultipartFile;


public interface FileProcessingService {

    JsonArray importFile(MultipartFile file);

    String exportFile(String json);
}
