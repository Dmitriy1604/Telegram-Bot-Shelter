package com.tgshelterbot.controller;

import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.service.AnimalService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/animal")
@AllArgsConstructor
public class AnimalController {
    private final AnimalService service;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get all animals",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class)
                    )
            )
    })
    @GetMapping
    public List<AnimalDto> getAll() {
        return service.getAll();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Create animal",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "animal not found"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalDto create(@Valid @RequestBody AnimalDto dto) {
        return service.create(dto);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Find animal by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "animal not found"
            )
    })
    @GetMapping("/{id}")
    public AnimalDto read(@PathVariable Long id) {
        return service.read(id);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Update animal by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "animal not found"
            )
    })
    @PutMapping("/{id}")
    public AnimalDto update(@PathVariable Long id, @Valid @RequestBody AnimalDto dto) {
        return service.update(id, dto);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete animal by id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "animal not found"
            )
    })
    @DeleteMapping("/{id}")
    public AnimalDto delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
