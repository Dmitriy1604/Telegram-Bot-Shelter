package com.tgshelterbot.controller;

import com.tgshelterbot.model.dto.UserDtoCrud;
import com.tgshelterbot.model.dto.UserDtoCrudSerialized;
import com.tgshelterbot.service.UserCRUDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot/user")
public class UserController {
    private final UserCRUDService crudService;

    @Operation(summary = "Find all users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Collection of Users will be returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class))),
    })
    @GetMapping
    public Collection<UserDtoCrudSerialized> getAllUsers() {
        return crudService.getAllUsers();
    }

    @Operation(summary = "Find user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "User will be returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Collection.class))),
    })
    @GetMapping("/{id}")
    public UserDtoCrudSerialized getUserById(@PathVariable Long id) {
        return crudService.getUser(id);
    }

    @Operation(summary = "Update user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Performed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDtoCrudSerialized.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDtoCrudSerialized.class)))
    })
    @PutMapping
    public UserDtoCrudSerialized updateUser(@RequestBody UserDtoCrud userDto) {
        return crudService.updateUser(userDto);
    }

    @Operation(summary = "Remove user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Performed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDtoCrudSerialized.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDtoCrudSerialized.class)))
    })
    @DeleteMapping("{id}")
    public UserDtoCrudSerialized removeUser(@PathVariable Long id) {
        return crudService.deleteUser(id);
    }
}
