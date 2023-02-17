package com.tgshelterbot.controller;

import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.service.ShelterService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/shelter")
@AllArgsConstructor
public class ShelterController {
    private final ShelterService service;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get all Shelter",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class)
                    )
            )
    })
    @GetMapping
    public List<ShelterDto> getAll() {
        return service.getAll();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Create Shelter",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Shelter not found"
            )
    })
    @PostMapping
    public ResponseEntity<ShelterDto> create(@Valid @RequestBody ShelterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Find Shelter by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Shelter not found"
            )
    })
    @GetMapping("/{id}")
    public ShelterDto read(@PathVariable Long id) {
        return service.read(id);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Update Shelter by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Shelter not found"
            )
    })
    @PutMapping("/{id}")
    public ShelterDto update(@PathVariable Long id, @Valid @RequestBody ShelterDto dto) {
        return service.update(id, dto);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete Shelter by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Shelter not found"
            )
    })
    @DeleteMapping("/{id}")
    public ShelterDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
