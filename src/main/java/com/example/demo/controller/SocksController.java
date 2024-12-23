package com.example.demo.controller;
import com.example.demo.model.FilterOperation;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
public class SocksController {
    private static final Logger logger = LoggerFactory.getLogger(SocksController.class);

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



    @PutMapping("/{id}")
    public ResponseEntity<Socks> updateSocks(
            @PathVariable Long id,
            @RequestBody @Valid Socks updatedSocks) {
        return ResponseEntity.ok(socksService.updateSocks(id, updatedSocks));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Socks>> filterSocks(
            @RequestParam String color,
            @RequestParam String operation,
            @RequestParam int cottonPart) {

        List<Socks> socksList;

        switch (operation) {
            case "moreThan":
                socksList = socksService.getSocksByColorAndCottonPartGreaterThan(color, cottonPart);
                break;
            case "lessThan":
                socksList = socksService.getSocksByColorAndCottonPartLessThan(color, cottonPart);
                break;
            case "equal":
                socksList = socksService.getSocksByColorAndCottonPartEqual(color, cottonPart);
                break;
            default:
                throw new IllegalArgumentException("Unsupported filter operation: " + operation);
        }

        return ResponseEntity.ok(socksList);
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
    public ResponseEntity<String> uploadSocksBatch(@RequestPart("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                logger.error("Uploaded file is empty");
                return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
            }

            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                logger.error("Invalid file format: {}", file.getOriginalFilename());
                return ResponseEntity.badRequest().body("Invalid file format. Please upload a .xlsx file.");
            }

            socksBatchService.processSocksBatch(file);
            logger.info("File processed successfully");
            return ResponseEntity.ok("File processed successfully");

        } catch (IOException e) {
            logger.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        }
    }
}
