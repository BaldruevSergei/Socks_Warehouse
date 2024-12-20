package com.example.demo.service;

import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SocksServiceTest {

    @Mock
    private SocksRepository socksRepository;

    @InjectMocks
    private SocksService socksService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddSocks() {
        Socks socks = new Socks();
        socks.setColor("red");
        socks.setCottonPart(50);
        socks.setQuantity(100);

        when(socksRepository.save(socks)).thenReturn(socks);

        Socks result = socksService.addSocks(socks);

        assertNotNull(result);
        assertEquals("red", result.getColor());
        assertEquals(50, result.getCottonPart());
        assertEquals(100, result.getQuantity());
        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void testRemoveSocksSuccess() {
        Socks existingSocks = new Socks();
        existingSocks.setId(1L);
        existingSocks.setColor("blue");
        existingSocks.setCottonPart(70);
        existingSocks.setQuantity(50);

        Socks requestSocks = new Socks();
        requestSocks.setId(1L);
        requestSocks.setQuantity(30);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));

        socksService.removeSocks(requestSocks);

        assertEquals(20, existingSocks.getQuantity());
        verify(socksRepository, times(1)).save(existingSocks);
    }

    @Test
    public void testRemoveSocksNotEnoughQuantity() {
        Socks existingSocks = new Socks();
        existingSocks.setId(1L);
        existingSocks.setColor("blue");
        existingSocks.setCottonPart(70);
        existingSocks.setQuantity(20);

        Socks requestSocks = new Socks();
        requestSocks.setId(1L);
        requestSocks.setQuantity(30);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            socksService.removeSocks(requestSocks);
        });

        assertEquals("Not enough socks in stock", exception.getMessage());
        verify(socksRepository, never()).save(existingSocks);
    }

    @Test
    public void testRemoveSocksNotFound() {
        Socks requestSocks = new Socks();
        requestSocks.setId(1L);
        requestSocks.setQuantity(30);

        when(socksRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            socksService.removeSocks(requestSocks);
        });

        assertEquals("Socks not found", exception.getMessage());
        verify(socksRepository, never()).save(any());
    }
    @Test
    public void testGetFilteredSocksMoreThan() {
        Socks socks1 = new Socks();
        socks1.setColor("red");
        socks1.setCottonPart(60);
        socks1.setQuantity(50);

        Socks socks2 = new Socks();
        socks2.setColor("red");
        socks2.setCottonPart(80);
        socks2.setQuantity(30);

        when(socksRepository.findByColorAndCottonPartGreaterThan("red", 50))
                .thenReturn(List.of(socks1, socks2));

        List<Socks> result = socksService.getFilteredSocks("red", "more_than", 50);

        assertEquals(2, result.size());
        verify(socksRepository, times(1)).findByColorAndCottonPartGreaterThan("red", 50);
    }




    @Test
    public void testGetFilteredSocksEqual() {
        Socks socks = new Socks();
        socks.setColor("green");
        socks.setCottonPart(50);
        socks.setQuantity(40);

        when(socksRepository.findByColorAndCottonPart("green", 50))
                .thenReturn(List.of(socks));

        List<Socks> result = socksService.getFilteredSocks("green", "equal", 50);

        assertEquals(1, result.size());
        verify(socksRepository, times(1)).findByColorAndCottonPart("green", 50);
    }
    @Test
    public void testUpdateSocks() {
        Socks existingSocks = new Socks();
        existingSocks.setId(1L);
        existingSocks.setColor("red");
        existingSocks.setCottonPart(50);
        existingSocks.setQuantity(100);

        Socks updatedSocks = new Socks();
        updatedSocks.setColor("blue");
        updatedSocks.setCottonPart(70);
        updatedSocks.setQuantity(150);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));
        when(socksRepository.save(any(Socks.class))).thenReturn(updatedSocks);

        Socks result = socksService.updateSocks(1L, updatedSocks);

        assertEquals("blue", result.getColor());
        assertEquals(70, result.getCottonPart());
        assertEquals(150, result.getQuantity());
        verify(socksRepository, times(1)).save(existingSocks);
    }


}
