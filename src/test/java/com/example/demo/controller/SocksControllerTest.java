package com.example.demo.controller;

import com.example.demo.SocksWarehouseApplication;
import com.example.demo.model.Socks;
import com.example.demo.service.SocksBatchService;
import com.example.demo.service.SocksService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocksController.class)
@ContextConfiguration(classes = SocksWarehouseApplication.class)
public class SocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocksService socksService;
    @MockBean
    private SocksBatchService socksBatchService;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterIncome() throws Exception {
        Socks socks = new Socks();
        socks.setId(1L);
        socks.setColor("red");
        socks.setCottonPart(50);
        socks.setQuantity(100);

        when(socksService.addSocks(any(Socks.class))).thenReturn(socks);

        mockMvc.perform(post("/api/socks/income")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("red"))
                .andExpect(jsonPath("$.cottonPart").value(50))
                .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    public void testRegisterOutcome() throws Exception {
        Socks socks = new Socks();
        socks.setId(1L);
        socks.setQuantity(50);

        Mockito.doNothing().when(socksService).removeSocks(any(Socks.class));

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socks)))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks removed successfully"));
    }

    @Test
    public void testGetAllSocks() throws Exception {
        Socks socks1 = new Socks();
        socks1.setId(1L);
        socks1.setColor("red");
        socks1.setCottonPart(50);
        socks1.setQuantity(100);

        Socks socks2 = new Socks();
        socks2.setId(2L);
        socks2.setColor("blue");
        socks2.setCottonPart(30);
        socks2.setQuantity(50);

        when(socksService.getAllSocks()).thenReturn(List.of(socks1, socks2));

        mockMvc.perform(get("/api/socks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[1].color").value("blue"));
    }

    @Test
    public void testFilterSocksMoreThan() throws Exception {
        Socks socks1 = new Socks();
        socks1.setId(1L);
        socks1.setColor("red");
        socks1.setCottonPart(60);
        socks1.setQuantity(50);

        Socks socks2 = new Socks();
        socks2.setId(2L);
        socks2.setColor("red");
        socks2.setCottonPart(70);
        socks2.setQuantity(30);

        when(socksService.getFilteredSocks(eq("red"), eq("more_than"), eq(50)))
                .thenReturn(List.of(socks1, socks2));

        mockMvc.perform(get("/api/socks/filter")
                        .param("color", "red")
                        .param("operation", "more_than")
                        .param("cottonPart", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cottonPart").value(60))
                .andExpect(jsonPath("$[1].cottonPart").value(70));
    }

    @Test
    public void testRegisterOutcomeNotEnoughSocks() throws Exception {
        Socks socks = new Socks();
        socks.setId(1L);
        socks.setQuantity(200);

        Mockito.doThrow(new IllegalArgumentException("Not enough socks in stock"))
                .when(socksService).removeSocks(any(Socks.class));

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socks)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough socks in stock"));
    }
    @Test
    public void testUpdateSocks() throws Exception {
        Socks updatedSocks = new Socks();
        updatedSocks.setId(1L);
        updatedSocks.setColor("blue");
        updatedSocks.setCottonPart(70);
        updatedSocks.setQuantity(150);

        when(socksService.updateSocks(eq(1L), any(Socks.class))).thenReturn(updatedSocks);

        mockMvc.perform(put("/api/socks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSocks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("blue"))
                .andExpect(jsonPath("$.cottonPart").value(70))
                .andExpect(jsonPath("$.quantity").value(150));
    }
    @Test
    public void testUploadSocksBatch() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "socks.csv",
                "text/csv",
                "color,cottonPart,quantity\nred,50,100".getBytes()
        );

        Mockito.doNothing().when(socksBatchService).processSocksBatch(any());

        mockMvc.perform(multipart("/api/socks/batch").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File processed successfully"));
    }

    @Test
    public void testUploadSocksBatchError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid.csv",
                "text/csv",
                "invalid data".getBytes()
        );

        Mockito.doThrow(new RuntimeException("Error processing file"))
                .when(socksBatchService).processSocksBatch(any());

        mockMvc.perform(multipart("/api/socks/batch").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error processing file: Error processing file"));
    }

    @Test
    public void testRegisterOutcomeSocksNotFound() throws Exception {
        Socks socks = new Socks();
        socks.setId(1L);
        socks.setQuantity(30);

        Mockito.doThrow(new IllegalArgumentException("Socks not found"))
                .when(socksService).removeSocks(any(Socks.class));

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(socks)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Socks not found"));
    }



}
