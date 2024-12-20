package com.example.demo.service;

import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocksBatchService {

    private static final Logger logger = LoggerFactory.getLogger(SocksBatchService.class);
    private final SocksRepository socksRepository;

    // Метод для обработки файла из папки resources
    public void processSocksBatchFromResources() throws IOException {
        ClassPathResource resource = new ClassPathResource("socks_large.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            List<Socks> socksList = parseExcelFile(inputStream);
            socksRepository.saveAll(socksList);
        }
    }

    // Метод для обработки файла, загружаемого пользователем
    public void processSocksBatch(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            List<Socks> socksList = parseExcelFile(inputStream);
            socksRepository.saveAll(socksList);
        }
    }

    // Универсальный метод для парсинга Excel-файлов
    private List<Socks> parseExcelFile(InputStream inputStream) throws IOException {
        List<Socks> socksList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0 || row == null) { // Пропускаем заголовок или пустые строки
                    continue;
                }

                try {
                    String color = row.getCell(0).getStringCellValue();
                    int cottonPart = (int) row.getCell(1).getNumericCellValue();
                    int quantity = (int) row.getCell(2).getNumericCellValue();

                    Socks socks = new Socks();
                    socks.setColor(color);
                    socks.setCottonPart(cottonPart);
                    socks.setQuantity(quantity);
                    socksList.add(socks);
                } catch (Exception e) {
                    errorMessages.add("Error in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            for (String errorMessage : errorMessages) {
                logger.warn(errorMessage);
            }
        }

        return socksList;
    }
}
