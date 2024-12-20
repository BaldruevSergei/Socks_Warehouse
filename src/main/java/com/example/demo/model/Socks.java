package com.example.demo.model;
import javax.persistence.*;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Data
@Entity
public class Socks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Color must not be blank")
    @Size(max = 50, message = "Color must be less than 50 characters")
    private String color;

    @Column(nullable = false)
    @Min(value = 0, message = "Cotton part must be at least 0")
    @Max(value = 100, message = "Cotton part must be at most 100")
    private int cottonPart;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}
