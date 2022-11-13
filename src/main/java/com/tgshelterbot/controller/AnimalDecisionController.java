package com.tgshelterbot.controller;

import com.tgshelterbot.model.Animal;
import com.tgshelterbot.model.dto.AnimalDto;
import com.tgshelterbot.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/bot/resolver")
@RestController
@RequiredArgsConstructor
public class AnimalDecisionController {
    private final AnimalService animalService;

    @Operation(summary = "Get all animals by status in the shelter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Get all animals that is:\n in test period or\n in shelter\n or obtain their new " +
                            "home ",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class)))
    })
    @GetMapping
    public Collection<AnimalDto> getAllStatedAnimal(@RequestParam(value = "state") Animal.AnimalStateEnum stateEnum) {
        return animalService.findAllBySateInTest(stateEnum);
    }

    @Operation(summary = "Approve a pet for its new landlord or extend a test timestamp.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Performed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class))), @ApiResponse(responseCode = "404",
            description = "Animal not found.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AnimalDto.class)))
    })
    @PutMapping("/{id}/extend_or_approve")
    public AnimalDto extendTestTimeFrame(@PathVariable Long id, @RequestParam Animal.TimeFrame timeFrame) {
        return animalService.extendPeriod(id, timeFrame);
    }
}
