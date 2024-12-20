package com.example.demo.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.service.SocksBatchService;
import com.example.demo.service.SocksService;
import com.example.demo.model.Socks;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
public class SocksController {

    private final SocksService socksService;
    private final SocksBatchService socksBatchService;

    @PostMapping("/income")
    public ResponseEntity<Socks> registerIncome(@RequestBody @Valid Socks socks) {
        return ResponseEntity.ok(socksService.addSocks(socks));
    }

    @PostMapping("/outcome")
    public ResponseEntity<String> registerOutcome(@RequestBody Socks socks) {
        socksService.removeSocks(socks);
        return ResponseEntity.ok("Socks removed successfully");
    }

    @GetMapping
    public ResponseEntity<List<Socks>> getAllSocks() {
        return ResponseEntity.ok(socksService.getAllSocks());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Socks>> filterSocks(
            @RequestParam String color,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer cottonPart,
            @RequestParam(required = false) Integer minCottonPart,
            @RequestParam(required = false) Integer maxCottonPart,
            @RequestParam(required = false, defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(socksService.getFilteredSocks(color, operation, cottonPart, minCottonPart, maxCottonPart, sortBy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Socks> updateSocks(
            @PathVariable Long id,
            @RequestBody @Valid Socks updatedSocks) {
        return ResponseEntity.ok(socksService.updateSocks(id, updatedSocks));
    }

    @PostMapping("/batch")
    @Operation(
            summary = "Upload a batch of socks",
            description = "Upload an Excel file containing socks data in .xlsx format.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary", description = "The Excel file to upload")
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or error occurred during processing"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
            }

            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Invalid file format. Please upload a .xlsx file.");
            }

            socksBatchService.processSocksBatch(file);
            return ResponseEntity.ok("File processed successfully");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }
}