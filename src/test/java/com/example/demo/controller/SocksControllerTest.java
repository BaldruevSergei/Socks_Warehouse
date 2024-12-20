package com.example.demo.controller;

import com.example.demo.model.Socks;
import com.example.demo.service.SocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SocksControllerTest {

    @Mock
    private SocksService socksService;

    @InjectMocks
    private SocksController socksController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(socksController).build();
    }

    @Test
    public void testGetFilteredSocksMoreThan() throws Exception {
        Socks socks1 = new Socks();
        socks1.setColor("red");
        socks1.setCottonPart(60);
        socks1.setQuantity(50);

        Socks socks2 = new Socks();
        socks2.setColor("red");
        socks2.setCottonPart(80);
        socks2.setQuantity(30);

        when(socksService.getFilteredSocks(eq("red"), eq("more_than"), eq(50), eq(null), eq(null), eq("color")))
                .thenReturn(List.of(socks1, socks2));

        mockMvc.perform(get("/api/socks/filter")
                        .param("color", "red")
                        .param("operation", "more_than")
                        .param("cottonPart", "50")
                        .param("sortBy", "color")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[0].cottonPart").value(60))
                .andExpect(jsonPath("$[1].color").value("red"))
                .andExpect(jsonPath("$[1].cottonPart").value(80));

        verify(socksService, times(1)).getFilteredSocks(eq("red"), eq("more_than"), eq(50), eq(null), eq(null), eq("color"));
    }

    @Test
    public void testAddSocks() throws Exception {
        Socks socks = new Socks();
        socks.setColor("blue");
        socks.setCottonPart(40);
        socks.setQuantity(100);

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
    public void testRemoveSocks() throws Exception {
        doNothing().when(socksService).removeSocks(any(Socks.class));

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\": \"red\", \"cottonPart\": 50, \"quantity\": 20}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks removed successfully"));

        verify(socksService, times(1)).removeSocks(any(Socks.class));
    }

    @Test
    public void testUpdateSocks() throws Exception {
        Socks updatedSocks = new Socks();
        updatedSocks.setId(1L);
        updatedSocks.setColor("green");
        updatedSocks.setCottonPart(60);
        updatedSocks.setQuantity(150);

        when(socksService.updateSocks(eq(1L), any(Socks.class))).thenReturn(updatedSocks);

        mockMvc.perform(put("/api/socks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\": \"green\", \"cottonPart\": 60, \"quantity\": 150}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("green"))
                .andExpect(jsonPath("$.cottonPart").value(60))
                .andExpect(jsonPath("$.quantity").value(150));

        verify(socksService, times(1)).updateSocks(eq(1L), any(Socks.class));
    }

    @Test
    public void testUploadSocksBatch() throws Exception {
        doNothing().when(socksService).processSocksBatch(any());

        mockMvc.perform(multipart("/api/socks/batch")
                        .file("file", "test data".getBytes()))
                .andExpect(status().isOk())
                .andExpect(content().string("File processed successfully"));

        verify(socksService, times(1)).processSocksBatch(any());
    }
}
