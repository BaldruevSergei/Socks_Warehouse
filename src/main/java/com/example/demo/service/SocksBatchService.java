package com.example.demo.service;

import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocksBatchService {

    private final SocksRepository socksRepository;

    // Метод для обработки файла, загружаемого пользователем
    public void processSocksBatch(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            List<Socks> socksList = parseExcelFile(inputStream); // Парсинг файла
            socksRepository.saveAll(socksList); // Сохранение в базу данных
        } catch (IOException e) {
            throw new IOException("Failed to process the Excel file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred during batch processing: " + e.getMessage(), e);
        }
    }

    // Универсальный метод для парсинга Excel-файлов
    private List<Socks> parseExcelFile(InputStream inputStream) throws IOException {
        List<Socks> socksList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Пропускаем заголовок
                    continue;
                }
                try {
                    Socks socks = new Socks();
                    socks.setColor(row.getCell(0).getStringCellValue());
                    socks.setCottonPart((int) row.getCell(1).getNumericCellValue());
                    socks.setQuantity((int) row.getCell(2).getNumericCellValue());
                    socksList.add(socks);
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + row.getRowNum() + ", " + e.getMessage());
                }
            }
        }
        return socksList;
    }
}
