package com.example.demo.service;

import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import com.example.demo.service.SocksBatchService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SocksBatchServiceTest {

    @Mock
    private SocksRepository socksRepository;

    @InjectMocks
    private SocksBatchService socksBatchService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessSocksBatch() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "socks.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createExcelFile()
        );

        socksBatchService.processSocksBatch(file);

        verify(socksRepository, times(1)).saveAll(any());
    }

    private byte[] createExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Socks");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Color");
            header.createCell(1).setCellValue("CottonPart");
            header.createCell(2).setCellValue("Quantity");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Red");
            dataRow.createCell(1).setCellValue(50);
            dataRow.createCell(2).setCellValue(100);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        }
    }
}
