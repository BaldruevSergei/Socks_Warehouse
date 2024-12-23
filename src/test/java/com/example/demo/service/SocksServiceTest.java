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
    public void shouldAddSocksSuccessfully() {
        Socks socks = new Socks(null, "red", 50, 100);

        when(socksRepository.save(socks)).thenReturn(socks);

        Socks result = socksService.addSocks(socks);

        assertNotNull(result);
        assertEquals("red", result.getColor());
        assertEquals(50, result.getCottonPart());
        assertEquals(100, result.getQuantity());
        verify(socksRepository, times(1)).save(socks);
    }

    @Test
    public void shouldRemoveSocksWhenSufficientQuantity() {
        Socks existingSocks = new Socks(1L, "blue", 70, 50);
        Socks requestSocks = new Socks(1L, null, 0, 30);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));

        socksService.removeSocks(requestSocks);

        assertEquals(20, existingSocks.getQuantity());
        verify(socksRepository, times(1)).save(existingSocks);
    }

    @Test
    public void shouldThrowExceptionWhenNotEnoughQuantity() {
        Socks existingSocks = new Socks(1L, "blue", 70, 20);
        Socks requestSocks = new Socks(1L, null, 0, 30);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            socksService.removeSocks(requestSocks);
        });

        assertEquals("Not enough socks in stock", exception.getMessage());
        verify(socksRepository, never()).save(existingSocks);
    }

    @Test
    public void shouldThrowExceptionWhenSocksNotFound() {
        Socks requestSocks = new Socks(1L, null, 0, 30);

        when(socksRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            socksService.removeSocks(requestSocks);
        });

        assertEquals("Socks not found", exception.getMessage());
        verify(socksRepository, never()).save(any());
    }

    @Test
    public void shouldReturnFilteredSocksWhenCottonPartMoreThan() {
        List<Socks> mockSocks = List.of(
                new Socks(1L, "red", 60, 50),
                new Socks(2L, "red", 80, 30)
        );

        when(socksRepository.findByColorAndCottonPartGreaterThan(eq("red"), eq(50)))
                .thenReturn(mockSocks);

        List<Socks> result = socksService.getSocksByColorAndCottonPartGreaterThan("red", 50);

        assertEquals(2, result.size());
        verify(socksRepository, times(1)).findByColorAndCottonPartGreaterThan(eq("red"), eq(50));
    }

    @Test
    public void shouldUpdateSocksSuccessfully() {
        Socks existingSocks = new Socks(1L, "red", 50, 100);
        Socks updatedSocks = new Socks(null, "blue", 70, 150);

        when(socksRepository.findById(1L)).thenReturn(Optional.of(existingSocks));
        when(socksRepository.save(any(Socks.class))).thenReturn(updatedSocks);

        Socks result = socksService.updateSocks(1L, updatedSocks);

        assertEquals("blue", result.getColor());
        assertEquals(70, result.getCottonPart());
        assertEquals(150, result.getQuantity());
        verify(socksRepository, times(1)).save(existingSocks);
    }
}
