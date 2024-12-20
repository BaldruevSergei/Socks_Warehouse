package com.example.demo.service;


import com.example.demo.model.FilterOperation;
import com.example.demo.model.Socks;
import com.example.demo.repository.SocksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocksService {

    private final SocksRepository socksRepository;

    public Socks addSocks(Socks socks) {
        return socksRepository.save(socks);
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
    public enum Operation {
        MORE_THAN, LESS_THAN, EQUAL
    }
    public Socks updateSocks(Long id, Socks updatedSocks) {
        Socks existingSocks = socksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Socks with ID " + id + " not found"));

        existingSocks.setColor(updatedSocks.getColor());
        existingSocks.setCottonPart(updatedSocks.getCottonPart());
        existingSocks.setQuantity(updatedSocks.getQuantity());

        return socksRepository.save(existingSocks);
    }


    public List<Socks> getFilteredSocks(String color, String operation, int cottonPart) {
        FilterOperation filterOperation = FilterOperation.fromString(operation);

        switch (filterOperation) {
            case MORE_THAN:
                return socksRepository.findByColorAndCottonPartGreaterThan(color, cottonPart);
            case LESS_THAN:
                return socksRepository.findByColorAndCottonPartLessThan(color, cottonPart);
            case EQUAL:
                return socksRepository.findByColorAndCottonPart(color, cottonPart);
            default:
                throw new IllegalArgumentException("Unsupported filter operation: " + operation);
        }
    }

}