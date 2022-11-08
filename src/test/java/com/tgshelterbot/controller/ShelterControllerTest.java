package com.tgshelterbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgshelterbot.mapper.ShelterMapperImpl;
import com.tgshelterbot.model.Shelter;
import com.tgshelterbot.model.dto.ShelterDto;
import com.tgshelterbot.repository.ShelterRepository;
import com.tgshelterbot.service.impl.ShelterServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShelterController.class)
class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShelterRepository shelterRepository;

    @SpyBean
    private ShelterServiceImpl shelterService;
    @SpyBean
    private ShelterMapperImpl shelterMapper;

    @InjectMocks
    private ShelterController shelterController;

    private static final String URL = "/shelter/";
    private static final Long ID = 1L;
    private static final String NAME = "test";

    @Test
    void getAll() throws Exception{
        List<Shelter> shelterList = new ArrayList<>();
        Shelter shelter2 = new Shelter();
        shelter2.setId(2L);
        shelter2.setName("2");
        shelterList.add(getShelter());
        shelterList.add(shelter2);
        String json = objectMapper.writeValueAsString(shelterList);

        Mockito.when(shelterRepository.findAll()).thenReturn(shelterList);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }


    @Test
    void create() throws Exception {
        ShelterDto shelterDto = getShelterDto();
        String json = objectMapper.writeValueAsString(shelterDto);

        Mockito.when(shelterRepository.save(any(Shelter.class))).thenReturn(getShelter());

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json));

    }

    @Test
    void read() throws Exception {
        ShelterDto shelterDto = getShelterDto();
        String json = objectMapper.writeValueAsString(shelterDto);

        Mockito.when(shelterRepository.findById(any(Long.class))).thenReturn(Optional.of(getShelter()));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }


    @Test
    void delete() throws Exception {
        ShelterDto shelterDto = getShelterDto();
        String json = objectMapper.writeValueAsString(shelterDto);
        Mockito.when(shelterRepository.findById(any(Long.class))).thenReturn(Optional.of(getShelter()));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + ID
                ))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(json));
    }

    private ShelterDto getShelterDto() {
        return new ShelterDto(ID, NAME);
    }

    private Shelter getShelter() {
        Shelter shelter = new Shelter();
        shelter.setId(ID);
        shelter.setName(NAME);
        return shelter;
    }

}