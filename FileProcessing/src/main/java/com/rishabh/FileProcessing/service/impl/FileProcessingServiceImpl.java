package com.rishabh.FileProcessing.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.rishabh.FileProcessing.service.FileProcessingService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class FileProcessingServiceImpl implements FileProcessingService {
    @Override
    public String importFile(MultipartFile file) {
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).build()) {
            List<String[]> rows = reader.readAll();

            // Assuming the first row contains column headers
            String[] headers = rows.get(0);
            List<Map<String, String>> data = rows.stream()
                    .skip(1) // Skip headers
                    .map(row -> {
                        Map<String, String> rowData = new java.util.HashMap<>();
                        for (int i = 0; i < headers.length; i++) {
                            rowData.put(headers[i], row[i]);
                        }
                        return rowData;
                    })
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public String exportFile(List<Map<String, Object>> jsonDataArray) {
        Map<String, Object> firstJsonObject = jsonDataArray.get(0);
        List<String> headers = new ArrayList<>(firstJsonObject.keySet());

        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> jsonObject : jsonDataArray) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                Object value = jsonObject.get(header);
                String cellValue = (value != null) ? String.valueOf(value) : "";
                row.add(cellValue);
            }
            rows.add(row);
        }

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", headers)).append("\n");
        for (List<String> row : rows) {
            csv.append(String.join(",", row)).append("\n");
        }
        return csv.toString();
    }
}
