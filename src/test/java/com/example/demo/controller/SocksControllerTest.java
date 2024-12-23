package com.example.demo.controller;

import com.example.demo.model.Socks;
import com.example.demo.service.SocksBatchService;
import com.example.demo.service.SocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SocksControllerTest {

    @Mock
    private SocksService socksService;
    @Mock
    private SocksBatchService socksBatchService; // Объявление мока


    @InjectMocks
    private SocksController socksController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(socksController).build();
    }

    @Test
    public void testFilterSocksMoreThan() throws Exception {
        List<Socks> mockSocks = Arrays.asList(
                new Socks(1L, "red", 60, 100),
                new Socks(2L, "red", 80, 200)
        );

        when(socksService.getSocksByColorAndCottonPartGreaterThan(eq("red"), eq(50)))
                .thenReturn(mockSocks);

        mockMvc.perform(get("/api/socks/filter")
                        .param("color", "red")
                        .param("operation", "moreThan")
                        .param("cottonPart", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[0].cottonPart").value(60))
                .andExpect(jsonPath("$[0].quantity").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].color").value("red"))
                .andExpect(jsonPath("$[1].cottonPart").value(80))
                .andExpect(jsonPath("$[1].quantity").value(200));
    }

    @Test
    public void testAddSocks() throws Exception {
        Socks socks = new Socks(null, "blue", 40, 100);

        when(socksService.addSocks(any(Socks.class))).thenReturn(socks);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\": \"blue\", \"cottonPart\": 40, \"quantity\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("blue"))
                .andExpect(jsonPath("$.cottonPart").value(40))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(socksService, times(1)).addSocks(any(Socks.class));
    }

    @Test
    public void testUploadSocksBatch() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "socks.xlsx",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "Test data".getBytes()
        );

        doNothing().when(socksBatchService).processSocksBatch(any(MockMultipartFile.class));

        mockMvc.perform(multipart("/api/socks/batch")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("File processed successfully"));
    }




    @Test
    public void testRemoveSocks() throws Exception {
        doNothing().when(socksService).removeSocks(any(Socks.class));

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"quantity\": 20}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks removed successfully"));

        verify(socksService, times(1)).removeSocks(any(Socks.class));
    }
}
