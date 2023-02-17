package com.tgshelterbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgshelterbot.mapper.UserStateMapper;
import com.tgshelterbot.mapper.UserStateMapperImpl;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.impl.UserStateServiceImpl;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserStateController.class)
class UserStateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserStateRepository stateRepository;
    @SpyBean
    private UserStateServiceImpl stateService;
    @SpyBean
    UserStateMapperImpl dtoMapper;
    private UserState userState;
    @InjectMocks
    private UserStateController controller;
    private final String URL = "/bot/userState";
    private final Long ID = 1L;
    private final String SLASH = "/";
    private String json;

    @BeforeEach
    void init() throws JsonProcessingException {
        userState = new UserState();
        userState.setId(ID);
        userState.setName("NAME");
        userState.setTagSpecial(null);
        userState.setShelterId(ID);
        json = objectMapper.writeValueAsString(userState);
    }

    @Test
    void create_ShouldReturnUserStatePOJO_AndRightResponseStatuses() throws Exception {
        when(stateRepository.save(any(UserState.class))).thenReturn(userState);
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        userState.setId(null);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userState));
        userState.setId(1L);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setTagSpecial(UserStateSpecial.SELECT_SHELTER);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setName(null);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setShelterId(-4L);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void update_ShouldReturnUserStatePOJO_AndRightResponseStatuses() throws Exception {
        when(stateRepository.save(any(UserState.class))).thenReturn(userState);
        when(stateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userState));
        mockMvc.perform(put(URL).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(put(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setTagSpecial(UserStateSpecial.REPORT);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(put(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setName(null);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(put(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
        userState.setShelterId(-4L);
        json = objectMapper.writeValueAsString(userState);
        mockMvc.perform(post(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void getAllUserState_ShouldReturnListOfPOJO() throws Exception {
        when(stateRepository.findAll()).thenReturn(List.of(userState));
        List<UserState> states = List.of(userState);
        json = objectMapper.writeValueAsString(states);
        mockMvc.perform(get(URL).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getUserStateById_ShouldReturnUserStatePOJO_NotFoundStatus() throws Exception {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userState));
        mockMvc.perform(get(URL + SLASH + ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + "/" + ID).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void deleteUserState_ShouldReturnOk_NotFound() throws Exception {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(userState));
        doNothing().when(stateRepository).deleteById(anyLong());
        mockMvc.perform(delete(URL + SLASH + ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(delete(URL + SLASH + ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}