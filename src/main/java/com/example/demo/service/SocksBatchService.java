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
                // Пропускаем заголовок или пустые строки
                if (row == null || row.getRowNum() == 0) {
                    continue;
                }

                try {
                    // Читаем данные из ячеек
                    String color = row.getCell(0).getStringCellValue(); // Цвет
                    int cottonPart = (int) row.getCell(1).getNumericCellValue(); // Содержание хлопка
                    int quantity = (int) row.getCell(2).getNumericCellValue(); // Количество

                    // Создаем объект Socks и добавляем его в список
                    Socks socks = new Socks();
                    socks.setColor(color);
                    socks.setCottonPart(cottonPart);
                    socks.setQuantity(quantity);
                    socksList.add(socks);
                } catch (Exception e) {
                    // Добавляем сообщение об ошибке
                    errorMessages.add("Ошибка в строке " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        // Логируем все ошибки, если они есть
        if (!errorMessages.isEmpty()) {
            for (String errorMessage : errorMessages) {
                System.err.println(errorMessage);
            }
        }

        return socksList;
    }
}
