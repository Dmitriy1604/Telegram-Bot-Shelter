package com.tgshelterbot.controller;

import com.tgshelterbot.model.dto.InlineMenuDto;
import com.tgshelterbot.model.dto.UserStateDto;
import com.tgshelterbot.service.InlineMenuService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot/inlineMenu")
public class InlineMenuController {

    private final InlineMenuService service;

    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "UserState was created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InlineMenuDto.class))),
            @ApiResponse(responseCode = "404", description = "Menu doesn't exist"),
            @ApiResponse(responseCode = "403", description = "Inline menu already exist"),
            @ApiResponse(responseCode = "406", description = "Arguments is not acceptable")
    })
    @PostMapping
    public ResponseEntity<InlineMenuDto> addInlineMenu(@RequestBody InlineMenuDto inlineMenuDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveInlineMenu(inlineMenuDto));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Collection of InlineMenu will be returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class))),
    })
    @GetMapping
    public Collection<InlineMenuDto> getAllInlineMenu() {
        return service.getAllInlineMenu();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Inline menu will be returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Inline menu doesn't exist"),
    })
    @GetMapping("/{id}")
    public InlineMenuDto getInlineMenu(@PathVariable Long id) {
        return service.getInlineMenu(id);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Inline menu will be updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Inline menu not found"),
            @ApiResponse(responseCode = "406", description = "Arguments is not acceptable")
    })
    @PutMapping
    public InlineMenuDto updateInlineMenu(@RequestBody InlineMenuDto inlineMenuDto) {
        return service.updateInlineMenu(inlineMenuDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inline menu will be removed"),
            @ApiResponse(responseCode = "404", description = "Inline menu doesn't exist")
    })
    @DeleteMapping("/{id}")
    public InlineMenuDto deleteInlineMenu(@PathVariable Long id) {
        return service.deleteInlineMenu(id);
    }
}
