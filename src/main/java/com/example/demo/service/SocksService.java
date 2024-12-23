package com.example.demo.service;

import com.example.demo.model.FilterOperation;
import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocksService {

    private final SocksRepository socksRepository;

    public Socks addSocks(Socks socks) {
        return socksRepository.save(socks);
    }
    public void processSocksBatch(MultipartFile file) {
        // Логика обработки файла
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        // Пример: просто логируем имя файла
        System.out.println("Processing file: " + file.getOriginalFilename());
    }
    public void removeSocks(Socks socks) {
        if (socks.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Socks existing = socksRepository.findById(socks.getId())
                .orElseThrow(() -> new IllegalArgumentException("Socks not found"));
        if (existing.getQuantity() < socks.getQuantity()) {
            throw new IllegalArgumentException("Not enough socks in stock");
        }
        existing.setQuantity(existing.getQuantity() - socks.getQuantity());
        socksRepository.save(existing);
    }

    public List<Socks> getAllSocks() {
        return socksRepository.findAll();
    }

    public Socks updateSocks(Long id, Socks updatedSocks) {
        Socks existingSocks = socksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Socks with ID " + id + " not found"));

        existingSocks.setColor(updatedSocks.getColor());
        existingSocks.setCottonPart(updatedSocks.getCottonPart());
        existingSocks.setQuantity(updatedSocks.getQuantity());

        return socksRepository.save(existingSocks);
    }
    public List<Socks> getSocksByColorAndCottonPartGreaterThan(String color, int cottonPart) {
        return socksRepository.findByColorAndCottonPartGreaterThan(color, cottonPart);
    }

    public List<Socks> getSocksByColorAndCottonPartLessThan(String color, int cottonPart) {
        return socksRepository.findByColorAndCottonPartLessThan(color, cottonPart);
    }

    public List<Socks> getSocksByColorAndCottonPartEqual(String color, int cottonPart) {
        return socksRepository.findByColorAndCottonPart(color, cottonPart);
    }


}
