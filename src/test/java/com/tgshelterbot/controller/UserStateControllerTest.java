package com.tgshelterbot.controller;

import com.tgshelterbot.dto.mapper.DtoMapper;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.impl.UserStateService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserStateController.class)
class UserStateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserStateRepository stateRepository;
    @SpyBean
    private UserStateService stateService;
    @SpyBean
    DtoMapper dtoMapper;
    @InjectMocks
    UserStateController controller;
    private UserState userState;
    private JSONObject userStateObject;
    private final String URL = "/bot/userState";
    private final Long ID = 1L;
    private final String NAME = "Главное меню";
    private final String PARAM = "?id={x}";

    @BeforeEach
    void init () throws JSONException {
        userStateObject = new JSONObject();
        userStateObject.put("id", ID);
        userStateObject.put("name", NAME);
        userStateObject.put("shelterId", ID);
        userState = new UserState();
        userState.setId(ID);
        userState.setName(NAME);
        userState.setShelterId(ID);
    }

    @Test
    void createOrUpdateUser_ShouldReturnUserStatePOJO () throws Exception {
        when(stateRepository.save(any(UserState.class))).thenReturn(userState);
        mockMvc.perform(post(URL).content(userStateObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.stateSpecial").value((Object) null))
                .andExpect(jsonPath("$.shelterId").value(ID));
    }

    @Test
    void deleteUserState_ShouldReturnOk () throws Exception {
        doNothing().when(stateRepository).deleteById(anyLong());
        mockMvc.perform(delete(URL + PARAM, ID.toString()).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getAllUserState_ShouldReturnListOfPOJO () throws Exception {
        when(stateRepository.findAll()).thenReturn(List.of(userState));
        mockMvc.perform(get(URL + "/all").content(userStateObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ID))
                .andExpect(jsonPath("$[0].name").value(NAME))
                .andExpect(jsonPath("$[0].stateSpecial").value((Object) null))
                .andExpect(jsonPath("$[0].shelterId").value(ID));
    }

    @Test
    void getUserStateById_ShouldReturnPOJO () throws Exception {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userState));
        mockMvc.perform(get(URL + PARAM, ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.stateSpecial").value((Object) null))
                .andExpect(jsonPath("$.shelterId").value(ID));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + PARAM, ID).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}