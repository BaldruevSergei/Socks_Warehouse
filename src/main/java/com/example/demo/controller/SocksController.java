package com.example.demo.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.model.Socks;
import com.example.demo.service.SocksBatchService;
import com.example.demo.service.SocksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            @RequestParam String operation,
            @RequestParam int cottonPart) {
        return ResponseEntity.ok(socksService.getFilteredSocks(color, operation, cottonPart));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Socks> updateSocks(
            @PathVariable Long id,
            @RequestBody @Valid Socks updatedSocks) {
        return ResponseEntity.ok(socksService.updateSocks(id, updatedSocks));
    }

    @PostMapping("/batch")
    @Operation(
            summary = "Upload a socks batch file",
            description = "Upload an Excel file containing socks data.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully"),
            @ApiResponse(responseCode = "400", description = "Error processing file")
    })
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file) {
        try {
            socksBatchService.processSocksBatch(file);
            return ResponseEntity.ok("File processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }
}





