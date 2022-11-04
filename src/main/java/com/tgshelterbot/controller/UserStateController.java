package com.tgshelterbot.controller;

import com.tgshelterbot.dto.UserStateDto;
import com.tgshelterbot.service.impl.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot/userState")
public class UserStateController {
    private final UserStateService stateService;

    @PostMapping
    public ResponseEntity<UserStateDto> createOrUpdateUser (@RequestBody UserStateDto userState) {
        return ResponseEntity.ok(stateService.saveOrUpdate(userState));
    }

    @DeleteMapping
    public void deleteUserState (@RequestParam(value = "id") Long id) {
        stateService.removeUserState(id);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<UserStateDto>> getAll () {
        return ResponseEntity.ok(stateService.getAllUserStates());
    }

    @GetMapping
    public ResponseEntity<UserStateDto> getUserStateById (@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(stateService.getUserState(id));
    }
}
