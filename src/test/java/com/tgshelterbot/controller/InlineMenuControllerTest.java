package com.tgshelterbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgshelterbot.mapper.MapperDTO;
import com.tgshelterbot.model.InlineMenu;
import com.tgshelterbot.model.UserState;
import com.tgshelterbot.model.UserStateSpecial;
import com.tgshelterbot.repository.InlineMenuRepository;
import com.tgshelterbot.repository.UserStateRepository;
import com.tgshelterbot.service.impl.InlineMenuServiceImpl;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InlineMenuController.class)
class InlineMenuControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private InlineMenuRepository repository;
    @MockBean
    private UserStateRepository stateRepository;
    @SpyBean
    private InlineMenuServiceImpl service;
    @SpyBean
    private MapperDTO mapperDTO;
    @InjectMocks
    private InlineMenuController controller;
    private final String URL = "/bot/inlineMenu";
    private final Long ID = 1L;
    private final String SLASH = "/";
    private ObjectMapper mapper;
    private UserState userStateOne;
    private UserState userStateTwo;
    private UserState userStateMinus;
    private String json;
    private InlineMenu inlineMenu;

    @BeforeEach
    void init() throws JsonProcessingException, JSONException {
        inlineMenu = new InlineMenu();
        userStateOne = new UserState(1L, "name", UserStateSpecial.REPORT, ID);
        userStateTwo = new UserState(2L, "name", UserStateSpecial.REPORT, ID);
        userStateMinus = new UserState(-2L, "name", UserStateSpecial.REPORT, ID);
        mapper = new ObjectMapper();
        inlineMenu.setId(ID);
        inlineMenu.setShelterId(ID);
        inlineMenu.setLanguageId(ID);
        inlineMenu.setTagCallback("query");
        inlineMenu.setQuestion("question");
        inlineMenu.setAnswer("answer");
        inlineMenu.setButton("button");
        inlineMenu.setStateId(userStateOne);
        inlineMenu.setStateIdNext(userStateOne);
        inlineMenu.setPriority(0);
        json = mapper.writeValueAsString(inlineMenu);
    }

    @Test
    void addInlineMenu_ShouldReturnInlineMenuPOJO_CorrectResponseStatuses() throws Exception {
        when(repository.save(any(InlineMenu.class))).thenReturn(inlineMenu);
        when(stateRepository.findAll()).thenReturn(List.of(userStateOne));
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
        when(stateRepository.findAll()).thenReturn(List.of(userStateTwo));
        json = objectMapper.writeValueAsString(inlineMenu);
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotAcceptable());
        when(stateRepository.findAll()).thenReturn(List.of(userStateMinus));
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotAcceptable());
        when(stateRepository.findAll()).thenReturn(List.of(userStateOne));
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(inlineMenu));
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    void getAllInlineMenu_ShouldReturnCollectionOfPOJO_OkStatus() throws Exception {
        json = objectMapper.writeValueAsString(List.of(inlineMenu));
        when(repository.findAll()).thenReturn(List.of(inlineMenu));
        mockMvc.perform(get(URL).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getInlineMenu_shouldReturnPOJO_OkOrNotFound() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(inlineMenu));
        mockMvc.perform(get(URL + SLASH + ID).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + SLASH + ID).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void updateInlineMenu_shouldReturnPOJO_CorrectStatuses() throws Exception {
        when(stateRepository.findAll()).thenReturn(List.of(userStateOne));
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(inlineMenu));
        when(repository.save(any(InlineMenu.class))).thenReturn(inlineMenu);
        mockMvc.perform(put(URL).content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        when(stateRepository.findAll()).thenReturn(List.of(userStateTwo));
        mockMvc.perform(put(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotAcceptable());
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        when(stateRepository.findAll()).thenReturn(List.of(userStateOne));
        mockMvc.perform(put(URL).content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void deleteInlineMenu_ShouldReturnOk_NotFound() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(inlineMenu));
        doNothing().when(repository).deleteById(anyLong());
        mockMvc.perform(delete(URL + SLASH + ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(json))
                .andExpect(status().isOk());
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(delete(URL + SLASH + ID).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}