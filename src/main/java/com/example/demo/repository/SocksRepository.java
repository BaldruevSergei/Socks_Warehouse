package com.example.demo.repository;

import com.example.demo.model.Socks;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocksRepository extends JpaRepository<Socks, Long> {
    List<Socks> findByColorAndCottonPartGreaterThan(String color, int cottonPart);
    List<Socks> findByColorAndCottonPartLessThan(String color, int cottonPart);
    List<Socks> findByColorAndCottonPart(String color, int cottonPart);

    // Метод для диапазона с поддержкой сортировки
    List<Socks> findByColorAndCottonPartBetween(String color, int minCottonPart, int maxCottonPart, Sort sort);
}
